package com.niyaj.popos.features.delivery_partner.presentation

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.niyaj.popos.features.MainActivity
import com.niyaj.popos.features.RealmModule
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.Navigation
import com.niyaj.popos.features.components.util.SheetLayout
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.destinations.PartnerScreenDestination
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@HiltAndroidTest
@UninstallModules(RealmModule::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class PartnerScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var currentBottomSheet = mutableStateOf<BottomSheetScreen?>(null)

    private lateinit var navController: NavHostController

    private val newPartner = DeliveryPartner(
        partnerName = "New Partner",
        partnerPhone = "9078563421",
        partnerEmail = "test@example.com",
        partnerPassword = "Password@2k22",
    )

    private val updatedPartner = DeliveryPartner(
        partnerName = "Updated Partner",
        partnerPhone = "7078563421",
        partnerEmail = "updated@example.com",
        partnerPassword = "Updated@2k22",
    )

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class,
        ExperimentalMaterialApi::class
    )
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            PoposTheme {
                val scaffoldState = rememberScaffoldState()
                val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
                val scope = rememberCoroutineScope()
                navController = rememberAnimatedNavController()
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                navController.navigatorProvider += bottomSheetNavigator

                // to set the current sheet to null when the bottom sheet closes
                if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                    currentBottomSheet.value = null
                }

                val closeSheet: () -> Unit = {
                    scope.launch {
                        if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                            bottomSheetScaffoldState.bottomSheetState.collapse()
                        }

                        // to set the current sheet to null when the bottom sheet closes
                        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                            currentBottomSheet.value = null
                        }
                    }
                }

                val openSheet: (BottomSheetScreen) -> Unit = {
                    scope.launch {
                        currentBottomSheet.value = it
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }
                }

                BottomSheetScaffold(
                    sheetContent = {
                        currentBottomSheet.value?.let { currentSheet ->
                            SheetLayout(
                                currentScreen = currentSheet,
                                onCloseBottomSheet = closeSheet,
                                navController = navController,
                            )
                        }
                    },
                    sheetPeekHeight = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                    scaffoldState = bottomSheetScaffoldState,
                    sheetGesturesEnabled = true,
                    sheetElevation = 8.dp,
                    sheetShape = MaterialTheme.shapes.medium,
                ) {
                    Navigation(
                        onOpenSheet = openSheet,
                        scaffoldState = scaffoldState,
                        bottomSheetScaffoldState = bottomSheetScaffoldState,
                        navController = navController,
                        bottomSheetNavigator = bottomSheetNavigator,
                        startRoute = PartnerScreenDestination,
                    )
                }

            }
        }
    }

}