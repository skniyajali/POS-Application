package com.niyaj.popos.features.charges.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.ADD_EDIT_CHARGES_BUTTON
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_AMOUNT_ERROR
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_AMOUNT_FIELD
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_APPLIED_SWITCH
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_NAME_ERROR
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_NAME_FIELD
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.common.util.safeString
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest

/**
 * Add Edit Changes Screen
 * @author Sk Niyaj Ali
 * @param chargesId
 * @param navController
 * @param chargesViewModel
 * @param resultBackNavigator
 * @see ChargesViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditChargesScreen(
    chargesId: String? = "",
    navController: NavController = rememberNavController(),
    chargesViewModel: ChargesViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    LaunchedEffect(key1 = true) {
        chargesViewModel.eventFlow.collectLatest { event ->
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

    SentryTraced(tag = "AddEditChargesScreen") {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxWidth(),
            text = if (!chargesId.isNullOrEmpty())
                stringResource(id = R.string.edit_charges)
            else
                stringResource(id = R.string.create_new_charges),
            icon = Icons.Default.Bolt,
            onClosePressed = {
                navController.navigateUp()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                StandardOutlinedTextField(
                    modifier = Modifier.testTag(CHARGES_NAME_FIELD),
                    text = chargesViewModel.addEditState.chargesName,
                    hint = "Charges Name",
                    leadingIcon = Icons.Default.Badge,
                    error = chargesViewModel.addEditState.chargesNameError,
                    errorTag = CHARGES_NAME_ERROR,
                    onValueChange = {
                        chargesViewModel.onChargesEvent(ChargesEvent.ChargesNameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier.testTag(CHARGES_AMOUNT_FIELD),
                    text = chargesViewModel.addEditState.chargesPrice,
                    hint = "Charges Amount",
                    leadingIcon = Icons.Default.CurrencyRupee,
                    keyboardType = KeyboardType.Number,
                    error = chargesViewModel.addEditState.chargesPriceError,
                    errorTag = CHARGES_AMOUNT_ERROR,
                    onValueChange = {
                        chargesViewModel.onChargesEvent(ChargesEvent.ChargesPriceChanged(safeString(it).toString()))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        modifier = Modifier.testTag(CHARGES_APPLIED_SWITCH),
                        checked = chargesViewModel.addEditState.chargesApplicable,
                        onCheckedChange = {
                            chargesViewModel.onChargesEvent(ChargesEvent.ChargesApplicableChanged)
                        }
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if(chargesViewModel.addEditState.chargesApplicable)
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
                    text = if (!chargesId.isNullOrEmpty()) stringResource(id = R.string.edit_charges)
                    else stringResource(id = R.string.create_new_charges),
                    icon = if (!chargesId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                    onClick = {
                        if (!chargesId.isNullOrEmpty()) {
                            chargesViewModel.onChargesEvent(ChargesEvent.UpdateCharges(chargesId))
                        } else {
                            chargesViewModel.onChargesEvent(ChargesEvent.CreateNewCharges)
                        }
                    },
                )
            }
        }
    }
}