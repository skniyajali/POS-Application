package com.niyaj.popos.features.app_settings.presentation

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.BuildConfig
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.SettingsCard
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.DeletionSettingsDestination
import com.niyaj.popos.features.destinations.SettingsScreenDestination
import com.niyaj.popos.features.order.presentation.components.TwoGridText
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

/**
 *  Settings Screen Composable
 *  @param navController
 *  @param scaffoldState
 *  @param settingsViewModel
 *  @param resultRecipient
 *
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Destination
@Composable
fun SettingsScreen(
    navController : NavController,
    scaffoldState : ScaffoldState,
    settingsViewModel : SettingsViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<DeletionSettingsDestination, String>
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()
    val deletePastState = rememberMaterialDialogState()

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        settingsViewModel.eventFlow.collect { event ->
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

    val hasStoragePermission =
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )

    fun askForPermissions() {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    SentryTraced(tag = SettingsScreenDestination.route) {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            title = {
                Text(text = "Settings")
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
                            settingsViewModel.onEvent(SettingsEvent.DeleteAllRecords)
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
                title(text = "Delete All Records?")
                message(res = R.string.delete_all_records)
            }

            MaterialDialog(
                dialogState = deletePastState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            settingsViewModel.onEvent(SettingsEvent.DeletePastRecords)
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deletePastState.hide()
                        },
                    )
                }
            ) {
                title(text = "Delete Past Records?")
                message(res = R.string.delete_past_records)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.5f),
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                ) {
                    item("Deletion Settings") {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        SettingsCard(
                            text = "Data Deletion Settings",
                            icon = Icons.Default.RemoveCircleOutline,
                            onClick = {
                                navController.navigate(DeletionSettingsDestination())
                            },
                        )
                    }

                    item("Delete Past Records") {
                        SettingsCard(
                            text = "Delete Past Records",
                            icon = Icons.Default.Delete,
                            onClick = {
                                deletePastState.show()
                            },
                        )
                    }

                    item("Delete All Records") {
                        SettingsCard(
                            text = "Delete All Records",
                            icon = Icons.Default.DeleteForever,
                            onClick = {
                                dialogState.show()
                            },
                        )
                    }

                    item("Backup Database") {
                        SettingsCard(
                            text = "Backup Database",
                            icon = Icons.Default.UploadFile,
                            onClick = {
                                askForPermissions()

                                settingsViewModel.backupDatabase()
                            },
                        )
                    }

                    item("Restore Database") {
                        SettingsCard(
                            text = "Restore Database",
                            icon = Icons.Default.SettingsBackupRestore,
                            onClick = {
                                askForPermissions()

                                settingsViewModel.restoreDatabase(context)
                            },
                        )
                    }
                }

                Column {
                    AppDetails()
                }
            }
        }
    }
}


@Composable
fun AppDetails(
    modifier : Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
    ) {
        TwoGridText(
            textOne = "Developed By",
            textTwo = stringResource(id = R.string.developer_name),
        )

        Divider(modifier = Modifier.fillMaxWidth())

        TwoGridText(
            textOne = "Developer Email",
            textTwo = stringResource(id = R.string.developer_email),
        )

        Divider(modifier = Modifier.fillMaxWidth())

        TwoGridText(
            textOne = "Developer Profile",
            textTwo = stringResource(id = R.string.developer_profile),
        )

        Divider(modifier = Modifier.fillMaxWidth())

        TwoGridText(
            textOne = "Application ID",
            textTwo = BuildConfig.APPLICATION_ID,
        )

        Divider(modifier = Modifier.fillMaxWidth())

        TwoGridText(
            textOne = "Version Name",
            textTwo = BuildConfig.VERSION_NAME
        )

        Divider(modifier = Modifier.fillMaxWidth())

        TwoGridText(
            textOne = "Version Code",
            textTwo = BuildConfig.VERSION_CODE.toString()
        )
    }
}