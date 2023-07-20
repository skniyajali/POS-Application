package com.niyaj.popos.features.printer_info.presentation.add_edit

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.Margin
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.WidthNormal
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardCheckboxWithText
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun UpdatePrinterInfo(
    navController : NavController,
    viewModel : UpdatePrinterInfoViewModel = hiltViewModel(),
    resultBackNavigator : ResultBackNavigator<String>
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

                is UiEvent.IsLoading -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.update_printer_info))
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
                    text = stringResource(id = R.string.update_printer_info),
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
            item {
                StandardOutlinedTextField(
                    text = viewModel.state.printerDpi.toString(),
                    leadingIcon = Icons.Default.DensityMedium,
                    label = "Printer DPI",
                    error = dpiError,
                    message = "Usually found in the back side of the printer",
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterDpiChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    text = viewModel.state.printerWidth.toString(),
                    label = "Printer Width",
                    leadingIcon = Icons.Default.WidthNormal,
                    error = widthError,
                    message = "The printer width in millimeters(mm)",
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterWidthChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    text = viewModel.state.printerNbrLines.toString(),
                    label = "Printer NBR Lines",
                    leadingIcon = Icons.Default.ViewHeadline,
                    error = nbrError,
                    message = "How many characters should be printed in one line",
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterNbrLinesChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    text = viewModel.state.productNameLength.toString(),
                    label = "Product Name Length",
                    leadingIcon = Icons.Default.Margin,
                    error = nameLengthError,
                    message = "Product name should be printed in one line",
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductNameLengthChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    text = viewModel.state.productWiseReportLimit.toString(),
                    label = "Product Report Limit",
                    leadingIcon = Icons.Default.ReceiptLong,
                    error = productLimitError,
                    message = "How many products should be printed in a report",
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductReportLimitChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    text = viewModel.state.addressWiseReportLimit.toString(),
                    label = "Address Report Limit",
                    leadingIcon = Icons.Default.Receipt,
                    error = addressLimitError,
                    message = "How many addresses should be printed in a report",
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.AddressReportLimitChanged(it))
                    }
                )
            }

            item {
                StandardOutlinedTextField(
                    text = viewModel.state.customerWiseReportLimit.toString(),
                    label = "Customer Report Limit",
                    leadingIcon = Icons.Default.Receipt,
                    error = customerLimitError,
                    message = "How many customers should be printed in a report",
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.CustomerReportLimitChanged(it))
                    }
                )
            }

            item {
                StandardCheckboxWithText(
                    text = "Print QR Code In Bill",
                    checked = viewModel.state.printQRCode,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintQrCodeChanged)
                    }
                )
            }

            item {
                StandardCheckboxWithText(
                    text = "Print Logo In Bill",
                    checked = viewModel.state.printResLogo,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintResLogoChanged)
                    }
                )
            }

            item {
                StandardCheckboxWithText(
                    text = "Print Welcome Text In Bill",
                    checked = viewModel.state.printWelcomeText,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintWelcomeTextChanged)
                    }
                )
            }
        }
    }
}