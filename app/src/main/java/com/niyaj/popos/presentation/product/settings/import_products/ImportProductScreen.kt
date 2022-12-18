package com.niyaj.popos.presentation.product.settings.import_products

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.*
import com.niyaj.popos.presentation.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.presentation.product.components.ProductCard
import com.niyaj.popos.presentation.ui.theme.*
import com.niyaj.popos.presentation.util.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination(style = DestinationStyle.BottomSheet::class)
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ImportProductScreen(
    navController: NavController = rememberNavController(),
    resultBackNavigator: ResultBackNavigator<String>,
    importProductViewModel: ImportProductViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val isChosen = importProductViewModel.onChoose

    val selectedProducts = importProductViewModel.selectedProducts.toList()

    val importedData = importProductViewModel.importedProducts.toList()
    val groupedImportedProducts = importedData.toList().groupBy { it.category }

    val showImportedBtn = if(isChosen) selectedProducts.isNotEmpty() else importedData.isNotEmpty()

    var onImportedFile by remember {
        mutableStateOf(false)
    }

    var productsExpanded by remember {
        mutableStateOf(false)
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
        importProductViewModel.eventFlow.collect { event ->
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
        text = stringResource(id = com.niyaj.popos.R.string.import_products),
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Import " + if(isChosen) "${selectedProducts.size} Selected Products" else " All Products",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row {
                        StandardChip(
                            text = "All",
                            isToggleable = isChosen,
                            isSelected = !isChosen,
                            onClick = {
                                importProductViewModel.onChoose = false
                                importProductViewModel.onEvent(ImportProductEvent.SelectAllProduct)
                            }
                        )

                        Spacer(modifier = Modifier.width(SpaceMini))

                        StandardChip(
                            text = "Choose",
                            isToggleable = !isChosen,
                            isSelected = isChosen,
                            onClick = {
                                importProductViewModel.onEvent(ImportProductEvent.OnChooseProduct)
                            }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isChosen,
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
                                        importProductViewModel.onEvent(ImportProductEvent.SelectAllProduct)
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
                                    groupedImportedProducts.forEach{ (category, products_new) ->
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
                                                text = category.categoryName,
                                                count = products_new.count(),
                                                onClick = {
                                                    importProductViewModel.onEvent(ImportProductEvent.SelectProducts(products_new.map { it.productId }))
                                                }
                                            )
                                        }

                                        itemsIndexed(
                                            items = products_new,
                                        ){ index, product ->
                                            ProductCard(
                                                productName = product.productName,
                                                productPrice = product.productPrice.toString(),
                                                productCategoryName = product.category.categoryName,
                                                doesSelected = selectedProducts.contains(product.productId),
                                                isAvailable = product.productAvailability,
                                                onSelectProduct = {
                                                    importProductViewModel.onEvent(ImportProductEvent.SelectProduct(product.productId))
                                                },
                                            )

                                            Spacer(modifier = Modifier.height(SpaceSmall))

                                            if(index == importedData.size - 1) {
                                                Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }

            if(importedData.isNotEmpty()){
                Spacer(modifier = Modifier.height(SpaceMedium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            importProductViewModel.onEvent(ImportProductEvent.ClearImportedProducts)
                        },
                        modifier = Modifier
                            .heightIn(ButtonSize),
                        border = BorderStroke(1.dp, MaterialTheme.colors.error),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.error
                        ),
                        shape = RoundedCornerShape(SpaceMini),
                    ) {
                        Text(text = "Cancel".uppercase())
                    }

                    Spacer(modifier = Modifier.width(SpaceSmall))

                    Button(
                        onClick = {
                            scope.launch {
                                importProductViewModel.onEvent(ImportProductEvent.ImportProducts)
                            }
                        },
                        modifier = Modifier
                            .heightIn(ButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondaryVariant
                        ),
                        shape = RoundedCornerShape(SpaceMini),
                        enabled = showImportedBtn
                    ) {
                        Icon(
                            imageVector = Icons.Default.SaveAlt,
                            contentDescription = "Import Data",
                        )

                        Spacer(modifier = Modifier.width(SpaceSmall))

                        Text("Import ${if (isChosen) selectedProducts.size else "All"} Product".uppercase(), style = MaterialTheme.typography.button)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(SpaceMedium))

                Button(
                    onClick = {
                        scope.launch {
                            val result = ImportExport.openFile()
                            importLauncher.launch(result)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ButtonSize),
                    enabled = true
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Import Products",
                    )

                    Spacer(modifier = Modifier.width(SpaceMini))

                    Text(
                        text = stringResource(id = com.niyaj.popos.R.string.open_file).uppercase(),
                        style = MaterialTheme.typography.button,
                    )
                }
            }
        }
    }
}