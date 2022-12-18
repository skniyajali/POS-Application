package com.niyaj.popos.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.StandardOutlinedTextField
import com.niyaj.popos.presentation.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.presentation.ui.theme.ButtonSize
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditCustomerScreen(
    customerId: String? = "",
    navController: NavController = rememberNavController(),
    customerViewModel: CustomerViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    LaunchedEffect(key1 = true) {
        customerViewModel.eventFlow.collect { event ->
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
        text = if (!customerId.isNullOrEmpty())
            stringResource(id = R.string.edit_customer)
        else
            stringResource(id = R.string.create_customer),
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StandardOutlinedTextField(
                modifier = Modifier,
                text = customerViewModel.addEditCustomerState.customerPhone,
                hint = "Customer Phone",
                error = customerViewModel.addEditCustomerState.customerPhoneError,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    customerViewModel.onCustomerEvent(CustomerEvent.CustomerPhoneChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = customerViewModel.addEditCustomerState.customerName ?: "",
                hint = "Customer Name",
                error = customerViewModel.addEditCustomerState.customerNameError,
                onValueChange = {
                    customerViewModel.onCustomerEvent(CustomerEvent.CustomerNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = customerViewModel.addEditCustomerState.customerEmail ?: "",
                hint = "Customer Email",
                error = customerViewModel.addEditCustomerState.customerEmailError,
                onValueChange = {
                    customerViewModel.onCustomerEvent(CustomerEvent.CustomerEmailChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = {
                    if (!customerId.isNullOrEmpty()) {
                        customerViewModel.onCustomerEvent(CustomerEvent.UpdateCustomer(customerId))
                    } else {
                        customerViewModel.onCustomerEvent(CustomerEvent.CreateNewCustomer)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
            ) {
                Text(
                    text = if (!customerId.isNullOrEmpty())
                        stringResource(id = R.string.edit_customer).uppercase()
                    else
                        stringResource(id = R.string.create_customer).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}