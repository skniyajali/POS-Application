package com.niyaj.popos.realm.app_settings.presentation

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
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.ExtendedFabButton
import com.niyaj.popos.presentation.components.SettingsCard
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.destinations.DeletionSettingsDestination
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
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
fun SettingsScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<DeletionSettingsDestination, String>
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
        settingsViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.OnError -> {
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
            ExtendedFabButton(
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

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            item {
                Spacer(modifier = Modifier.height(SpaceSmall))
                SettingsCard(
                    text = "Data Deletion Settings",
                    icon = Icons.Default.RemoveCircleOutline,
                    onClick = {
                        navController.navigate(DeletionSettingsDestination())
                    },
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }
            item {
                SettingsCard(
                    text = "Delete All Records",
                    icon = Icons.Default.DeleteForever,
                    onClick = {
                        dialogState.show()
                    },
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }

    }
}