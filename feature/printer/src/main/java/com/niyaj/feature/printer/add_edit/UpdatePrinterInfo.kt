package com.niyaj.feature.printer.add_edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.Margin
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.WidthNormal
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.PrinterInfoTestTags.ADDRESS_REPORT_ERROR
import com.niyaj.common.tags.PrinterInfoTestTags.ADDRESS_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.ADDRESS_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.CUSTOMER_REPORT_ERROR
import com.niyaj.common.tags.PrinterInfoTestTags.CUSTOMER_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.CUSTOMER_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_DPI_ERROR_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_DPI_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_DPI_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NBR_ERROR
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NBR_LINES_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NBR_LINES_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_PRODUCT_NAME_ERROR
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_PRODUCT_NAME_LENGTH_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_PRODUCT_NAME_LENGTH_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_WIDTH_ERROR_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_WIDTH_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_WIDTH_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_LOGO_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_QR_CODE_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_WELCOME_TEXT_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_REPORT_ERROR
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_BUTTON
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardCheckboxWithText
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(
    route = Screens.UPDATE_PRINTER_INFO_SCREEN,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun UpdatePrinterInfo(
    navController: NavController,
    viewModel: UpdatePrinterInfoViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val state = rememberLazyListState()

    val dpiError = viewModel.dpiError.collectAsStateWithLifecycle().value
    val widthError = viewModel.widthError.collectAsStateWithLifecycle().value
    val nbrError = viewModel.nbrError.collectAsStateWithLifecycle().value
    val nameLengthError = viewModel.nameLengthError.collectAsStateWithLifecycle().value
    val productLimitError = viewModel.productLimitError.collectAsStateWithLifecycle().value
    val addressLimitError = viewModel.addressLimitError.collectAsStateWithLifecycle().value
    val customerLimitError = viewModel.customerLimitError.collectAsStateWithLifecycle().value

    val hasError = viewModel.hasError.collectAsStateWithLifecycle().value

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = UPDATE_PRINTER_BUTTON)
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                elevation = 0.dp,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !hasError,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    }
                ),
            ) {
                StandardButtonFW(
                    modifier = Modifier.padding(SpaceSmall),
                    text = UPDATE_PRINTER_BUTTON,
                    onClick = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.UpdatePrinterInfo)
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            state = state,
        ) {
            item(PRINTER_DPI_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.printerDpi.toString(),
                    leadingIcon = Icons.Default.DensityMedium,
                    label = PRINTER_DPI_FIELD,
                    error = dpiError,
                    errorTag = PRINTER_DPI_ERROR_FIELD,
                    message = PRINTER_DPI_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterDpiChanged(it))
                    }
                )
            }

            item(PRINTER_WIDTH_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.printerWidth.toString(),
                    label = PRINTER_WIDTH_FIELD,
                    leadingIcon = Icons.Default.WidthNormal,
                    error = widthError,
                    errorTag = PRINTER_WIDTH_ERROR_FIELD,
                    message = PRINTER_WIDTH_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterWidthChanged(it))
                    }
                )
            }

            item(PRINTER_NBR_LINES_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.printerNbrLines.toString(),
                    label = PRINTER_NBR_LINES_FIELD,
                    leadingIcon = Icons.Default.ViewHeadline,
                    error = nbrError,
                    errorTag = PRINTER_NBR_ERROR,
                    message = PRINTER_NBR_LINES_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterNbrLinesChanged(it))
                    }
                )
            }

            item(PRINTER_PRODUCT_NAME_LENGTH_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.productNameLength.toString(),
                    label = PRINTER_PRODUCT_NAME_LENGTH_FIELD,
                    leadingIcon = Icons.Default.Margin,
                    error = nameLengthError,
                    errorTag = PRINTER_PRODUCT_NAME_ERROR,
                    message = PRINTER_PRODUCT_NAME_LENGTH_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductNameLengthChanged(it))
                    }
                )
            }

            item(PRODUCT_REPORT_LIMIT_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.productWiseReportLimit.toString(),
                    label = PRODUCT_REPORT_LIMIT_FIELD,
                    leadingIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    error = productLimitError,
                    errorTag = PRODUCT_REPORT_ERROR,
                    message = PRODUCT_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductReportLimitChanged(it))
                    }
                )
            }

            item(ADDRESS_REPORT_LIMIT_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.addressWiseReportLimit.toString(),
                    label = ADDRESS_REPORT_LIMIT_FIELD,
                    leadingIcon = Icons.Default.Receipt,
                    error = addressLimitError,
                    errorTag = ADDRESS_REPORT_ERROR,
                    message = ADDRESS_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.AddressReportLimitChanged(it))
                    }
                )
            }

            item(CUSTOMER_REPORT_LIMIT_FIELD) {
                StandardOutlinedTextField(
                    text = viewModel.state.customerWiseReportLimit.toString(),
                    label = CUSTOMER_REPORT_LIMIT_FIELD,
                    leadingIcon = Icons.Default.Receipt,
                    error = customerLimitError,
                    errorTag = CUSTOMER_REPORT_ERROR,
                    message = CUSTOMER_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.CustomerReportLimitChanged(it))
                    }
                )
            }

            item(PRINT_QR_CODE_IN_BILL) {
                StandardCheckboxWithText(
                    text = PRINT_QR_CODE_IN_BILL,
                    checked = viewModel.state.printQRCode,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintQrCodeChanged)
                    }
                )
            }

            item(PRINT_LOGO_IN_BILL) {
                StandardCheckboxWithText(
                    text = PRINT_LOGO_IN_BILL,
                    checked = viewModel.state.printResLogo,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintResLogoChanged)
                    }
                )
            }

            item(PRINT_WELCOME_TEXT_IN_BILL) {
                StandardCheckboxWithText(
                    text = PRINT_WELCOME_TEXT_IN_BILL,
                    checked = viewModel.state.printWelcomeText,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintWelcomeTextChanged)
                    }
                )
            }
        }
    }
}