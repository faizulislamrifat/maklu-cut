package com.example.maklucut

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.maklucut.data.Clip
import com.example.maklucut.data.GeminiScout
import com.example.maklucut.data.VideoInfo
import com.example.maklucut.ui.main.AppScreen
import com.example.maklucut.ui.main.ClipDetailScreen
import com.example.maklucut.ui.main.HomeScreen
import com.example.maklucut.ui.main.SettingsScreen
import com.example.maklucut.ui.theme.MAKLUCUTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("maklu_prefs", Context.MODE_PRIVATE)

        setContent {
            MAKLUCUTTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
                var apiKey by remember {
                    mutableStateOf(prefs.getString("gemini_api_key", "") ?: "")
                }
                var modelName by remember {
                    mutableStateOf(prefs.getString("gemini_model", GeminiScout.DEFAULT_MODEL) ?: GeminiScout.DEFAULT_MODEL)
                }
                var selectedVideo by remember { mutableStateOf<VideoInfo?>(null) }
                var selectedClip by remember { mutableStateOf<Clip?>(null) }

                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    currentScreen = AppScreen.HOME
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF141416))
                        .statusBarsPadding()
                ) {
                    when (currentScreen) {
                        AppScreen.HOME -> {
                            HomeScreen(
                                apiKey = apiKey,
                                modelName = modelName,
                                onOpenClip = { video, clip ->
                                    selectedVideo = video
                                    selectedClip = clip
                                    currentScreen = AppScreen.CLIP_DETAIL
                                },
                                onOpenSettings = {
                                    currentScreen = AppScreen.SETTINGS
                                }
                            )
                        }
                        AppScreen.CLIP_DETAIL -> {
                            if (selectedVideo != null && selectedClip != null) {
                                ClipDetailScreen(
                                    video = selectedVideo!!,
                                    clip = selectedClip!!,
                                    onBack = { currentScreen = AppScreen.HOME }
                                )
                            } else {
                                currentScreen = AppScreen.HOME
                            }
                        }
                        AppScreen.SETTINGS -> {
                            SettingsScreen(
                                currentApiKey = apiKey,
                                currentModel = modelName,
                                onSave = { newKey, newModel ->
                                    apiKey = newKey
                                    modelName = newModel
                                    prefs.edit()
                                        .putString("gemini_api_key", newKey)
                                        .putString("gemini_model", newModel)
                                        .apply()
                                },
                                onBack = { currentScreen = AppScreen.HOME }
                            )
                        }
                    }
                }
            }
        }
    }
}
