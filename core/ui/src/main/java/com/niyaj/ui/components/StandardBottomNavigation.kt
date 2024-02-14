package com.niyaj.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.niyaj.designsystem.theme.PurpleHaze
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun StandardBottomNavigation(
    navController : NavController,
    showFab : Boolean = false,
) {
    val navItems = if (!showFab) {
        bottomNavItems.minusElement(BottomNavItem(route = "", contentDescription = ""))
    }else bottomNavItems

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val showBottomBar = true
//        currentRoute in listOf(
//        MainFeedScreenDestination.route,
//        OrderScreenDestination.route,
//        ReportScreenDestination.route,
//    )

    val shape = if (showFab) CircleShape else null

    if (showBottomBar) {
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(topStart = SpaceSmall, topEnd = SpaceSmall)),
            backgroundColor = PurpleHaze,
            cutoutShape = shape,
            elevation = 5.dp
        ) {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxSize(),
                backgroundColor = PurpleHaze,
            ) {
                navItems.forEach { bottomNavItem ->
                    StandardBottomNavItem(
                        selectedIcon = bottomNavItem.selectedIcon,
                        deselectedIcon = bottomNavItem.deselectedIcon,
                        contentDescription = bottomNavItem.contentDescription,
                        selected = bottomNavItem.route == navController.currentDestination?.route,
                        enabled = bottomNavItem.selectedIcon != null
                    ) {
                        if (navController.currentDestination?.route != bottomNavItem.route) {
                            navController.navigate(bottomNavItem.route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StandardBottomNavigation(
    currentRoute: String,
    showFab : Boolean = false,
    onNavigateToDestination: (String) -> Unit,
) {
    val navItems = if (!showFab) {
        bottomNavItems.minusElement(BottomNavItem(route = "", contentDescription = ""))
    }else bottomNavItems

    val showBottomBar = true
//        currentRoute in listOf(
//        MainFeedScreenDestination.route,
//        OrderScreenDestination.route,
//        ReportScreenDestination.route,
//    )

    val shape = if (showFab) CircleShape else null

    if (showBottomBar) {
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(topStart = SpaceSmall, topEnd = SpaceSmall)),
            backgroundColor = PurpleHaze,
            cutoutShape = shape,
            elevation = 5.dp
        ) {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxSize(),
                backgroundColor = PurpleHaze,
            ) {
                navItems.forEach { bottomNavItem ->
                    StandardBottomNavItem(
                        selectedIcon = bottomNavItem.selectedIcon,
                        deselectedIcon = bottomNavItem.deselectedIcon,
                        contentDescription = bottomNavItem.contentDescription,
                        selected = bottomNavItem.route == currentRoute,
                        enabled = bottomNavItem.selectedIcon != null
                    ) {
                        if (currentRoute != bottomNavItem.route) {
                            onNavigateToDestination(bottomNavItem.route)
                        }
                    }
                }
            }
        }
    }
}

val bottomNavItems: List<BottomNavItem> = emptyList()

//listOf(
//    BottomNavItem(
//        route = MainFeedScreenDestination.route,
//        selectedIcon = Icons.Rounded.Home,
//        deselectedIcon = Icons.Outlined.Home,
//        contentDescription = "Home"
//    ),
//    BottomNavItem(
//        route = CartScreenDestination.route,
//        selectedIcon = Icons.Rounded.ShoppingCart,
//        deselectedIcon = Icons.Outlined.ShoppingCart,
//        contentDescription = "Cart"
//    ),
//    BottomNavItem(route = "", contentDescription = ""),
//    BottomNavItem(
//        route = OrderScreenDestination.route,
//        selectedIcon = Icons.Rounded.Inventory2,
//        deselectedIcon = Icons.Outlined.Inventory2,
//        contentDescription = "Order"
//    ),
//    BottomNavItem(
//        route = ReportScreenDestination.route,
//        selectedIcon = Icons.Rounded.BarChart,
//        deselectedIcon = Icons.Outlined.BarChart,
//        contentDescription = "Report"
//    ),
//)

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector? = null,
    val deselectedIcon: ImageVector? = null,
    val contentDescription: String,
)