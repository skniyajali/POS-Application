package com.niyaj.popos.features.expenses.presentation.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor8
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.SettingsCard
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.ExportExpensesScreenDestination
import com.niyaj.popos.features.destinations.ImportExpensesScreenDestination
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.presentation.ExpensesItems
import com.niyaj.popos.features.expenses.presentation.ExpensesViewModel
import com.niyaj.popos.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult.Canceled
import com.ramcosta.composedestinations.result.NavResult.Value
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Expenses Setting Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see ExpensesViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination()
@Composable
fun ExpensesSettingScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ExpensesSettingViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
    importRecipient: ResultRecipient<ImportExpensesScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportExpensesScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val deleteLastSevenDaysState = rememberMaterialDialogState()
    val deleteAllOrdersState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is Canceled -> {}
            is Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportRecipient.onNavResult { result ->
        when (result) {
            is Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
            is Canceled -> {}
        }
    }

    SentryTraced(tag = "ExpensesSettingScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            title = {
                Text(text = "Expenses Settings")
            },
            navActions = {},
        ) {
            MaterialDialog(
                dialogState = deleteLastSevenDaysState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            viewModel.onEvent(ExpensesSettingsEvent.DeletePastExpenses)
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteLastSevenDaysState.hide()
                        },
                    )
                }
            ) {
                title(text = "Delete Past Expenses?")
                message(res = R.string.delete_past_expenses_msg)
            }

            MaterialDialog(
                dialogState = deleteAllOrdersState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            viewModel.onEvent(ExpensesSettingsEvent.DeleteAllExpenses)
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteAllOrdersState.hide()
                        },
                    )
                }
            ) {
                title(text = "Delete All Expenses?")
                message(res = R.string.delete_all_expenses_msg)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
            ) {
                item {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    SettingsCard(
                        text = "Delete Past Expenses",
                        icon = Icons.Default.Delete,
                        onClick = {
                            deleteLastSevenDaysState.show()
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    SettingsCard(
                        text = "Delete All Expenses",
                        icon = Icons.Default.DeleteForever,
                        onClick = {
                            deleteAllOrdersState.show()
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item {
                    SettingsCard(
                        text = "Import Expenses",
                        icon = Icons.Default.SaveAlt,
                        onClick = {
                            navController.navigate(ImportExpensesScreenDestination())
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item {
                    SettingsCard(
                        text = "Export Expenses",
                        icon = Icons.Default.SaveAlt,
                        iconModifier = Modifier.rotate(180F),
                        onClick = {
                            navController.navigate(ExportExpensesScreenDestination())
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImportExportExpensesBody(
    lazyListState: LazyListState,
    groupedExpenses: Map<String, List<Expenses>>,
    selectedExpenses: List<String>,
    expanded: Boolean,
    onExpandChanged: () -> Unit,
    onSelectExpense : (String) -> Unit,
    onClickSelectAll: () -> Unit,
    backgroundColor: Color = LightColor8
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = backgroundColor,
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = expanded,
            title = {
                TextWithIcon(
                    text = if(selectedExpenses.isNotEmpty()) "${selectedExpenses.size} Selected" else "Select Expenses",
                    icon = Icons.Default.Dns,
                    isTitle = true
                )
            },
            rowClickable = true,
            trailing = {
                IconButton(
                    onClick = onClickSelectAll
                ) {
                    Icon(
                        imageVector = Icons.Default.Rule,
                        contentDescription = "Select All Customers"
                    )
                }
            },
            expand = {  modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                ExpensesItems(
                    lazyListState = lazyListState,
                    showScrollToTop = lazyListState.isScrolled,
                    groupedExpenses = groupedExpenses,
                    doesSelected = {
                        selectedExpenses.contains(it)
                    },
                    doesAnySelected = true,
                    onSelectExpense = onSelectExpense,
                    headerColor = backgroundColor,
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(SpaceMedium))
}