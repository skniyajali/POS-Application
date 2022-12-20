package com.niyaj.popos.features.charges.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.safeString
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.flow.collectLatest

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
        text = if (!chargesId.isNullOrEmpty())
            stringResource(id = R.string.edit_charges)
        else
            stringResource(id = R.string.create_new_charges),
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
                text = chargesViewModel.addEditState.chargesName,
                hint = "Charges Name",
                error = chargesViewModel.addEditState.chargesNameError,
                onValueChange = {
                    chargesViewModel.onChargesEvent(ChargesEvent.ChargesNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = chargesViewModel.addEditState.chargesPrice,
                hint = "AddOn Price",
                keyboardType = KeyboardType.Number,
                error = chargesViewModel.addEditState.chargesPriceError,
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

            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = {
                    if (!chargesId.isNullOrEmpty()) {
                        chargesViewModel.onChargesEvent(ChargesEvent.UpdateCharges(chargesId))
                    } else {
                        chargesViewModel.onChargesEvent(ChargesEvent.CreateNewCharges)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
            ) {
                Text(
                    text = if (!chargesId.isNullOrEmpty())
                        stringResource(id = R.string.edit_charges).uppercase()
                    else
                        stringResource(id = R.string.create_new_charges).uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}