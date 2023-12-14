package com.movies.syncflix.features.serverMode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.movies.syncflix.feature.servermode.api.ServerModeComponent
import com.movies.syncflix.features.VideoPlayer

@Composable
fun ServerModeScreen(
    component: ServerModeComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    Scaffold(
        modifier = modifier.statusBarsPadding()
    ) { scaffoldPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val colorCircle = if (state.isOnline) {
                    Color.Green
                } else {
                    Color.Red
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(colorCircle, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Android ${state.serverIp}")
            }

            VideoPlayer(
                component = component.videoPlayerComponent,
                modifier = Modifier
            )
        }
    }
}