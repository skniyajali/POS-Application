package com.niyaj.popos.presentation.product.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.navigation.NavController
import com.niyaj.popos.presentation.components.SettingsCard
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.destinations.ExportProductScreenDestination
import com.niyaj.popos.destinations.ImportProductScreenDestination
import com.niyaj.popos.destinations.ProductPriceScreenDestination
import com.niyaj.popos.presentation.ui.theme.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
@Destination
@Composable
fun ProductSettingScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    resultRecipient: ResultRecipient<ProductPriceScreenDestination, String>,
    exportResultRecipient: ResultRecipient<ExportProductScreenDestination, String>,
    importResultRecipient: ResultRecipient<ImportProductScreenDestination, String>,
) {

    val scope = rememberCoroutineScope()

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportResultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    importResultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        title = {
            Text(text = "Product Settings")
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            SettingsCard(
                text = "Increase Product Price",
                icon = Icons.Default.ControlPoint,
                onClick = {
                    navController.navigate(ProductPriceScreenDestination())
                },
            )
            Spacer(modifier = Modifier.height(SpaceMedium))

            SettingsCard(
                text = "Decrease Product Price",
                icon = Icons.Default.RemoveCircleOutline,
                onClick = {
                    navController.navigate(ProductPriceScreenDestination(type = "Decrease"))
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            SettingsCard(
                text = "Import Products",
                icon = Icons.Default.SaveAlt,
                onClick = {
                    navController.navigate(ImportProductScreenDestination)
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            SettingsCard(
                text = "Export Products",
                icon = Icons.Default.SaveAlt,
                iconModifier = Modifier.rotate(180F),
                onClick = {
                    navController.navigate(ExportProductScreenDestination)
                },
            )
        }
    }
}