package com.niyaj.popos.features.customer.presentation.settings.import_contact


import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.ImportExport
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.customer.domain.model.Contact
import com.niyaj.popos.features.customer.presentation.components.ContactCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination(style = DestinationStyle.BottomSheet::class)
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImportContactScreen(
    navController: NavController = rememberNavController(),
    resultBackNavigator: ResultBackNavigator<String>,
    importContactViewModel: ImportContactViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val isChosen = importContactViewModel.onChoose

    val selectedContacts = importContactViewModel.selectedContacts.toList()

    val importedData = importContactViewModel.importedContacts.toList()

    val showImportedBtn = if(isChosen) selectedContacts.isNotEmpty() else importedData.isNotEmpty()

    var expanded by remember {
        mutableStateOf(false)
    }

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val contacts = mutableStateListOf<Contact>()
                    val data = ImportExport.readData<ImportContact>(context, it)

                    data.forEach { importContact ->
                        val toContact = importContact.toContact()

                        if (toContact != null) {
                            contacts.add(toContact)
                        }
                    }

                    importContactViewModel.onEvent(ImportContactEvent.ImportContactsData(contacts.toList()))
                }
            }
        }

    LaunchedEffect(key1 = true) {
        importContactViewModel.eventFlow.collect { event ->
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
        text = stringResource(id = R.string.import_customers),
        icon = Icons.Default.SaveAlt,
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
                        text = "Import " + if(isChosen) "${selectedContacts.size} Selected Contacts" else " All Contacts",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row {
                        StandardOutlinedChip(
                            text = "All",
                            isToggleable = isChosen,
                            isSelected = !isChosen,
                            onClick = {
                                importContactViewModel.onChoose = false
                                importContactViewModel.onEvent(ImportContactEvent.SelectAllContact)
                            }
                        )

                        Spacer(modifier = Modifier.width(SpaceMini))

                        StandardOutlinedChip(
                            text = "Choose",
                            isToggleable = !isChosen,
                            isSelected = isChosen,
                            onClick = {
                                importContactViewModel.onEvent(ImportContactEvent.OnChooseContact)
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
                        .then(if (expanded) Modifier.weight(1.1F) else Modifier),
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded = !expanded
                            },
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        StandardExpandable(
                            onExpandChanged = {
                                expanded = !expanded
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            expanded = expanded,
                            title = {
                                TextWithIcon(
                                    text = if(selectedContacts.isNotEmpty()) "${selectedContacts.size} Selected" else "Choose Contacts",
                                    icon = Icons.Default.Dns,
                                    isTitle = true
                                )
                            },
                            rowClickable = true,
                            trailing = {
                                IconButton(
                                    onClick = {
                                        importContactViewModel.onEvent(ImportContactEvent.SelectAllContact)
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Rule, contentDescription = "Select All Product")
                                }
                            },
                            expand = {  modifier: Modifier ->
                                IconButton(
                                    modifier = modifier,
                                    onClick = {
                                        expanded = !expanded
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
                                    itemsIndexed(
                                        items = importedData,
                                    ){ index, contact ->
                                        ContactCard(
                                            phoneNo = contact.phoneNo,
                                            contactName = contact.name,
                                            contactEmail = contact.email,
                                            doesSelected = selectedContacts.contains(contact.contactId),
                                            onSelectProduct = {
                                                importContactViewModel.onEvent(ImportContactEvent.SelectContact(contact.contactId))
                                            },
                                        )

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        if(index == importedData.size - 1) {
                                            Spacer(modifier = Modifier.height(
                                                ProfilePictureSizeSmall
                                            ))
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
                            importContactViewModel.onEvent(ImportContactEvent.ClearImportedContacts)
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
                                importContactViewModel.onEvent(ImportContactEvent.ImportContacts)
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

                        Text("Import ${if (isChosen) selectedContacts.size else "All"} Product".uppercase(), style = MaterialTheme.typography.button)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(SpaceMedium))

                StandardButton(
                    text = stringResource(id = R.string.open_file),
                    icon = Icons.Default.UploadFile,
                    onClick = {
                        scope.launch {
                            val result = ImportExport.openFile(context)
                            importLauncher.launch(result)
                        }
                    },
                )
            }
        }
    }
}