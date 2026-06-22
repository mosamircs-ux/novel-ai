package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale

class VoiceActingService(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    // Map of Character Name to Pitch/Rate and specific Voice criteria
    private val characterVoiceMap = mutableMapOf<String, CharacterVoiceConfig>()

    data class CharacterVoiceConfig(
        val pitch: Float = 1.0f,
        val speechRate: Float = 1.0f,
        val preferredVoiceIndex: Int = 0 // Offset index to pick among standard voices
    )

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                Log.d("VoiceActingService", "TextToSpeech initialized successfully.")
                tts?.language = Locale.US
            } else {
                Log.e("VoiceActingService", "Failed to initialize TextToSpeech.")
            }
        }
    }

    // Configures or returns voice characteristics for a character
    private fun getOrCreateConfigForCharacter(name: String, role: String): CharacterVoiceConfig {
        return characterVoiceMap.getOrPut(name) {
            when (role.lowercase()) {
                "protagonist" -> CharacterVoiceConfig(pitch = 0.85f, speechRate = 1.05f, preferredVoiceIndex = 1)
                "antagonist" -> CharacterVoiceConfig(pitch = 0.70f, speechRate = 0.90f, preferredVoiceIndex = 2)
                "supporting" -> {
                    // Stagger voice characteristics based on hashcode
                    val hash = name.hashCode().coerceAtLeast(0)
                    val pitchVal = 0.9f + (hash % 4) * 0.1f // 0.9, 1.0, 1.1, 1.2
                    val speedVal = 0.85f + ((hash + 1) % 4) * 0.1f // 0.85, 0.95, 1.05, 1.15
                    CharacterVoiceConfig(pitch = pitchVal, speechRate = speedVal, preferredVoiceIndex = (hash % 3) + 3)
                }
                else -> CharacterVoiceConfig(pitch = 1.0f, speechRate = 1.0f, preferredVoiceIndex = 0)
            }
        }
    }

    fun speak(text: String, characterName: String, role: String) {
        if (!isInitialized) {
            Log.w("VoiceActingService", "TTS not fully initialized yet.")
            return
        }

        val config = getOrCreateConfigForCharacter(characterName, role)
        
        // Stop any current speech
        tts?.stop()

        // Auto-detect language (Arabic vs English)
        val isArabic = text.any { it in '\u0600'..'\u06FF' }
        val locale = if (isArabic) Locale("ar") else Locale.US
        tts?.language = locale

        // Set Pitch and Speech Rate
        tts?.setPitch(config.pitch)
        tts?.setSpeechRate(config.speechRate)

        // Try to assign a unique voice if available
        try {
            val availableVoices = tts?.voices?.toList()
            if (!availableVoices.isNullOrEmpty()) {
                val matchingVoices = availableVoices.filter { it.locale.language == locale.language }
                if (matchingVoices.isNotEmpty()) {
                    val assignedVoice = matchingVoices[config.preferredVoiceIndex % matchingVoices.size]
                    tts?.voice = assignedVoice
                    Log.d("VoiceActingService", "Assigned voice: ${assignedVoice.name} to $characterName (Language: ${locale.language})")
                }
            }
        } catch (e: Exception) {
            Log.e("VoiceActingService", "Error setting custom voice: ${e.message}")
        }

        // Play TTS speech
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "DialogueId_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
