package com.movies.syncflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.media3.common.util.UnstableApi
import com.arkivanov.decompose.defaultComponentContext
import com.movies.syncflix.features.root.DefaultRootComponent
import com.movies.syncflix.features.root.RootScreen
import com.movies.syncflix.ui.theme.SyncFlixTheme
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val rootComponent = DefaultRootComponent(
            dependencies = get(),
            componentContext = defaultComponentContext(),
        )
        setContent {
            SyncFlixTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RootScreen(rootComponent)
                }
            }
        }
    }
}