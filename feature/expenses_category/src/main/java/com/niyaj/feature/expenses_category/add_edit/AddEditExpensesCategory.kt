package com.niyaj.feature.expenses_category.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ExpenseTestTags.ADD_EDIT_EXPENSE_CATEGORY_BUTTON
import com.niyaj.common.tags.ExpenseTestTags.ADD_EDIT_EXPENSE_CATEGORY_SCREEN
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE_CATEGORY
import com.niyaj.common.tags.ExpenseTestTags.EDIT_EXPENSE_CATEGORY_ITEM
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_CATEGORY_NAME_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_CATEGORY_NAME_FIELD
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.expenses_category.ExpensesCategoryViewModel
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.flow.collectLatest

/**
 * Add/Edit Expenses Category Screen
 * @author Sk Niyaj Ali
 * @param expensesCategoryId
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see ExpensesCategoryViewModel
 */
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditExpensesCategoryScreen(
    expensesCategoryId: String = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditExpensesCategoryViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = expensesCategoryId) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    val title =
        if (expensesCategoryId.isEmpty()) CREATE_NEW_EXPENSE_CATEGORY else EDIT_EXPENSE_CATEGORY_ITEM

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ADD_EDIT_EXPENSE_CATEGORY_SCREEN),
        text = title,
        icon = Icons.Default.Category,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            StandardOutlinedTextField(
                modifier = Modifier,
                text = viewModel.categoryName,
                label = EXPENSE_CATEGORY_NAME_FIELD,
                leadingIcon = Icons.Default.Category,
                error = nameError,
                errorTag = EXPENSE_CATEGORY_NAME_ERROR,
                onValueChange = viewModel::updateCategoryName,
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_EXPENSE_CATEGORY_BUTTON),
                text = title,
                enabled = nameError == null,
                icon = if (expensesCategoryId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    viewModel.createOrUpdateCategory(expensesCategoryId)
                },
            )
        }
    }
}