package com.niyaj.feature.product.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ProductTestTags.ADD_EDIT_PRODUCT_BUTTON
import com.niyaj.common.tags.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.common.tags.ProductTestTags.EDIT_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_AVAILABILITY_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_CATEGORY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_CATEGORY_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_FIELD
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.CustomDropdownMenuItem
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@OptIn(ExperimentalMaterialApi::class)
@Destination(route = Screens.ADD_EDIT_PRODUCT_SCREEN, style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditProductScreen(
    productId: String = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditProductViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    val categoryError = viewModel.categoryError.collectAsStateWithLifecycle().value
    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(
        categoryError,
        priceError,
        nameError,
    ).all { it == null }

    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    var expanded by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val title = if (productId.isEmpty()) CREATE_NEW_PRODUCT else EDIT_PRODUCT

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
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
        modifier = Modifier.fillMaxWidth(),
        text = title,
        icon = Icons.Default.Dns,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .testTag(PRODUCT_CATEGORY_FIELD),
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            //This value is used to assign to the DropDown the same width
                            textFieldSize = coordinates.size.toSize()
                        },
                    text = selectedCategory.categoryName,
                    label = PRODUCT_CATEGORY_FIELD,
                    leadingIcon = Icons.Default.Category,
                    error = categoryError,
                    errorTag = PRODUCT_CATEGORY_ERROR,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                        .height(300.dp),
                ) {
                    categories.forEach { category ->
                        CustomDropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(category.categoryId),
                            text = {
                                Text(
                                    text = category.categoryName,
                                    style = MaterialTheme.typography.body2,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                viewModel.onEvent(AddEditProductEvent.CategoryChanged(category))
                                expanded = false
                            },
                            leadingIcon = {
                                CircularBox(
                                    icon = Icons.Default.Category,
                                    doesSelected = selectedCategory.categoryName == category.categoryName,
                                    text = category.categoryName
                                )
                            }
                        )

                        Divider(modifier = Modifier.fillMaxWidth())
                    }

                    if (categories.isEmpty()) {
                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            enabled = false,
                            onClick = {},
                            content = {
                                Text(
                                    text = "Categories not available",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            },
                        )
                    }

                    Divider(modifier = Modifier.fillMaxWidth())

                    CustomDropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            navController.navigate(Screens.ADD_EDIT_CATEGORY_SCREEN)
                        },
                        text = {
                            Text(
                                text = "Create new category",
                                color = MaterialTheme.colors.secondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create",
                                tint = MaterialTheme.colors.secondary
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                contentDescription = "trailing"
                            )
                        }
                    )
                }
            }

            StandardOutlinedTextField(
                modifier = Modifier.testTag(PRODUCT_NAME_FIELD),
                text = viewModel.state.productName,
                label = PRODUCT_NAME_FIELD,
                leadingIcon = Icons.Default.Badge,
                error = nameError,
                errorTag = PRODUCT_NAME_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditProductEvent.ProductNameChanged(it))
                },
            )

            StandardOutlinedTextField(
                modifier = Modifier.testTag(PRODUCT_PRICE_FIELD),
                text = viewModel.state.productPrice,
                label = PRODUCT_PRICE_FIELD,
                leadingIcon = Icons.Default.CurrencyRupee,
                keyboardType = KeyboardType.Number,
                error = priceError,
                errorTag = PRODUCT_PRICE_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditProductEvent.ProductPriceChanged(it))
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    modifier = Modifier.testTag(PRODUCT_AVAILABILITY_FIELD),
                    checked = viewModel.state.productAvailability,
                    onCheckedChange = {
                        viewModel.onEvent(AddEditProductEvent.ProductAvailabilityChanged)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = if (viewModel.state.productAvailability)
                        "Marked as available"
                    else
                        "Marked as unavailable",
                    style = MaterialTheme.typography.overline
                )
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PRODUCT_BUTTON),
                text = title,
                enabled = enableBtn,
                icon = if (productId.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    viewModel.onEvent(AddEditProductEvent.AddOrUpdateProduct(productId))
                },
            )
        }
    }
}