package com.niyaj.popos.features.product.presentation.settings.export_products

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.ImportExport.createFile
import com.niyaj.popos.features.common.util.ImportExport.writeData
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardChip
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.TextWithCount
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.product.presentation.components.ProductCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Destination(style = DestinationStyle.BottomSheet::class)
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ExportProductScreen(
    navController: NavController = rememberNavController(),
    resultBackNavigator: ResultBackNavigator<String>,
    exportProductViewModel: ExportProductViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val isChoose = exportProductViewModel.onChoose

    val products = exportProductViewModel.products.collectAsState().value.products
    val groupedProducts = exportProductViewModel.products.collectAsState().value.products.groupBy {
        it.category
    }

    val selectedProducts = exportProductViewModel.selectedProducts.toList()

    val exportedData = exportProductViewModel.exportedProducts.collectAsState().value

    val showFileSelector = if(isChoose) selectedProducts.isNotEmpty() else products.isNotEmpty()

    var productsExpanded by remember {
        mutableStateOf(false)
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                scope.launch {
                    val result = writeData(context, it, exportedData)

                    if(result){
                        resultBackNavigator.navigateBack("${exportedData.size} Products has been exported")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export products")
                    }
                }
            }
        }

    LaunchedEffect(key1 = true) {
        exportProductViewModel.eventFlow.collect { event ->
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
        text = stringResource(id = R.string.export_products),
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Export " + if(isChoose) "${selectedProducts.size} Selected Products" else " All Products",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold
                )

                Row {
                    StandardChip(
                        text = "All",
                        isToggleable = true,
                        isSelected = !isChoose,
                        onClick = {
                            exportProductViewModel.onChoose = false
                            exportProductViewModel.onEvent(ExportProductEvent.DeselectProducts)
                        }
                    )

                    Spacer(modifier = Modifier.width(SpaceMini))

                    StandardChip(
                        text = "Choose",
                        isToggleable = true,
                        isSelected = isChoose,
                        onClick = {
                            exportProductViewModel.onEvent(ExportProductEvent.OnChooseProduct)
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = isChoose,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (productsExpanded) Modifier.weight(1.1F) else Modifier),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            productsExpanded = !productsExpanded
                        },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    StandardExpandable(
                        onExpandChanged = {
                            productsExpanded = !productsExpanded
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        expanded = productsExpanded,
                        title = {
                            TextWithIcon(
                                text = if(selectedProducts.isNotEmpty()) "${selectedProducts.size} Selected" else "Choose Products",
                                icon = Icons.Default.Dns,
                                isTitle = true
                            )
                        },
                        rowClickable = true,
                        trailing = {
                            IconButton(
                                onClick = {
                                    exportProductViewModel.onEvent(ExportProductEvent.SelectAllProduct)
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Rule, contentDescription = "Select All Product")
                            }
                        },
                        expand = {  modifier: Modifier ->
                            IconButton(
                                modifier = modifier,
                                onClick = {
                                    productsExpanded = !productsExpanded
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
                            LazyColumn(
                                state = lazyListState,
                            ){
                                groupedProducts.forEach{ (category, products_new) ->
                                    stickyHeader {
                                        TextWithCount(
                                            modifier = Modifier
                                                .background(
                                                    if (showScrollToTop.value)
                                                        MaterialTheme.colors.onPrimary
                                                    else Color.Transparent
                                                )
                                                .clip(
                                                    RoundedCornerShape(if (showScrollToTop.value) 4.dp else 0.dp)
                                                ),
                                            text = category?.categoryName!!,
                                            count = products_new.count(),
                                            onClick = {
                                                exportProductViewModel.onEvent(ExportProductEvent.SelectProducts(products_new.map { it.productId }))
                                            }
                                        )
                                    }

                                    itemsIndexed(
                                        items = products_new,
                                    ){ index, product ->
                                        ProductCard(
                                            productName = product.productName,
                                            productPrice = product.productPrice.toString(),
                                            productCategoryName = product.category?.categoryName!!,
                                            doesSelected = selectedProducts.contains(product.productId),
                                            isAvailable = product.productAvailability,
                                            onSelectProduct = {
                                                exportProductViewModel.onEvent(ExportProductEvent.SelectProduct(product.productId))
                                            },
                                        )

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        if(index == products.size - 1) {
                                            Spacer(modifier = Modifier.height(
                                                ProfilePictureSizeSmall
                                            ))
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            Spacer(modifier = Modifier.height(SpaceMedium))

            Button(
                onClick = {
                    scope.launch {
                        val result = createFile(fileName = "products")
                        exportLauncher.launch(result)
                        exportProductViewModel.onEvent(ExportProductEvent.GetExportedProduct)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
                enabled = showFileSelector
            ) {
                Icon(
                    imageVector = Icons.Default.SaveAlt,
                    contentDescription = "Export Products",
                    modifier = Modifier.rotate(180F)
                )

                Spacer(modifier = Modifier.width(SpaceMini))

                Text(
                    text = stringResource(id = R.string.export_products).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}