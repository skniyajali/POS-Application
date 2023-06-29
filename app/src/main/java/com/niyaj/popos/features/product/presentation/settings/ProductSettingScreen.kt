package com.niyaj.popos.features.product.presentation.settings

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.SettingsCard
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.ExportProductScreenDestination
import com.niyaj.popos.features.destinations.ImportProductScreenDestination
import com.niyaj.popos.features.destinations.ProductPriceScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch


/**
 * Product Setting Screen
 * @author Sk Niyaj Ali
 *
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ProductSettingScreen(
    navController : NavController,
    scaffoldState : ScaffoldState,
    resultRecipient : ResultRecipient<ProductPriceScreenDestination, String>,
    exportResultRecipient : ResultRecipient<ExportProductScreenDestination, String>,
    importResultRecipient : ResultRecipient<ImportProductScreenDestination, String>,
) {
    val hasStoragePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    )

    val scope = rememberCoroutineScope()

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    importResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    SentryTraced(tag = "ProductSettingScreen") {
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
}