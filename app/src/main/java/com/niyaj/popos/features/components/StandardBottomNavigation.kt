package com.niyaj.popos.features.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.niyaj.popos.features.common.ui.theme.PurpleHaze
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ReportScreenDestination

@Composable
fun StandardBottomNavigation(
    navController : NavController,
    showFab : Boolean = false,
) {
    val navItems = if (!showFab) {
        bottomNavItems.minusElement(BottomNavItem(route = "", contentDescription = ""))
    }else bottomNavItems

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val showBottomBar = currentRoute in listOf(
        MainFeedScreenDestination.route,
        OrderScreenDestination.route,
        ReportScreenDestination.route,
    )

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

val bottomNavItems: List<BottomNavItem> = listOf(
    BottomNavItem(
        route = MainFeedScreenDestination.route,
        selectedIcon = Icons.Rounded.Home,
        deselectedIcon = Icons.Outlined.Home,
        contentDescription = "Home"
    ),
    BottomNavItem(
        route = CartScreenDestination.route,
        selectedIcon = Icons.Rounded.ShoppingCart,
        deselectedIcon = Icons.Outlined.ShoppingCart,
        contentDescription = "Cart"
    ),
    BottomNavItem(route = "", contentDescription = ""),
    BottomNavItem(
        route = OrderScreenDestination.route,
        selectedIcon = Icons.Rounded.Inventory2,
        deselectedIcon = Icons.Outlined.Inventory2,
        contentDescription = "Order"
    ),
    BottomNavItem(
        route = ReportScreenDestination.route,
        selectedIcon = Icons.Rounded.BarChart,
        deselectedIcon = Icons.Outlined.BarChart,
        contentDescription = "Report"
    ),
)

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector? = null,
    val deselectedIcon: ImageVector? = null,
    val contentDescription: String,
)