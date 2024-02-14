package com.niyaj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall


data class QuickLinksData(
    val text : String,
    val icon : ImageVector,
//    val direction : Direction,
)

val quickLinksData = emptyList<QuickLinksData>()
    /*
    listOf(
    QuickLinksData(
        text = "Cart",
        icon = Icons.Default.ShoppingCart,
        direction = CartScreenDestination()
    ),
    QuickLinksData(
        text = "Orders",
        icon = Icons.Default.Inventory,
        direction = OrderScreenDestination()
    ),
    QuickLinksData(
        text = "Expenses",
        icon = Icons.Default.Money,
        direction = ExpensesScreenDestination()
    ),
    QuickLinksData(
        text = "Reports",
        icon = Icons.Default.Assessment,
        direction = ReportScreenDestination()
    ),
    QuickLinksData(
        text = "Employee",
        icon = Icons.Default.People,
        direction = EmployeeScreenDestination()
    ),
    QuickLinksData(
        text = "Attendance",
        icon = Icons.Default.CalendarMonth,
        direction = AttendanceScreenDestination()
    ),
    QuickLinksData(
        text = "Payments",
        icon = Icons.Default.Money,
        direction = SalaryScreenDestination()
    ),
    QuickLinksData(
        text = "Category",
        icon = Icons.Default.Dns,
        direction = CategoryScreenDestination()
    ),
    QuickLinksData(
        text = "Products",
        icon = Icons.Default.Dns,
        direction = ProductScreenDestination()
    ),
    QuickLinksData(
        text = "AddOn",
        icon = Icons.Default.InsertLink,
        direction = AddOnItemScreenDestination()
    ),
    QuickLinksData(
        text = "Charges",
        icon = Icons.Default.Sell,
        direction = ChargesScreenDestination()
    ),
    QuickLinksData(
        text = "Address",
        icon = Icons.Default.Business,
        direction = AddressScreenDestination()
    ),
    QuickLinksData(
        text = "Customer",
        icon = Icons.Default.PeopleAlt,
        direction = CustomerScreenDestination()
    ),
    QuickLinksData(
        text = "Reminder",
        icon = Icons.Default.Notifications,
        direction = ReminderScreenDestination()
    ),
    QuickLinksData(
        text = "Reminder",
        icon = Icons.Default.Notifications,
        direction = ReminderScreenDestination()
    ),
    QuickLinksData(
        text = "Settings",
        icon = Icons.Default.Settings,
        direction = SettingsScreenDestination()
    )
)
     */


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickLinks(
    modifier : Modifier = Modifier,
    navController : NavController,
    linksData : List<QuickLinksData> = quickLinksData,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.Center,
        maxItemsInEachRow = 2,
    ) {
        linksData.forEach { quickLink ->
            QuickLink(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(SpaceMini),
                text = quickLink.text,
                icon = quickLink.icon,
                onClick = {
//                    navController.navigate(quickLink.direction)
                }
            )
        }
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