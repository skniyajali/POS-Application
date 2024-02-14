package com.niyaj.feature.address.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SETTINGS_SCREEN
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_MESSAGE
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.address.destinations.ExportAddressScreenDestination
import com.niyaj.feature.address.destinations.ImportAddressScreenDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffoldNewF
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.isScrolled
import com.niyaj.ui.util.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@Destination
@Composable
fun AddressSettingScreen(
    scaffoldState: ScaffoldState,
    navController: NavController,
    viewModel: AddressSettingViewModel = hiltViewModel(),
    importRecipient: ResultRecipient<ImportAddressScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportAddressScreenDestination, String>,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNewF(
        navController = navController,
        scaffoldState = scaffoldState,
        title = ADDRESS_SETTINGS_SCREEN,
        showBackButton = true,
        navActions = {},
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ) {
            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                SettingsCard(
                    text = "Delete All Address",
                    icon = Icons.Default.DeleteForever,
                    onClick = {
                        dialogState.show()
                    },
                )
            }

            item {
                SettingsCard(
                    text = "Import Addresses",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportAddressScreenDestination())
                    },
                )
            }

            item {
                SettingsCard(
                    text = "Export Addresses",
                    icon = Icons.Default.SaveAlt,
                    iconModifier = Modifier.rotate(180F),
                    onClick = {
                        navController.navigate(ExportAddressScreenDestination())
                    },
                )
            }
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = viewModel::deleteItems
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    dialogState.hide()
                },
            )
        }
    ) {
        title(text = DELETE_ADDRESS_ITEM_TITLE)
        message(text = DELETE_ADDRESS_ITEM_MESSAGE)
    }
}