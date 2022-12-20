package com.niyaj.popos.features.product.presentation.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.destinations.AddEditCategoryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@OptIn(ExperimentalMaterialApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditProductScreen(
    productId: String? = "",
    navController: NavController = rememberNavController(),
    addEditProductViewModel: AddEditProductViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(key1 = true) {
        addEditProductViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = if (!productId.isNullOrEmpty())
            stringResource(id = R.string.edit_product)
        else
            stringResource(id = R.string.create_product),
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ){
                ExposedDropdownMenuBox(
                    modifier = Modifier.weight(2.5f),
                    expanded = addEditProductViewModel.expanded,
                    onExpandedChange = {
                        addEditProductViewModel.expanded = !addEditProductViewModel.expanded
                    }
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditProductViewModel.addEditProductState.category.categoryName,
                        hint = "Category Name",
                        error = addEditProductViewModel.addEditProductState.categoryError,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = addEditProductViewModel.expanded
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = addEditProductViewModel.expanded,
                        onDismissRequest = {
                            addEditProductViewModel.expanded = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                    ) {
                        addEditProductViewModel.categories.collectAsState().value.forEachIndexed { index, category ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    addEditProductViewModel.onAddEditEvent(
                                        AddEditProductEvent.CategoryNameChanged(category)
                                    )
                                    addEditProductViewModel.expanded = false
                                }
                            ) {
                                Text(
                                    text = category.categoryName,
                                    style = MaterialTheme.typography.body1,
                                )
                            }

                            if(index != addEditProductViewModel.categories.collectAsState().value.size - 1) {
                                Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(SpaceSmall))

                Button(
                    onClick = {
                        navController.navigate(AddEditCategoryScreenDestination())
                    },
                    modifier = Modifier.sizeIn(
                        minHeight = 56.dp,
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = addEditProductViewModel.addEditProductState.productName,
                hint = "Product Name",
                error = addEditProductViewModel.addEditProductState.productNameError,
                onValueChange = {
                    addEditProductViewModel.onAddEditEvent(AddEditProductEvent.ProductNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = addEditProductViewModel.addEditProductState.productPrice,
                hint = "Product Price",
                keyboardType = KeyboardType.Number,
                error = addEditProductViewModel.addEditProductState.productPriceError,
                onValueChange = {
                    addEditProductViewModel.onAddEditEvent(AddEditProductEvent.ProductPriceChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = addEditProductViewModel.addEditProductState.productAvailability,
                    onCheckedChange = {
                        addEditProductViewModel.onAddEditEvent(AddEditProductEvent.ProductAvailabilityChanged)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = if(addEditProductViewModel.addEditProductState.productAvailability)
                        "Marked as available"
                    else
                        "Marked as unavailable",
                    style = MaterialTheme.typography.overline
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = {
                    if (!productId.isNullOrEmpty()) {
                        addEditProductViewModel.onAddEditEvent(AddEditProductEvent.UpdateProduct(productId))
                    } else {
                        addEditProductViewModel.onAddEditEvent(AddEditProductEvent.CreateNewProduct)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
            ) {
                Text(
                    text = if (!productId.isNullOrEmpty())
                        stringResource(id = R.string.edit_product).uppercase()
                    else
                        stringResource(id = R.string.create_product).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }

}