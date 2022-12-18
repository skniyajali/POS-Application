package com.niyaj.popos.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.presentation.components.ExtendedFabButton
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.destinations.CartScreenDestination
import com.niyaj.popos.presentation.destinations.EmployeeScreenDestination
import com.niyaj.popos.presentation.destinations.ExpensesScreenDestination
import com.niyaj.popos.presentation.destinations.OrderScreenDestination
import com.niyaj.popos.presentation.destinations.ProductScreenDestination
import com.niyaj.popos.presentation.destinations.ReportScreenDestination
import com.niyaj.popos.presentation.main_feed.components.IconBox
import com.niyaj.popos.presentation.ui.theme.ProfilePictureSizeExtraLarge
import com.niyaj.popos.presentation.ui.theme.ProfilePictureSizeLarge
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProfileScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
) {

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        title = {
            Text(text = stringResource(id = R.string.popos_highlight))
        },
        navActions = {},
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            ExtendedFabButton(
                text = "",
                showScrollToTop = showScrollToTop.value,
                visible = false,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {},
            )
        },
        floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = 4.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "logo",
                            modifier = Modifier
                                .size(
                                    width = ProfilePictureSizeExtraLarge,
                                    height = ProfilePictureSizeLarge
                                )
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "Chinna Seeragapadi, Salem",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "TamilNadu, India - 636308",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Divider(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Text(
                            text = "95008 25077 / 9591 85001",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(SpaceMedium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconBox(
                        modifier = Modifier.width(148.dp),
                        iconName = Icons.Default.ShoppingCart,
                        text = "My Carts",
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        iconColor = MaterialTheme.colors.secondaryVariant,
                        textColor = MaterialTheme.colors.onBackground,
                        onClick = { navController.navigate(CartScreenDestination) }
                    )
                    Spacer(modifier = Modifier.width(SpaceMedium))
                    IconBox(
                        modifier = Modifier.width(148.dp),
                        iconName = Icons.Default.Inventory,
                        text = "My Orders",
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        iconColor = MaterialTheme.colors.secondaryVariant,
                        textColor = MaterialTheme.colors.onBackground,
                        onClick = { navController.navigate(OrderScreenDestination()) }
                    )
                }
                Spacer(modifier = Modifier.height(SpaceMedium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconBox(
                        modifier = Modifier.width(148.dp),
                        iconName = Icons.Default.Assessment,
                        text = "Reports",
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        iconColor = MaterialTheme.colors.secondaryVariant,
                        textColor = MaterialTheme.colors.onBackground,
                        onClick = { navController.navigate(ReportScreenDestination()) }
                    )
                    Spacer(modifier = Modifier.width(SpaceMedium))
                    IconBox(
                        modifier = Modifier.width(148.dp),
                        iconName = Icons.Default.People,
                        text = "Employee",
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        iconColor = MaterialTheme.colors.secondaryVariant,
                        textColor = MaterialTheme.colors.onBackground,
                        onClick = { navController.navigate(EmployeeScreenDestination) }
                    )
                }

                Spacer(modifier = Modifier.height(SpaceMedium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconBox(
                        modifier = Modifier.width(148.dp),
                        iconName = Icons.Default.Money,
                        text = "Expenses",
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        iconColor = MaterialTheme.colors.secondaryVariant,
                        textColor = MaterialTheme.colors.onBackground,
                        onClick = { navController.navigate(ExpensesScreenDestination()) }
                    )
                    Spacer(modifier = Modifier.width(SpaceMedium))
                    IconBox(
                        modifier = Modifier.width(148.dp),
                        iconName = Icons.Default.Dns,
                        text = "Products",
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        iconColor = MaterialTheme.colors.secondaryVariant,
                        textColor = MaterialTheme.colors.onBackground,
                        onClick = { navController.navigate(ProductScreenDestination) }
                    )
                }
                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }
}