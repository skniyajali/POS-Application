package com.niyaj.popos.features.category.presentation

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.category.domain.util.CategoryTestTags.ADD_EDIT_CATEGORY_BTN
import com.niyaj.popos.features.category.domain.util.CategoryTestTags.CATEGORY_NAME_ERROR
import com.niyaj.popos.features.category.domain.util.CategoryTestTags.CATEGORY_NAME_FIELD
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest

/**
 * Add Edit Category Screen
 * @author Sk Niyaj Ali
 * @param categoryId
 * @param navController
 * @param categoryViewModel
 * @see CategoryViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditCategoryScreen(
    categoryId: String? = "",
    navController: NavController = rememberNavController(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    LaunchedEffect(key1 = true){
        categoryViewModel.eventFlow.collectLatest{ event ->
            when(event){
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

    SentryTraced(tag = "AddEditCategoryScreen") {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxWidth(),
            text = if (!categoryId.isNullOrEmpty())
                stringResource(id = R.string.edit_category)
            else
                stringResource(id = R.string.create_category),
            icon = Icons.Default.Category,
            onClosePressed = {
                navController.navigateUp()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(CATEGORY_NAME_FIELD),
                    text = categoryViewModel.addEditCategoryState.categoryName,
                    label = "Category Name",
                    leadingIcon = Icons.Default.Badge,
                    error = categoryViewModel.addEditCategoryState.categoryNameError,
                    errorTag = CATEGORY_NAME_ERROR,
                    onValueChange = {
                        categoryViewModel.onEvent(CategoryEvent.CategoryNameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = categoryViewModel.addEditCategoryState.categoryAvailability,
                        onCheckedChange = {
                            categoryViewModel.onEvent(CategoryEvent.CategoryAvailabilityChanged)
                        }
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if(categoryViewModel.addEditCategoryState.categoryAvailability)
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
                    text = if (!categoryId.isNullOrEmpty()) stringResource(id = R.string.edit_category)
                    else stringResource(id = R.string.create_category),
                    icon = if (!categoryId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.AddCircleOutline,
                    onClick = {
                        if (!categoryId.isNullOrEmpty()) {
                            categoryViewModel.onEvent(CategoryEvent.UpdateCategory(categoryId))
                        } else {
                            categoryViewModel.onEvent(CategoryEvent.CreateNewCategory)
                        }
                    }
                )
            }
        }
    }
}