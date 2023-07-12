package com.niyaj.popos.features.address.presentation.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.SettingsCard
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.ExportAddressScreenDestination
import com.niyaj.popos.features.destinations.ImportAddressScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun AddressSettingScreen(
    scaffoldState : ScaffoldState,
    navController : NavController,
    viewModel : AddressSettingViewModel = hiltViewModel(),
    importRecipient: ResultRecipient<ImportAddressScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportAddressScreenDestination, String>,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

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

                is UiEvent.IsLoading -> {}
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

    SentryTraced(tag = "AddressSettingsScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            title = {
                Text(text = "Address Settings")
            },
            navActions = {},
            isFloatingActionButtonDocked = true,
            floatingActionButton = {
                StandardFabButton(
                    text = "",
                    showScrollToTop = showScrollToTop.value,
                    visible = false,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {},
                )
            },
            floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
        ) {
            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            viewModel.onEvent(AddressSettingEvent.DeleteAllAddress)
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            dialogState.hide()
                        },
                    )
                }
            ) {
                title(text = "Delete All Addresses?")
                message(res = R.string.delete_all_addresses)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
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
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item {
                    SettingsCard(
                        text = "Import Addresses",
                        icon = Icons.Default.SaveAlt,
                        onClick = {
                            navController.navigate(ImportAddressScreenDestination())
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
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
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}