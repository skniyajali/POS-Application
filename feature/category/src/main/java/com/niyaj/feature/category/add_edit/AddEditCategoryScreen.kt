package com.niyaj.feature.category.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CategoryTestTags.ADD_EDIT_CATEGORY_BTN
import com.niyaj.common.tags.CategoryTestTags.ADD_EDIT_CATEGORY_SCREEN
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_AVAILABLE_SWITCH
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_ERROR
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NAME_FIELD
import com.niyaj.common.tags.CategoryTestTags.CREATE_NEW_CATEGORY
import com.niyaj.common.tags.CategoryTestTags.UPDATE_CATEGORY
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.flow.collectLatest

/**
 * Add Edit Category Screen
 * @author Sk Niyaj Ali
 * @param categoryId
 * @param navController
 * @param viewModel
 * @see AddEditCategoryViewModel
 */
@Destination(route = Screens.ADD_EDIT_CATEGORY_SCREEN, style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditCategoryScreen(
    categoryId: String? = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditCategoryViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val categoryNameError = viewModel.categoryNameError.collectAsStateWithLifecycle().value

    val title = if (!categoryId.isNullOrEmpty()) UPDATE_CATEGORY else CREATE_NEW_CATEGORY

    LaunchedEffect(key1 = true) {
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

    BottomSheetWithCloseDialog(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ADD_EDIT_CATEGORY_SCREEN),
        text = title,
        icon = Icons.Default.Category,
        onClosePressed = { navController.navigateUp() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StandardOutlinedTextField(
                modifier = Modifier.testTag(CATEGORY_NAME_FIELD),
                text = viewModel.state.categoryName,
                label = CATEGORY_NAME_FIELD,
                leadingIcon = Icons.Default.Badge,
                error = categoryNameError,
                errorTag = CATEGORY_NAME_ERROR,
                onValueChange = {
                    viewModel.onEvent(CategoryEvent.CategoryNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    modifier = Modifier.testTag(CATEGORY_AVAILABLE_SWITCH),
                    checked = viewModel.state.categoryAvailability,
                    onCheckedChange = {
                        viewModel.onEvent(CategoryEvent.CategoryAvailabilityChanged)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = if (viewModel.state.categoryAvailability)
                        "Marked as available"
                    else
                        "Marked as unavailable",
                    style = MaterialTheme.typography.overline
                )
            }

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_CATEGORY_BTN),
                enabled = categoryNameError == null,
                text = title,
                icon = if (!categoryId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.AddCircleOutline,
                onClick = {
                    viewModel.onEvent(CategoryEvent.CreateOrUpdateCategory)
                }
            )
        }
    }

}