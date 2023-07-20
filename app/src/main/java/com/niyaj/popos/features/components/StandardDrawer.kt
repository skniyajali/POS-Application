package com.niyaj.popos.features.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.popos.features.common.ui.theme.IconSizeLarge
import com.niyaj.popos.features.common.ui.theme.LightColor12
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceLarge
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.destinations.AddOnItemScreenDestination
import com.niyaj.popos.features.destinations.AddressScreenDestination
import com.niyaj.popos.features.destinations.AttendanceScreenDestination
import com.niyaj.popos.features.destinations.CartOrderScreenDestination
import com.niyaj.popos.features.destinations.CategoryScreenDestination
import com.niyaj.popos.features.destinations.ChargesScreenDestination
import com.niyaj.popos.features.destinations.CustomerScreenDestination
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.destinations.ExpensesCategoryScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.LoginScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.PrinterInfoScreenDestination
import com.niyaj.popos.features.destinations.ProductScreenDestination
import com.niyaj.popos.features.destinations.ProfileScreenDestination
import com.niyaj.popos.features.destinations.ReminderScreenDestination
import com.niyaj.popos.features.destinations.ReportScreenDestination
import com.niyaj.popos.features.destinations.SalaryScreenDestination
import com.niyaj.popos.features.destinations.SettingsScreenDestination
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.profile.presentation.ProfileEvent
import com.niyaj.popos.features.profile.presentation.ProfileViewModel
import com.ramcosta.composedestinations.navigation.navigate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StandardDrawer(
    navController : NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val info = viewModel.info.collectAsStateWithLifecycle().value

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    val expanded = remember { mutableStateOf(false) }
    val settingsExpanded = remember { mutableStateOf(false) }
    val expensesExpanded = remember { mutableStateOf(false) }
    val employeeExpanded = remember { mutableStateOf(false) }
    val customersExpanded = remember { mutableStateOf(false) }
    val ordersExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpaceMedium),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(0.3f)
        ) {
            DrawerHeader(navController, info)

            Spacer(modifier = Modifier.height(SpaceLarge))

            Divider(thickness = 1.dp, modifier = Modifier.background(LightColor12))
        }

        LazyColumn(
            modifier = Modifier.weight(2.5f)
        ) {
            item {
                Spacer(modifier = Modifier.height(SpaceMedium))

                DrawerItem(
                    text = "Home",
                    icon = Icons.Default.Home,
                    selected = currentRoute == MainFeedScreenDestination.route,
                    onClick = {
                        navController.navigate(MainFeedScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Orders",
                    icon = Icons.Default.Inventory,
                    selected = currentRoute == OrderScreenDestination.route,
                    onClick = {
                        navController.navigate(OrderScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Reports",
                    icon = Icons.Default.Assessment,
                    selected = currentRoute == ReportScreenDestination.route,
                    onClick = {
                        navController.navigate(ReportScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                DrawerItem(
                    text = "View Expenses",
                    icon = Icons.Default.StickyNote2,
                    selected = currentRoute == ExpensesScreenDestination.route,
                    onClick = {
                        navController.navigate(ExpensesScreenDestination())
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceMedium))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightColor12),
                )

                Spacer(modifier = Modifier.height(SpaceSmall))


                StandardExpandable(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = ordersExpanded.value,
                    onExpandChanged = {
                        ordersExpanded.value = it
                    },
                    title = {
                        Text(text = "Orders, Cart Orders..")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.AllInbox,
                            contentDescription = "Cart Order Icon",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier : Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                ordersExpanded.value = !ordersExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Cart Order"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Orders",
                                icon = Icons.Default.Inventory,
                                selected = currentRoute == OrderScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(OrderScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            DrawerItem(
                                text = "Cart Orders",
                                icon = Icons.Default.BreakfastDining,
                                selected = currentRoute == CartOrderScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(CartOrderScreenDestination())
                                }
                            )
                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardExpandable(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expensesExpanded.value,
                    onExpandChanged = {
                        expensesExpanded.value = it
                    },
                    title = {
                        Text(text = "All Expenses")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = "Expenses Icon",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier : Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                expensesExpanded.value = !expensesExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Expenses",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Expenses",
                                icon = Icons.Default.StickyNote2,
                                selected = currentRoute == ExpensesScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(ExpensesScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Expenses Category",
                                icon = Icons.Default.Category,
                                selected = currentRoute == ExpensesCategoryScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(ExpensesCategoryScreenDestination())
                                }
                            )
                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardExpandable(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = customersExpanded.value,
                    onExpandChanged = {
                        customersExpanded.value = it
                    },
                    title = {
                        Text(text = "Customers, Addresses")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Customers, Addresses Icon",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier : Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                customersExpanded.value = !customersExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Customer, Addresses"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Customers",
                                icon = Icons.Default.PeopleAlt,
                                selected = currentRoute == CustomerScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(CustomerScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Addresses",
                                icon = Icons.Default.Business,
                                selected = currentRoute == AddressScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(AddressScreenDestination())
                                }
                            )
                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardExpandable(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = employeeExpanded.value,
                    onExpandChanged = {
                        employeeExpanded.value = it
                    },
                    title = {
                        Text(text = "Employee, Salary, Advance")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.PeopleAlt,
                            contentDescription = "Employee, Salary, Advance Icon",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier : Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                employeeExpanded.value = !employeeExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Employee"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Employees",
                                icon = Icons.Default.SwitchAccount,
                                selected = currentRoute == EmployeeScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(EmployeeScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Employee Absent Report",
                                icon = Icons.Default.EventBusy,
                                selected = currentRoute == AttendanceScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(AttendanceScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Employee Payments",
                                icon = Icons.Default.Money,
                                selected = currentRoute == SalaryScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(SalaryScreenDestination())
                                }
                            )
                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardExpandable(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded.value,
                    onExpandChanged = {
                        expanded.value = it
                    },
                    title = {
                        Text(text = "Products, Categories..")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Products, Categories Icon",
                            tint = MaterialTheme.colors.secondaryVariant
                        )

                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                expanded.value = !expanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Product/Category",
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {

                            DrawerItem(
                                text = "Categories",
                                icon = Icons.Default.Category,
                                selected = currentRoute == CategoryScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(CategoryScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Products",
                                icon = Icons.Default.Dns,
                                selected = currentRoute == ProductScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(ProductScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "AddOn Item",
                                icon = Icons.Default.InsertLink,
                                selected = currentRoute == AddOnItemScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(AddOnItemScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Charges Item",
                                icon = Icons.Default.Bolt,
                                selected = currentRoute == ChargesScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(ChargesScreenDestination())
                                }
                            )

                        }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardExpandable(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = settingsExpanded.value,
                    onExpandChanged = {
                        settingsExpanded.value = it
                    },
                    title = {
                        Text(text = "App Settings, Reminders")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = { modifier : Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                settingsExpanded.value = !settingsExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand Settings"
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier.padding(SpaceSmall),
                        ) {
                            DrawerItem(
                                text = "Reminders",
                                icon = Icons.Default.Notifications,
                                selected = currentRoute == ReminderScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(ReminderScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            DrawerItem(
                                text = "App Settings",
                                icon = Icons.Default.Settings,
                                selected = currentRoute == SettingsScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(SettingsScreenDestination())
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            DrawerItem(
                                text = "Printer Information",
                                icon = Icons.Default.Print,
                                selected = currentRoute == PrinterInfoScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(PrinterInfoScreenDestination())
                                }
                            )
                        }
                    },
                )
            }
        }

        Column(
            modifier = Modifier.weight(0.2f)
        ) {

            Divider(thickness = 1.dp, modifier = Modifier.background(LightColor12))

            Spacer(modifier = Modifier.height(SpaceSmall))

            DrawerItem(
                text = "Logout",
                icon = Icons.Default.Logout,
                selected = false,
                onClick = {
                    viewModel.onEvent(ProfileEvent.LogoutProfile)

                    navController.navigate(LoginScreenDestination()){
                        popUpTo(navController.graph.id){
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun DrawerHeader(
    navController : NavController,
    info: RestaurantInfo,
) {
    val context = LocalContext.current

    val resImage = info.getRestaurantLogo(context)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (resImage != null) {
                Image(
                    bitmap = resImage.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(ProfilePictureSizeSmall)
                        .clip(RoundedCornerShape(SpaceMini))
                )
            }

            Spacer(modifier = Modifier.width(SpaceMedium))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = info.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = info.tagline,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        IconButton(
            onClick = {
                navController.navigate(ProfileScreenDestination())
            },
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "My Account",
                modifier = Modifier.size(IconSizeLarge),
                tint = MaterialTheme.colors.primary,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerItem(
    text : String = "",
    icon : ImageVector? = null,
    selected : Boolean = false,
    selectedColor : Color = MaterialTheme.colors.secondary,
    unselectedColor : Color = MaterialTheme.colors.surface,
    iconColor : Color = MaterialTheme.colors.secondaryVariant,
    cardElevation : Dp = 0.dp,
    onClick : () -> Unit = {},
) {
    Card(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = if (selected) selectedColor else unselectedColor,
        elevation = if (selected) 2.dp else cardElevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text.plus("Icon"),
                    tint = if (selected) MaterialTheme.colors.onPrimary else iconColor
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                color = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
            )
        }
    }
}