package dev.dai.compose.visibility.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.dai.compose.visibility.sample.di.appModule
import dev.dai.compose.visibility.sample.ui.screen.ImageListScreen
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            ImageListScreen()
        }
    }
}