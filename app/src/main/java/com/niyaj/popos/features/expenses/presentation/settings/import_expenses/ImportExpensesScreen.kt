package com.niyaj.popos.features.expenses.presentation.settings.import_expenses

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.ImportExport
import com.niyaj.popos.features.common.util.ImportExport.openFile
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ImportExportHeader
import com.niyaj.popos.features.components.ImportFooter
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.presentation.settings.ExpensesSettingViewModel
import com.niyaj.popos.features.expenses.presentation.settings.ExpensesSettingsEvent
import com.niyaj.popos.features.expenses.presentation.settings.ImportExportExpensesBody
import com.niyaj.popos.utils.toPrettyDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination(style = DestinationStyle.BottomSheet::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ImportExpensesScreen(
    navController : NavController,
    viewModel : ExpensesSettingViewModel = hiltViewModel(),
    resultBackNavigator : ResultBackNavigator<String>
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val isChosen = viewModel.onChoose

    val selectedExpenses = viewModel.selectedExpenses.toList()

    val importedData = viewModel.importExportedExpenses.collectAsStateWithLifecycle().value

    val showImportedBtn = if(isChosen) selectedExpenses.isNotEmpty() else importedData.isNotEmpty()

    var expanded by remember {
        mutableStateOf(false)
    }

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val newData = ImportExport.readData<Expenses>(context, it)

                    viewModel.onEvent(ExpensesSettingsEvent.ImportExpensesData(newData))
                }
            }
        }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.import_expenses),
        icon = Icons.Default.SaveAlt,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            if(importedData.isNotEmpty()) {
                ImportExportHeader(
                    modifier = Modifier,
                    text = "Import " + if (isChosen) "${selectedExpenses.size} Selected Expenses" else " All Expenses",
                    onClickAll = {
                        viewModel.onChoose = false
                        viewModel.onEvent(ExpensesSettingsEvent.SelectAllExpenses())
                    },
                    isChosen = isChosen,
                    onClickChoose = {
                        viewModel.onEvent(ExpensesSettingsEvent.OnChooseExpenses)
                    }
                )

                AnimatedVisibility(
                    visible = isChosen,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (expanded) Modifier.weight(1.1F) else Modifier),
                ) {
                    ImportExportExpensesBody(
                        lazyListState = lazyListState,
                        groupedExpenses = importedData.groupBy { it.createdAt.toPrettyDate() },
                        selectedExpenses = selectedExpenses,
                        expanded = expanded,
                        onExpandChanged = {
                            expanded = !expanded
                        },
                        onSelectExpense = {
                            viewModel.onEvent(ExpensesSettingsEvent.SelectExpenses(it))
                        },
                        onClickSelectAll = {
                            viewModel.onEvent(ExpensesSettingsEvent.SelectAllExpenses())
                        }
                    )
                }
            }

            ImportFooter(
                text = "Import ${if (isChosen) selectedExpenses.size else "All"} Expenses",
                importedDataIsEmpty = importedData.isNotEmpty(),
                showImportedBtn = showImportedBtn,
                onClearImportedData = {
                    viewModel.onEvent(ExpensesSettingsEvent.ClearImportedExpenses)
                },
                onImportData = {
                    viewModel.onEvent(ExpensesSettingsEvent.ImportExpenses)
                },
                onOpenFile = {
                    scope.launch {
                        val result = openFile(context)
                        importLauncher.launch(result)
                    }
                }
            )
        }
    }
}