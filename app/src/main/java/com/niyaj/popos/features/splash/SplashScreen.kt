package com.niyaj.popos.features.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate

@Destination
@Composable
fun SplashScreen(
    navController : NavController = rememberNavController()
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This is the splash screen", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(SpaceMedium))
        Button(
            onClick = {
                navController.navigate(MainFeedScreenDestination)
            }
        ){
            Text("Go to Home")
        }
    }
}