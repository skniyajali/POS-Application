package com.niyaj.feature.product.settings.import_products

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ProductTestTags.IMPORT_PRODUCTS_NOTE_TEXT
import com.niyaj.common.tags.ProductTestTags.IMPORT_PRODUCTS_TITLE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.product.components.ProductBody
import com.niyaj.model.Product
import com.niyaj.ui.components.ImportExportHeader
import com.niyaj.ui.components.ImportFooter
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Import products from file to database
 */
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ImportProductScreen(
    navController: NavController = rememberNavController(),
    resultBackNavigator: ResultBackNavigator<String>,
    importProductViewModel: ImportProductViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()

    val isChosen = importProductViewModel.onChoose

    val selectedProducts = importProductViewModel.selectedProducts.toList()

    val importedData = importProductViewModel.importedProducts.toList()
    val groupedImportedProducts = importedData.toList().groupBy { it.category?.categoryName }

    val showImportedBtn = if (isChosen) selectedProducts.isNotEmpty() else importedData.isNotEmpty()

    var onImportedFile by remember {
        mutableStateOf(false)
    }

    var productsExpanded by remember {
        mutableStateOf(false)
    }

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readData<Product>(context, it)

                    onImportedFile = data.isEmpty()

                    importProductViewModel.onEvent(ImportProductEvent.ImportProductsData(data))
                }
            }
        }

    LaunchedEffect(key1 = true) {
        importProductViewModel.eventFlow.collectLatest { event ->
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
        text = IMPORT_PRODUCTS_TITLE,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            if (importedData.isNotEmpty()) {
                ImportExportHeader(
                    modifier = Modifier,
                    text = "Import " + if (isChosen) "${selectedProducts.size} Selected Products" else " All Products",
                    onClickAll = {
                        importProductViewModel.onChoose = false
                        importProductViewModel.onEvent(ImportProductEvent.SelectAllProduct)
                    },
                    isChosen = isChosen,
                    onClickChoose = {
                        importProductViewModel.onEvent(ImportProductEvent.OnChooseProduct)
                    }
                )

                AnimatedVisibility(
                    visible = isChosen,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (productsExpanded) Modifier.weight(1.1F) else Modifier),
                ) {
                    ProductBody(
                        modifier = Modifier,
                        lazyListState = lazyListState,
                        lazyGridState = lazyGridState,
                        groupedProducts = groupedImportedProducts,
                        selectedProducts = selectedProducts,
                        onCategorySelect = {
                            importProductViewModel.onEvent(ImportProductEvent.SelectProducts(it))
                        },
                        onSelectProduct = {
                            importProductViewModel.onEvent(ImportProductEvent.SelectProduct(it))
                        },
                        productsExpanded = productsExpanded,
                        onExpandChange = {
                            productsExpanded = !productsExpanded
                        },
                        selectedProductsSize = selectedProducts.size,
                        onClickSelectAll = {
                            importProductViewModel.onEvent(ImportProductEvent.SelectAllProduct)
                        }
                    )
                }
            }

            ImportFooter(
                importButtonText = "Import ${if (isChosen) selectedProducts.size else "All"} Product",
                noteText = IMPORT_PRODUCTS_NOTE_TEXT,
                onClearImportedData = {
                    importProductViewModel.onEvent(ImportProductEvent.ClearImportedProducts)
                },
                onImportData = {
                    scope.launch {
                        importProductViewModel.onEvent(ImportProductEvent.ImportProducts)
                    }
                },
                importedDataIsEmpty = importedData.isNotEmpty(),
                showImportedBtn = showImportedBtn,
                onOpenFile = {
                    scope.launch {
                        val result = ImportExport.openFile(context)
                        importLauncher.launch(result)
                    }
                },
            )
        }

    }
}