package com.niyaj.popos.features.common.util

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
import com.niyaj.popos.features.NavGraphs
import com.niyaj.popos.features.addon_item.presentation.AddOnItemScreen
import com.niyaj.popos.features.address.presentation.AddressScreen
import com.niyaj.popos.features.category.presentation.CategoryScreen
import com.niyaj.popos.features.charges.presentation.ChargesScreen
import com.niyaj.popos.features.customer.presentation.CustomerScreen
import com.niyaj.popos.features.delivery_partner.presentation.PartnerScreen
import com.niyaj.popos.features.destinations.AddOnItemScreenDestination
import com.niyaj.popos.features.destinations.AddressScreenDestination
import com.niyaj.popos.features.destinations.CategoryScreenDestination
import com.niyaj.popos.features.destinations.ChargesScreenDestination
import com.niyaj.popos.features.destinations.CustomerScreenDestination
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.destinations.ExpensesCategoryScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.PartnerScreenDestination
import com.niyaj.popos.features.destinations.ProductScreenDestination
import com.niyaj.popos.features.employee.presentation.EmployeeScreen
import com.niyaj.popos.features.expenses.presentation.ExpensesScreen
import com.niyaj.popos.features.expenses_category.presentation.ExpensesCategoryScreen
import com.niyaj.popos.features.main_feed.presentation.MainFeedScreen
import com.niyaj.popos.features.order.presentation.OrderScreen
import com.niyaj.popos.features.product.presentation.ProductScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.scope.resultRecipient

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun Navigation(
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