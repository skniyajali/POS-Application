package com.niyaj.popos.features.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.*
import com.niyaj.popos.features.destinations.*
import com.ramcosta.composedestinations.navigation.navigate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StandardDrawer(
    navController: NavController,
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val expanded = remember { mutableStateOf(false) }
    val settingsExpanded = remember { mutableStateOf(false) }
    val expensesExpanded = remember { mutableStateOf(false) }
    val partnersExpanded = remember { mutableStateOf(false) }
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
            DrawerHeader(navController)

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
                    text = "Orders",
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
                    text = "Reports",
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
                    text = "Expenses",
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
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = {
                        IconButton(onClick = {
                            ordersExpanded.value = !ordersExpanded.value
                        }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null
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
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = {
                        IconButton(onClick = {
                            expensesExpanded.value = !expensesExpanded.value
                        }) {
                            Icon(imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null)
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
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = {
                        IconButton(onClick = {
                            customersExpanded.value = !customersExpanded.value
                        }) {
                            Icon(imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null)
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
                    expanded = partnersExpanded.value,
                    onExpandChanged = {
                        partnersExpanded.value = it
                    },
                    title = {
                        Text(text = "Employee, Salary, Advance")
                    },
                    leading = {
                        Icon(
                            imageVector = Icons.Default.AllInbox,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = {
                        IconButton(onClick = {
                            partnersExpanded.value = !partnersExpanded.value
                        }) {
                            Icon(imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null)
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
                            contentDescription = null,
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
                            Icon(imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null)
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
                        Icon(imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondaryVariant)
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    },
                    expand = {
                        IconButton(onClick = {
                            settingsExpanded.value = !settingsExpanded.value
                        }) {
                            Icon(imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null)
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
                                text = "Print Settings",
                                icon = Icons.Default.Print,
                                selected = currentRoute == PrintSettingsScreenDestination.route,
                                iconColor = MaterialTheme.colors.secondary,
                                onClick = {
                                    navController.navigate(PrintSettingsScreenDestination())
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

                }
            )
        }

    }
}

@Composable
fun DrawerHeader(
    navController: NavController,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(id = R.drawable.only_logo),
                contentDescription = null,
                modifier = Modifier.size(ProfilePictureSizeSmall)
            )

            Spacer(modifier = Modifier.width(SpaceMedium))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.popos_highlight),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "- Pure And Tasty -",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        
        IconButton(
            onClick = {
               navController.navigate(ProfileScreenDestination)
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
    text: String = "",
    icon: ImageVector? = null,
    selected: Boolean = false,
    selectedColor: Color = MaterialTheme.colors.secondary,
    unselectedColor: Color = MaterialTheme.colors.surface,
    iconColor: Color = MaterialTheme.colors.secondaryVariant,
    cardElevation: Dp = 0.dp,
    onClick: () -> Unit = {},
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
                Icon(imageVector = icon,
                    contentDescription = null,
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