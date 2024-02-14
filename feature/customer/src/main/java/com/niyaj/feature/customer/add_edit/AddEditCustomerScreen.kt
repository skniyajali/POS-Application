package com.niyaj.feature.customer.add_edit

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CustomerTestTags.ADD_EDIT_CUSTOMER_BUTTON
import com.niyaj.common.tags.CustomerTestTags.ADD_EDIT_CUSTOMER_SCREEN
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.common.tags.CustomerTestTags.EDIT_CUSTOMER_ITEM
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

/**
 * Create a new customer or Update customer if [customerId] is provided via navigation
 *  Upon successful creation or updating the customer
 *  will return back the status as [String] to the Navigator.
 */
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditCustomerScreen(
    customerId: String? = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditCustomerViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val phoneError = viewModel.phoneError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val emailError = viewModel.emailError.collectAsStateWithLifecycle().value

    val enableBtn = phoneError == null && nameError == null && emailError == null

    val title = if (customerId.isNullOrEmpty()) CREATE_NEW_CUSTOMER else EDIT_CUSTOMER_ITEM

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
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ADD_EDIT_CUSTOMER_SCREEN),
        text = title,
        icon = Icons.Default.PersonAdd,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            StandardOutlinedTextField(
                modifier = Modifier.testTag(CUSTOMER_PHONE_FIELD),
                text = viewModel.addEditState.customerPhone,
                label = CUSTOMER_PHONE_FIELD,
                leadingIcon = Icons.Default.PhoneAndroid,
                error = phoneError,
                errorTag = CUSTOMER_PHONE_ERROR,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged(it))
                },
            )

            StandardOutlinedTextField(
                modifier = Modifier.testTag(CUSTOMER_NAME_FIELD),
                text = viewModel.addEditState.customerName ?: "",
                label = CUSTOMER_NAME_FIELD,
                leadingIcon = Icons.Default.Badge,
                error = nameError,
                errorTag = CUSTOMER_NAME_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditCustomerEvent.CustomerNameChanged(it))
                },
            )

            StandardOutlinedTextField(
                modifier = Modifier.testTag(CUSTOMER_EMAIL_FIELD),
                text = viewModel.addEditState.customerEmail ?: "",
                label = CUSTOMER_EMAIL_FIELD,
                leadingIcon = Icons.Default.Mail,
                error = emailError,
                errorTag = CUSTOMER_EMAIL_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditCustomerEvent.CustomerEmailChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceMini))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_CUSTOMER_BUTTON),
                enabled = enableBtn,
                text = title,
                icon = if (!customerId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    viewModel.onEvent(AddEditCustomerEvent.CreateOrUpdateCustomer)
                },
            )
        }
    }

}