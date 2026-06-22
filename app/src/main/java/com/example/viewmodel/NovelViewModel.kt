package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Novel
import com.example.data.model.Character
import com.example.data.model.Event
import com.example.data.model.Dialogue
import com.example.data.repository.NovelRepository
import com.example.service.GeminiService
import com.example.service.VoiceActingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NovelViewModel(
    application: Application,
    private val repository: NovelRepository,
    private val geminiService: GeminiService,
    private val voiceService: VoiceActingService
) : AndroidViewModel(application) {

    // App Settings: Language and Theme (Dynamic)
    private val _isDarkMode = MutableStateFlow(true) // Default to nice Cinema dark theme
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isArabic = MutableStateFlow(false) // Default to English UI, user can switch to Arabic
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }

    fun setLanguage(arabic: Boolean) {
        _isArabic.value = arabic
    }

    // List of all novels (reactive DB)
    val novels: StateFlow<List<Novel>> = repository.allNovels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Selected Novel
    private val _selectedNovelId = MutableStateFlow<Int?>(null)
    val selectedNovelId: StateFlow<Int?> = _selectedNovelId.asStateFlow()

    val currentNovel: StateFlow<Novel?> = _selectedNovelId
        .flatMapLatest { id ->
            if (id != null) repository.getNovelByIdFlow(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current characters, events, and dialogues for the selected novel
    val currentCharacters: StateFlow<List<Character>> = _selectedNovelId
        .flatMapLatest { id ->
            if (id != null) repository.getCharactersForNovel(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentEvents: StateFlow<List<Event>> = _selectedNovelId
        .flatMapLatest { id ->
            if (id != null) repository.getEventsForNovel(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentDialogues: StateFlow<List<Dialogue>> = _selectedNovelId
        .flatMapLatest { id ->
            if (id != null) repository.getDialoguesForNovel(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Analysis Status Tracker
    private val _analysisStatus = MutableStateFlow<AnalysisStatus>(AnalysisStatus.Idle)
    val analysisStatus: StateFlow<AnalysisStatus> = _analysisStatus.asStateFlow()

    // Interactive Cinema Player State
    private val _cinemaPlaying = MutableStateFlow(false)
    val cinemaPlaying: StateFlow<Boolean> = _cinemaPlaying.asStateFlow()

    private val _currentCinemaEventIndex = MutableStateFlow(0)
    val currentCinemaEventIndex: StateFlow<Int> = _currentCinemaEventIndex.asStateFlow()

    private val _activeSpeakerName = MutableStateFlow<String?>(null)
    val activeSpeakerName: StateFlow<String?> = _activeSpeakerName.asStateFlow()

    private val _activeDialogueText = MutableStateFlow<String?>(null)
    val activeDialogueText: StateFlow<String?> = _activeDialogueText.asStateFlow()

    // Movie Composition State
    private val _compositionStatus = MutableStateFlow<CompositionStatus>(CompositionStatus.Idle)
    val compositionStatus: StateFlow<CompositionStatus> = _compositionStatus.asStateFlow()

    sealed interface AnalysisStatus {
        object Idle : AnalysisStatus
        object ProcessingText : AnalysisStatus
        object DiscoveringCharacters : AnalysisStatus
        object DesigningScenes : AnalysisStatus
        object Success : AnalysisStatus
        data class Error(val errorMessage: String) : AnalysisStatus
    }

    sealed interface CompositionStatus {
        object Idle : CompositionStatus
        object RenderingVideo : CompositionStatus
        object SyncingAudio : CompositionStatus
        data class Ready(val videoUrl: String) : CompositionStatus
        data class Error(val message: String) : CompositionStatus
    }

    init {
        // Seed the starter classic novel if database is empty on start-up
        viewModelScope.launch {
            repository.seedMockDataIfEmpty()
        }
    }

    fun selectNovel(novelId: Int) {
        _selectedNovelId.value = novelId
        _currentCinemaEventIndex.value = 0
        _cinemaPlaying.value = false
        stopVoice()
    }

    // AI Novel analysis pipeline triggers here
    fun analyzeNovel(title: String, author: String, textContent: String) {
        viewModelScope.launch {
            _analysisStatus.value = AnalysisStatus.ProcessingText
            
            // 1. Initial pending DB insert
            val novel = Novel(
                title = title,
                author = author,
                textContent = textContent,
                status = "processing"
            )
            val novelId = repository.insertNovel(novel)

            try {
                _analysisStatus.value = AnalysisStatus.DiscoveringCharacters
                val extracted = geminiService.analyzeNovelText(title, author, textContent)
                
                if (extracted == null) {
                    _analysisStatus.value = AnalysisStatus.Error("AI Analysis service returned empty or failed. Please check your Gemini API key.")
                    repository.updateNovel(novel.copy(id = novelId, status = "failed"))
                    return@launch
                }

                _analysisStatus.value = AnalysisStatus.DesigningScenes
                // Save Characters
                val charactersToSave = extracted.characters.map { c ->
                    val stockImg = geminiService.generateStockIllustratedUrl(c.physicalDescription, isCharacter = true)
                    Character(
                        novelId = novelId,
                        name = c.name,
                        aliases = c.aliases,
                        role = c.role,
                        personality = c.personality,
                        physicalDescription = c.physicalDescription,
                        speakingStyle = c.speakingStyle,
                        imageUrl = stockImg,
                        relationshipsJson = c.relationships
                    )
                }
                repository.insertCharacters(charactersToSave)

                // Get DB Characters with correct ids
                val dbCharsList = repository.getCharactersForNovel(novelId).first()

                // Save Events
                val eventsToSave = extracted.events.map { e ->
                    val stockImg = geminiService.generateStockIllustratedUrl("${e.title} ${e.location}", isCharacter = false)
                    Event(
                        novelId = novelId,
                        title = e.title,
                        description = e.description,
                        visualDescription = e.visualDescription,
                        location = e.location,
                        emotionalTone = e.emotionalTone,
                        atmosphere = e.atmosphere,
                        chapter = e.chapter,
                        sequence = e.sequence,
                        imageUrl = stockImg
                    )
                }
                repository.insertEvents(eventsToSave)

                // Get DB Events list
                val dbEventsList = repository.getEventsForNovel(novelId).first()

                // Save Dialogues mapped to events and characters
                val dialoguesToSave = extracted.dialogues.map { d ->
                    val correspondingEvent = dbEventsList.find { it.sequence == d.eventSequence }
                    val correspondingChar = dbCharsList.find { it.name.lowercase() == d.speakerName.lowercase() }
                    
                    Dialogue(
                        eventId = correspondingEvent?.id ?: dbEventsList.firstOrNull()?.id ?: 0,
                        characterId = correspondingChar?.id,
                        text = d.text,
                        speakerName = d.speakerName,
                        addressedTo = d.addressedTo,
                        sequence = d.sequenceValue
                    )
                }
                repository.insertDialogues(dialoguesToSave)

                // Update original Novel entry
                val firstEventImg = dbEventsList.firstOrNull()?.imageUrl ?: "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600"
                repository.updateNovel(
                    Novel(
                        id = novelId,
                        title = title,
                        author = author,
                        textContent = textContent,
                        coverImage = firstEventImg,
                        status = "analyzed",
                        movieStatus = "ready"
                    )
                )

                _analysisStatus.value = AnalysisStatus.Success
                selectNovel(novelId)

            } catch (e: Exception) {
                Log.e("NovelViewModel", "Analysis failed: ${e.message}", e)
                _analysisStatus.value = AnalysisStatus.Error("Failed during analysis: ${e.message}")
                repository.updateNovel(novel.copy(id = novelId, status = "failed"))
            }
        }
    }

    // Cinema player automation & auto-advance logic
    fun toggleCinemaPlayback() {
        val nextMode = !_cinemaPlaying.value
        _cinemaPlaying.value = nextMode
        if (nextMode) {
            startCinemaPlaybackLoop()
        } else {
            stopVoice()
        }
    }

    private fun startCinemaPlaybackLoop() {
        viewModelScope.launch {
            val events = currentEvents.value
            val dialogues = currentDialogues.value
            val charsList = currentCharacters.value

            while (_cinemaPlaying.value) {
                val currentIndex = _currentCinemaEventIndex.value
                if (currentIndex >= events.size) {
                    _currentCinemaEventIndex.value = 0
                    _cinemaPlaying.value = false
                    break
                }

                val currentEvent = events[currentIndex]
                val eventDialogues = dialogues.filter { it.eventId == currentEvent.id }

                // 1. Narrator sets the scene by reading the description out loud
                _activeSpeakerName.value = null
                _activeDialogueText.value = currentEvent.description
                
                voiceService.speak(
                    text = currentEvent.description,
                    characterName = "NARRATOR",
                    role = "narrator"
                )

                val narratorTextTime = (currentEvent.description.length * 62L).coerceIn(3500L, 9000L)
                delay(narratorTextTime)

                if (!_cinemaPlaying.value) break

                // 2. Let characters take turns speaking to each other
                if (eventDialogues.isNotEmpty()) {
                    for (diag in eventDialogues) {
                        if (!_cinemaPlaying.value) break

                        val speakerChar = charsList.find { it.name.lowercase() == diag.speakerName.lowercase() }
                        _activeSpeakerName.value = diag.speakerName
                        _activeDialogueText.value = diag.text

                        // Trigger Voice Acting using Local TTS Service
                        voiceService.speak(
                            text = diag.text,
                            characterName = diag.speakerName,
                            role = speakerChar?.role ?: "supporting"
                        )

                        // Wait for dialogue reading time based on character count approx
                        val readTimeMs = (diag.text.length * 62L).coerceIn(3500L, 9000L)
                        delay(readTimeMs)
                    }
                }

                if (!_cinemaPlaying.value) break

                // Advance to next event
                if (currentIndex + 1 < events.size) {
                    _currentCinemaEventIndex.value = currentIndex + 1
                } else {
                    _currentCinemaEventIndex.value = 0
                    _cinemaPlaying.value = false
                }
            }
        }
    }

    fun setCinemaEventIndex(index: Int) {
        val events = currentEvents.value
        if (index in events.indices) {
            _currentCinemaEventIndex.value = index
            stopVoice()
            _activeDialogueText.value = null
            _activeSpeakerName.value = null
            if (_cinemaPlaying.value) {
                // Restart play loop from new position
                _cinemaPlaying.value = false
                viewModelScope.launch {
                    delay(200)
                    _cinemaPlaying.value = true
                    startCinemaPlaybackLoop()
                }
            }
        }
    }

    fun composeMovie(novelId: Int) {
        viewModelScope.launch {
            _compositionStatus.value = CompositionStatus.RenderingVideo
            delay(2000)
            _compositionStatus.value = CompositionStatus.SyncingAudio
            delay(2000)
            _compositionStatus.value = CompositionStatus.Ready("/assets/movie_$novelId.mp4")
        }
    }

    fun deleteNovel(novel: Novel) {
        viewModelScope.launch {
            repository.deleteNovel(novel)
            if (_selectedNovelId.value == novel.id) {
                _selectedNovelId.value = null
            }
        }
    }

    fun stopVoice() {
        voiceService.stop()
        _activeDialogueText.value = null
        _activeSpeakerName.value = null
    }

    override fun onCleared() {
        super.onCleared()
        voiceService.shutdown()
    }
}

// Custom ViewModel Factory
class NovelViewModelFactory(
    private val application: Application,
    private val repository: NovelRepository,
    private val geminiService: GeminiService,
    private val voiceService: VoiceActingService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NovelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NovelViewModel(application, repository, geminiService, voiceService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
