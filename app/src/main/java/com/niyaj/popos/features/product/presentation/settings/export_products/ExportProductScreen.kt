package com.niyaj.popos.features.product.presentation.settings.export_products

import android.Manifest
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.ImportExport.createFile
import com.niyaj.popos.features.common.util.ImportExport.writeData
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExportedFooter
import com.niyaj.popos.features.components.ImportExportHeader
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.product.presentation.components.ProductBody
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch

/**
 * Export products to file
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ExportProductScreen(
    navController : NavController = rememberNavController(),
    exportProductViewModel : ExportProductViewModel = hiltViewModel(),
    resultBackNavigator : ResultBackNavigator<String>,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()

    val context = LocalContext.current

    val hasStoragePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    )

    val isChoose = exportProductViewModel.onChoose

    val products = exportProductViewModel.products.collectAsStateWithLifecycle().value.products
    val groupedProducts = products.groupBy {
        it.category?.categoryName
    }

    val selectedProducts = exportProductViewModel.selectedProducts.toList()

    val exportedData = exportProductViewModel.exportedProducts.collectAsStateWithLifecycle().value

    val showFileSelector = if (isChoose) selectedProducts.isNotEmpty() else products.isNotEmpty()

    var productsExpanded by remember {
        mutableStateOf(false)
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyGridState.firstVisibleItemIndex > 0
        }
    }

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = writeData(context, it, exportedData)

                    if (result) {
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

    SentryTraced(tag = "ExportProductScreen") {
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
                ImportExportHeader(
                    text = "Export " + if (isChoose) "${selectedProducts.size} Selected Products" else " All Products",
                    isChosen = isChoose,
                    onClickChoose = {
                        exportProductViewModel.onEvent(ExportProductEvent.OnChooseProduct)
                    },
                    onClickAll = {
                        exportProductViewModel.onChoose = false
                        exportProductViewModel.onEvent(ExportProductEvent.DeselectProducts)
                    },
                )

                AnimatedVisibility(
                    visible = isChoose,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (productsExpanded) Modifier.weight(1.1F) else Modifier),
                ) {
                    ProductBody(
                        lazyListState = lazyListState,
                        lazyGridState = lazyGridState,
                        groupedProducts = groupedProducts,
                        onCategorySelect = {
                            exportProductViewModel.onEvent(ExportProductEvent.SelectProducts(it))
                        },
                        onSelectProduct = {
                            exportProductViewModel.onEvent(ExportProductEvent.SelectProduct(it))
                        },
                        onExpandChange = {
                            productsExpanded = !productsExpanded
                        },
                        selectedProducts = selectedProducts,
                        showScrollToTop = showScrollToTop.value,
                        productsExpanded = productsExpanded,
                        selectedProductsSize = selectedProducts.size,
                        onClickSelectAll = {
                            exportProductViewModel.onEvent(ExportProductEvent.SelectAllProduct)
                        },
                    )
                }

                ExportedFooter(
                    text = stringResource(id = R.string.export_products),
                    showFileSelector = showFileSelector,
                    onExportClick = {
                        scope.launch {
                            askForPermissions()
                            val result = createFile(context = context, fileName = "products")
                            exportLauncher.launch(result)
                            exportProductViewModel.onEvent(ExportProductEvent.GetExportedProduct)
                        }
                    }
                )
            }
        }
    }
}