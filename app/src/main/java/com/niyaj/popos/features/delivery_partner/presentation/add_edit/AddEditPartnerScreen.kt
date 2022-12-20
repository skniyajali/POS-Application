package com.niyaj.popos.features.delivery_partner.presentation.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.Primary
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.delivery_partner.domain.util.PartnerStatus
import com.niyaj.popos.features.delivery_partner.domain.util.PartnerType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AddEditPartnerScreen(
    partnerId: String = "",
    navController: NavController = rememberNavController(),
    scaffoldState : ScaffoldState,
    addEditPartnerViewModel: AddEditPartnerViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    var dropdownToggled by remember {
        mutableStateOf(false)
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Primary,
            darkIcons = false
        )
    }

    LaunchedEffect(key1 = true) {
        addEditPartnerViewModel.eventFlow.collectLatest { event ->
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

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        title = {
            Text(
                text = if (partnerId.isEmpty()) "Create New Partner" else "Update Partner",
            )
        },
        showBackArrow = true,
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
        ){
            item(key = "partnerName") {
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = addEditPartnerViewModel.addEditState.partnerName,
                    hint = "Partner Name",
                    error = addEditPartnerViewModel.addEditState.partnerNameError,
                    onValueChange = {
                        addEditPartnerViewModel.onAddEditPartnerEvent(
                            AddEditPartnerEvent.PartnerNameChanged(
                                it
                            )
                        )
                    },
                )
            }

            item(key = "partnerPhone") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = addEditPartnerViewModel.addEditState.partnerPhone,
                    hint = "Partner Phone",
                    keyboardType = KeyboardType.Number,
                    error = addEditPartnerViewModel.addEditState.partnerPhoneError,
                    onValueChange = {
                        addEditPartnerViewModel.onAddEditPartnerEvent(
                            AddEditPartnerEvent.PartnerPhoneChanged(
                                it
                            )
                        )
                    },
                )
            }

            item(key = "partnerEmail") {
                Spacer(modifier = Modifier.height(SpaceSmall))
                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = addEditPartnerViewModel.addEditState.partnerEmail,
                    hint = "Partner Email",
                    keyboardType = KeyboardType.Email,
                    error = addEditPartnerViewModel.addEditState.partnerEmailError,
                    onValueChange = {
                        addEditPartnerViewModel.onAddEditPartnerEvent(
                            AddEditPartnerEvent.PartnerEmailChanged(
                                it
                            )
                        )
                    },
                )
            }

            item(key = "partnerPassword") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = addEditPartnerViewModel.addEditState.partnerPassword,
                    hint = "Partner Password",
                    error = addEditPartnerViewModel.addEditState.partnerPasswordError,
                    onValueChange = {
                        addEditPartnerViewModel.onAddEditPartnerEvent(
                            AddEditPartnerEvent.PartnerPasswordChanged(
                                it
                            )
                        )
                    },
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = addEditPartnerViewModel.passwordToggle.value,
                    onPasswordToggleClick = {
                        addEditPartnerViewModel.togglePassword(it)
                    },
                )
            }

            item(key = "partnerType") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                ExposedDropdownMenuBox(
                    expanded = addEditPartnerViewModel.expanded,
                    onExpandedChange = {
                        addEditPartnerViewModel.expanded = !addEditPartnerViewModel.expanded
                    }
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditPartnerViewModel.addEditState.partnerType,
                        hint = "Partner Type",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = addEditPartnerViewModel.expanded
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = addEditPartnerViewModel.expanded,
                        onDismissRequest = {
                            addEditPartnerViewModel.expanded = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditPartnerViewModel.onAddEditPartnerEvent(
                                    AddEditPartnerEvent.PartnerTypeChanged(
                                        PartnerType.FullTime.partnerType
                                    )
                                )
                                addEditPartnerViewModel.expanded = false
                            }
                        ) {
                            Text(
                                text = PartnerType.FullTime.partnerType,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditPartnerViewModel.onAddEditPartnerEvent(
                                    AddEditPartnerEvent.PartnerTypeChanged(
                                        PartnerType.PartTime.partnerType
                                    )
                                )
                                addEditPartnerViewModel.expanded = false
                            }
                        ) {
                            Text(
                                text = PartnerType.PartTime.partnerType,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }

            item(key = "partnerStatus") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                ExposedDropdownMenuBox(
                    expanded = dropdownToggled,
                    onExpandedChange = {
                        dropdownToggled = !dropdownToggled
                    }
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        text = addEditPartnerViewModel.addEditState.partnerStatus,
                        hint = "Partner Status",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = dropdownToggled
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = dropdownToggled,
                        onDismissRequest = {
                            dropdownToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditPartnerViewModel.onAddEditPartnerEvent(
                                    AddEditPartnerEvent.PartnerStatusChanged(
                                        PartnerStatus.InActive.partnerStatus
                                    )
                                )
                                dropdownToggled = false
                            }
                        ) {
                            Text(
                                text = PartnerStatus.InActive.partnerStatus,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditPartnerViewModel.onAddEditPartnerEvent(
                                    AddEditPartnerEvent.PartnerStatusChanged(
                                        PartnerStatus.Active.partnerStatus
                                    )
                                )
                                dropdownToggled = false
                            }
                        ) {
                            Text(
                                text = PartnerStatus.Active.partnerStatus,
                                style = MaterialTheme.typography.body1,
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)

                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                addEditPartnerViewModel.onAddEditPartnerEvent(
                                    AddEditPartnerEvent.PartnerStatusChanged(
                                        PartnerStatus.Suspended.partnerStatus
                                    )
                                )
                                dropdownToggled = false
                            }
                        ) {
                            Text(
                                text = PartnerStatus.Suspended.partnerStatus,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }

            item(key = "partnerButton"){
                Spacer(modifier = Modifier.height(SpaceSmall))

                Button(
                    onClick = {
                        if (partnerId.isNotEmpty()) {
                            addEditPartnerViewModel.onAddEditPartnerEvent(
                                AddEditPartnerEvent.UpdatePartner(
                                    partnerId
                                )
                            )
                        } else {
                            addEditPartnerViewModel.onAddEditPartnerEvent(AddEditPartnerEvent.CreateNewPartner)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ButtonSize),
                ) {
                    Text(
                        text =
                        if (partnerId.isNotEmpty())
                            stringResource(id = R.string.update_partner).uppercase()
                        else
                            stringResource(id = R.string.create_new_partner).uppercase(),
                        style = MaterialTheme.typography.button,
                    )
                }
            }
        }
    }
}