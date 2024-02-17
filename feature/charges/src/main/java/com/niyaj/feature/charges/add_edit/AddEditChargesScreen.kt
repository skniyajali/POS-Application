package com.niyaj.feature.charges.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ChargesTestTags.ADD_EDIT_CHARGES_BUTTON
import com.niyaj.common.tags.ChargesTestTags.CHARGES_AMOUNT_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_AMOUNT_FIELD
import com.niyaj.common.tags.ChargesTestTags.CHARGES_APPLIED_SWITCH
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_FIELD
import com.niyaj.common.tags.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.common.tags.ChargesTestTags.UPDATE_CHARGES
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.charges.ChargesViewModel
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.flow.collectLatest

/**
 * Add Edit Changes Screen
 * @author Sk Niyaj Ali
 * @param chargesId
 * @param navController
 * @param viewModel
 * @param resultBackNavigator
 * @see ChargesViewModel
 */
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditChargesScreen(
    chargesId: String? = "",
    navController: NavController = rememberNavController(),
    viewModel: AddEditChargesViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null && priceError == null

    val title = if (!chargesId.isNullOrEmpty()) UPDATE_CHARGES else CREATE_NEW_CHARGES

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
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
        text = title,
        icon = Icons.Default.Bolt,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            StandardOutlinedTextField(
                modifier = Modifier.testTag(CHARGES_NAME_FIELD),
                text = viewModel.addEditState.chargesName,
                label = CHARGES_NAME_FIELD,
                leadingIcon = Icons.Default.Badge,
                error = nameError,
                errorTag = CHARGES_NAME_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier.testTag(CHARGES_AMOUNT_FIELD),
                text = viewModel.addEditState.chargesPrice,
                label = CHARGES_AMOUNT_FIELD,
                leadingIcon = Icons.Default.CurrencyRupee,
                keyboardType = KeyboardType.Number,
                error = priceError,
                errorTag = CHARGES_AMOUNT_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    modifier = Modifier.testTag(CHARGES_APPLIED_SWITCH),
                    checked = viewModel.addEditState.chargesApplicable,
                    onCheckedChange = {
                        viewModel.onEvent(AddEditChargesEvent.ChargesApplicableChanged)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = if (viewModel.addEditState.chargesApplicable)
                        "Marked as applied"
                    else
                        "Marked as not applied",
                    style = MaterialTheme.typography.overline
                )
            }

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButtonFW(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_CHARGES_BUTTON),
                text = title,
                enabled = enableBtn,
                icon = if (!chargesId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    viewModel.onEvent(AddEditChargesEvent.CreateOrUpdateCharges)
                },
            )
        }
    }
}