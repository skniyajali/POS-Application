package com.niyaj.popos.presentation.util

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.niyaj.popos.destinations.AddOnItemScreenDestination
import com.niyaj.popos.destinations.AddressScreenDestination
import com.niyaj.popos.destinations.CategoryScreenDestination
import com.niyaj.popos.destinations.ChargesScreenDestination
import com.niyaj.popos.destinations.CustomerScreenDestination
import com.niyaj.popos.destinations.EmployeeScreenDestination
import com.niyaj.popos.destinations.ExpensesCategoryScreenDestination
import com.niyaj.popos.destinations.ExpensesScreenDestination
import com.niyaj.popos.destinations.MainFeedScreenDestination
import com.niyaj.popos.destinations.OrderScreenDestination
import com.niyaj.popos.destinations.PartnerScreenDestination
import com.niyaj.popos.destinations.ProductScreenDestination
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.NavGraphs
import com.niyaj.popos.realm.address.presentation.AddressScreen
import com.niyaj.popos.realm.category.presentation.CategoryScreen
import com.niyaj.popos.realm.charges.presentation.ChargesScreen
import com.niyaj.popos.presentation.customer.CustomerScreen
import com.niyaj.popos.presentation.delivery_partner.PartnerScreen
import com.niyaj.popos.presentation.employee.EmployeeScreen
import com.niyaj.popos.presentation.expenses.ExpensesScreen
import com.niyaj.popos.presentation.expenses_category.ExpensesCategoryScreen
import com.niyaj.popos.presentation.main_feed.MainFeedScreen
import com.niyaj.popos.presentation.order.OrderScreen
import com.niyaj.popos.presentation.product.ProductScreen
import com.niyaj.popos.realm.addon_item.presentation.AddOnItemScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.scope.resultRecipient

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun NewNavigation(
    modifier: Modifier = Modifier,
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    bottomSheetNavigator: BottomSheetNavigator
) {
    navController.navigatorProvider += bottomSheetNavigator

    val navHostEngine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING, //default `rootDefaultAnimations` means no animations
        defaultAnimationsForNestedNavGraph = mapOf(
            NavGraphs.root to NestedNavGraphDefaultAnimations(
                enterTransition = { fadeIn(animationSpec = tween(2000)) },
                exitTransition = { fadeOut(animationSpec = tween(2000)) }
            )
        ) // all other nav graphs not specified in this map, will get their animations from the `rootDefaultAnimations` above.
    )

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(4.dp),
        modifier = modifier,
    ) {
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController,
            engine = navHostEngine,
            dependenciesContainerBuilder = {
                dependency(bottomSheetScaffoldState)
                dependency(scaffoldState)
            },
        ){
            composable(MainFeedScreenDestination){
                MainFeedScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            }

            composable(CategoryScreenDestination){
                CategoryScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient()
                )
            }

            composable(AddOnItemScreenDestination){
                AddOnItemScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(ChargesScreenDestination){
                ChargesScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient()
                )
            }

            composable(AddressScreenDestination){
                AddressScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(CustomerScreenDestination){
                CustomerScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(PartnerScreenDestination){
                PartnerScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(EmployeeScreenDestination){
                EmployeeScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient(),
                    addEditSalaryRecipient = resultRecipient(),
                    addEditAbsentRecipient = resultRecipient(),
                )
            }

            composable(ExpensesScreenDestination){
                ExpensesScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController,
                    resultRecipient = resultRecipient(),
                    settingRecipient = resultRecipient(),
                )
            }

            composable(ExpensesCategoryScreenDestination){
                ExpensesCategoryScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(ProductScreenDestination){
                ProductScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    resultRecipient = resultRecipient(),
                    navController = navController
                )
            }

            composable(OrderScreenDestination){
                OrderScreen(
                    onOpenSheet = onOpenSheet,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            }
        }
    }
}