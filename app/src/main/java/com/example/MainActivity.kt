package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.database.AppDatabase
import com.example.data.repository.NovelRepository
import com.example.service.GeminiService
import com.example.service.VoiceActingService
import com.example.ui.screens.NovelWeaverAppUi
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NovelViewModel
import com.example.viewmodel.NovelViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var voiceActingService: VoiceActingService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize DB & Repositories
        val database = AppDatabase.getDatabase(this)
        val repository = NovelRepository(
            novelDao = database.novelDao(),
            characterDao = database.characterDao(),
            eventDao = database.eventDao(),
            dialogueDao = database.dialogueDao()
        )

        // 2. Initialize Core Services (Gemini and local Text-To-Speech config)
        val geminiService = GeminiService()
        voiceActingService = VoiceActingService(this)

        // 3. Initialize Shared State ViewModel
        val factory = NovelViewModelFactory(
            application = application,
            repository = repository,
            geminiService = geminiService,
            voiceService = voiceActingService
        )
        val viewModel = ViewModelProvider(this, factory)[NovelViewModel::class.java]

        // 4. Render Layout Theme Overlay
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                NovelWeaverAppUi(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceActingService.shutdown()
    }
}
