package com.niyaj.popos.features.profile.presentation

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.BuildConfig
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeLarge
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.ImageStorageManager
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.NoteText
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardOutlinedButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddOnItemScreenDestination
import com.niyaj.popos.features.destinations.AddressScreenDestination
import com.niyaj.popos.features.destinations.AttendanceScreenDestination
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.ChargesScreenDestination
import com.niyaj.popos.features.destinations.CustomerScreenDestination
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ProductScreenDestination
import com.niyaj.popos.features.destinations.ReminderScreenDestination
import com.niyaj.popos.features.destinations.ReportScreenDestination
import com.niyaj.popos.features.destinations.UpdateProfileScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.IconBox
import com.niyaj.popos.features.order.presentation.components.TwoGridText
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.utils.Constants.RESTAURANT_LOGO
import com.niyaj.popos.utils.Constants.RESTAURANT_LOGO_NAME
import com.niyaj.popos.utils.Constants.RESTAURANT_PRINT_LOGO_NAME
import com.niyaj.popos.utils.isScrolled
import com.niyaj.popos.utils.toBitmap
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

/**
 * Profile Screen Composable
 * @author Sk Niyaj Ali
 *
 */
@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ProfileScreen(
    navController : NavController,
    scaffoldState : ScaffoldState,
    viewModel : ProfileViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<UpdateProfileScreenDestination, String>
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val info = viewModel.info.collectAsState().value
    val reslogo = info.getRestaurantLogo(context)
    val printLogo = info.getRestaurantPrintLogo(context)

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.onEvent(ProfileEvent.RefreshEvent)

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        )
    }

    fun checkForMediaPermission() {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    var showPrintLogo by rememberSaveable {
        mutableStateOf(false)
    }

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val result = ImageStorageManager.saveToInternalStorage(
                context,
                uri.toBitmap(context),
                RESTAURANT_LOGO_NAME
            )

            scope.launch {
                if (result) {
                    scaffoldState.snackbarHostState.showSnackbar("Images saved successfully.")
                    viewModel.onEvent(ProfileEvent.LogoChanged)
                }else {
                    scaffoldState.snackbarHostState.showSnackbar("Unable save image into storage.")
                }
            }
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val result = ImageStorageManager.saveToInternalStorage(
                context,
                uri.toBitmap(context),
                RESTAURANT_PRINT_LOGO_NAME
            )

            scope.launch {
                if (result) {
                    scaffoldState.snackbarHostState.showSnackbar("Print Image saved successfully.")
                    viewModel.onEvent(ProfileEvent.PrintLogoChanged)
                }else {
                    scaffoldState.snackbarHostState.showSnackbar("Unable save print image into storage.")
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        title = {
            Text(text = "Restaurant Details")
        },
        navActions = {
            IconButton(
                onClick = {
                    navController.navigate(UpdateProfileScreenDestination())
                }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            StandardFabButton(
                text = "",
                showScrollToTop = lazyListState.isScrolled,
                visible = false,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {},
            )
        },
        floatingActionButtonPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightColor6),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            item("Restaurant Info") {
                RestaurantCard(
                    info = info,
                    resLogo = reslogo,
                    printLogo = printLogo,
                    showPrintLogo = showPrintLogo,
                    onClickEdit = {
                        checkForMediaPermission()
                        resLogoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onClickChangePrintLogo = {
                        checkForMediaPermission()
                        printLogoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onClickViewPrintLogo = {
                        showPrintLogo = true
                    }
                )
            }

            item("Quick Links") {
                QuickLinks(navController = navController)
            }

            item("App Details") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                AppDetails()
            }
        }
    }
}

@Composable
fun RestaurantCard(
    modifier : Modifier = Modifier,
    info : RestaurantInfo,
    showPrintLogo : Boolean = false,
    @DrawableRes
    bannerRes : Int = R.drawable.banner,
    printLogo: Bitmap? = null,
    resLogo : Bitmap? = null,
    onClickEdit : () -> Unit,
    onClickChangePrintLogo : () -> Unit,
    onClickViewPrintLogo : () -> Unit,
) {
    val iconSize = 24.dp
    val offsetInPx = LocalDensity.current.run { (iconSize / 2).roundToPx() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .background(MaterialTheme.colors.onPrimary),
        shape = RoundedCornerShape(SpaceSmall),
        backgroundColor = MaterialTheme.colors.onPrimary,
        elevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = "Banner Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(
                            y = ProfilePictureSizeLarge / 2
                        )
                ) {
                    ProfileImage(
                        modifier = Modifier,
                        resLogo = resLogo
                    )

                    IconButton(
                        onClick = onClickEdit,
                        modifier = Modifier
                            .offset {
                                IntOffset(x = +offsetInPx, y = -offsetInPx)
                            }
                            .size(iconSize)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Change Image",
                            tint = MaterialTheme.colors.background
                        )
                    }
                }
            }

            RestaurantDetails(
                modifier = Modifier.padding(top = (ProfilePictureSizeLarge / 2)),
                info = info,
                printLogo = printLogo,
                showPrintLogo = showPrintLogo,
                onClickChangePrintLogo = onClickChangePrintLogo,
                onClickViewPrintLogo = onClickViewPrintLogo,
            )
        }
    }
}

@Composable
fun RestaurantDetails(
    modifier : Modifier = Modifier,
    info : RestaurantInfo,
    showPrintLogo : Boolean = false,
    printLogo : Bitmap? = null,
    onClickChangePrintLogo : () -> Unit,
    onClickViewPrintLogo : () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = info.name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = info.tagline,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Text(
            text = info.description,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Text(
            text = info.address,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            minLines = 2,
            modifier = Modifier.padding(horizontal = SpaceMini)
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = info.primaryPhone,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )

            if (info.secondaryPhone.isNotEmpty()) {
                Text(text = " / ")
                Text(
                    text = info.secondaryPhone,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        if (info.printLogo.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                NoteText(
                    text = "You have not set your print logo, Click below to set.",
                    onClick = onClickChangePrintLogo
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardButton(
                    text = "Set Image",
                    icon = Icons.Default.AddAPhoto,
                    onClick = onClickChangePrintLogo,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondaryVariant
                    )
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StandardOutlinedButton(
                    text = "Change",
                    icon = Icons.Default.AddToPhotos,
                    onClick = onClickChangePrintLogo,
                )

                Spacer(modifier = Modifier.width(SpaceSmall))

                StandardButton(
                    text = "View Image",
                    icon = Icons.Default.ImageSearch,
                    onClick = onClickViewPrintLogo
                )
            }
        }

        if (showPrintLogo && printLogo != null) {

            Spacer(modifier = Modifier.height(SpaceSmall))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(SpaceSmall),
                backgroundColor = LightColor6
            ) {
                Image(
                    bitmap = printLogo.asImageBitmap(),
                    contentDescription = "Print Logo",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun ProfileImage(
    modifier : Modifier = Modifier,
    defaultLogo : Int = RESTAURANT_LOGO.toInt(),
    resLogo : Bitmap? = null,
) {
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }

    val borderWidth = 4.dp

    if (resLogo == null) {
        Image(
            painter = painterResource(id = defaultLogo),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(ProfilePictureSizeLarge)
                .clip(CircleShape)
                .border(BorderStroke(borderWidth, rainbowColorsBrush), CircleShape)
        )
    } else {
        Image(
            bitmap = resLogo.asImageBitmap(),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(ProfilePictureSizeLarge)
                .clip(CircleShape)
                .border(BorderStroke(borderWidth, rainbowColorsBrush), CircleShape)
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickLinks(
    modifier : Modifier = Modifier,
    navController : NavController
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        maxItemsInEachRow = 2,
    ) {
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Cart",
            icon = Icons.Default.ShoppingCart,
            onClick = {
                navController.navigate(CartScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Orders",
            icon = Icons.Default.Inventory,
            onClick = {
                navController.navigate(OrderScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Expenses",
            icon = Icons.Default.Money,
            onClick = {
                navController.navigate(ExpensesScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Reports",
            icon = Icons.Default.Assessment,
            onClick = {
                navController.navigate(ReportScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Employee",
            icon = Icons.Default.People,
            onClick = {
                navController.navigate(EmployeeScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Attendance",
            icon = Icons.Default.CalendarMonth,
            onClick = {
                navController.navigate(AttendanceScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Payments",
            icon = Icons.Default.Money,
            onClick = {
                navController.navigate(AttendanceScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Category",
            icon = Icons.Default.Dns,
            onClick = {
                navController.navigate(ProductScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Products",
            icon = Icons.Default.Dns,
            onClick = {
                navController.navigate(ProductScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "AddOn",
            icon = Icons.Default.InsertLink,
            onClick = {
                navController.navigate(AddOnItemScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Charges",
            icon = Icons.Default.Sell,
            onClick = {
                navController.navigate(ChargesScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Address",
            icon = Icons.Default.Business,
            onClick = {
                navController.navigate(AddressScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Customer",
            icon = Icons.Default.PeopleAlt,
            onClick = {
                navController.navigate(CustomerScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Reminder",
            icon = Icons.Default.Notifications,
            onClick = {
                navController.navigate(ReminderScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "AddOn",
            icon = Icons.Default.InsertLink,
            onClick = {
                navController.navigate(AddOnItemScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Charges",
            icon = Icons.Default.Sell,
            onClick = {
                navController.navigate(ChargesScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Address",
            icon = Icons.Default.Business,
            onClick = {
                navController.navigate(AddressScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Customer",
            icon = Icons.Default.PeopleAlt,
            onClick = {
                navController.navigate(CustomerScreenDestination())
            }
        )
        QuickLink(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceMini),
            text = "Reminder",
            icon = Icons.Default.Notifications,
            onClick = {
                navController.navigate(ReminderScreenDestination())
            }
        )
    }
}

@Composable
fun QuickLink(
    modifier : Modifier = Modifier,
    text : String,
    icon : ImageVector,
    onClick : () -> Unit,
    elevation : Dp = 1.dp,
    backgroundColor : Color = MaterialTheme.colors.onPrimary,
    iconColor : Color = MaterialTheme.colors.secondaryVariant,
    textColor : Color = MaterialTheme.colors.onBackground,
) {
    IconBox(
        modifier = modifier.fillMaxWidth(),
        iconName = icon,
        text = text,
        elevation = elevation,
        backgroundColor = backgroundColor,
        iconColor = iconColor,
        textColor = textColor,
        onClick = onClick
    )
}


@Composable
fun AppDetails(
    modifier : Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
    ) {
        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        TwoGridText(
            textOne = "Application ID",
            textTwo = BuildConfig.APPLICATION_ID,
        )

        Spacer(modifier = Modifier.height(SpaceMini))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceMini))

        TwoGridText(
            textOne = "Version Name",
            textTwo = BuildConfig.VERSION_NAME
        )

        Spacer(modifier = Modifier.height(SpaceMini))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceMini))

        TwoGridText(
            textOne = "Version Code",
            textTwo = BuildConfig.VERSION_CODE.toString()
        )
    }
}