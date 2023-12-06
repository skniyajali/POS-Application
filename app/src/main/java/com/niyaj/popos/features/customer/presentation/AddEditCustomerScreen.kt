package com.niyaj.popos.features.customer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.ADD_EDIT_CUSTOMER_BUTTON
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_EMAIL_ERROR
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_EMAIL_FIELD
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_NAME_ERROR
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_NAME_FIELD
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_PHONE_ERROR
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_PHONE_FIELD
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import io.sentry.compose.SentryTraced

/**
 * Create a new customer or Update customer if [customerId] is provided via navigation
 *  Upon successful creation or updating the customer
 *  will return back the status as [String] to the Navigator.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
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

    SentryTraced(tag = "AddEditCustomerScreen") {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxWidth(),
            text = if (!customerId.isNullOrEmpty())
                stringResource(id = R.string.edit_customer)
            else
                stringResource(id = R.string.create_customer),
            icon = Icons.Default.PersonAdd,
            onClosePressed = {
                navController.navigateUp()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(CUSTOMER_PHONE_FIELD),
                    text = customerViewModel.addEditCustomerState.customerPhone,
                    label = "Customer Phone",
                    leadingIcon = Icons.Default.PhoneAndroid,
                    error = customerViewModel.addEditCustomerState.customerPhoneError,
                    errorTag = CUSTOMER_PHONE_ERROR,
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        customerViewModel.onCustomerEvent(CustomerEvent.CustomerPhoneChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier.testTag(CUSTOMER_NAME_FIELD),
                    text = customerViewModel.addEditCustomerState.customerName ?: "",
                    label = "Customer Name",
                    leadingIcon = Icons.Default.Badge,
                    error = customerViewModel.addEditCustomerState.customerNameError,
                    errorTag = CUSTOMER_NAME_ERROR,
                    onValueChange = {
                        customerViewModel.onCustomerEvent(CustomerEvent.CustomerNameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier.testTag(CUSTOMER_EMAIL_FIELD),
                    text = customerViewModel.addEditCustomerState.customerEmail ?: "",
                    label = "Customer Email",
                    leadingIcon = Icons.Default.Mail,
                    error = customerViewModel.addEditCustomerState.customerEmailError,
                    errorTag = CUSTOMER_EMAIL_ERROR,
                    onValueChange = {
                        customerViewModel.onCustomerEvent(CustomerEvent.CustomerEmailChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))

                StandardButtonFW(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ADD_EDIT_CUSTOMER_BUTTON),
                    text = if (!customerId.isNullOrEmpty()) stringResource(id = R.string.edit_customer)
                    else stringResource(id = R.string.create_customer),
                    icon = if (!customerId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        if (!customerId.isNullOrEmpty()) {
                            customerViewModel.onCustomerEvent(CustomerEvent.UpdateCustomer(customerId))
                        } else {
                            customerViewModel.onCustomerEvent(CustomerEvent.CreateNewCustomer)
                        }
                    },
                )
            }
        }
    }
}