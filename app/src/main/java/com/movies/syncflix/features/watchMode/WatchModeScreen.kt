package com.movies.syncflix.features.watchMode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movies.syncflix.features.VideoPlayer
import com.movies.syncflix.watchmode.api.WatchModeComponent

@Composable
fun WatchModeScreen(
    component: WatchModeComponent,
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
            VideoPlayer(
                component = component.videoPlayerComponent,
                modifier = Modifier
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                value = state.ipAddress,
                onValueChange = component::onIpAddressChange
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = component::onConnectClick) {
                Text("Подключиться")
            }
        }
    }
}