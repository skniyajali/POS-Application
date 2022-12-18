package com.niyaj.popos.presentation.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun NotificationScreen(
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = "Notifications screen", modifier = Modifier.fillMaxSize().align(Alignment.Center))
    }
}