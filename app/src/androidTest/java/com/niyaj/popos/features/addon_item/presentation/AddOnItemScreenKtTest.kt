package com.niyaj.popos.features.addon_item.presentation

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.navigation.plusAssign
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.niyaj.popos.features.MainActivity
import com.niyaj.popos.features.NavGraphs
import com.niyaj.popos.features.RealmModule
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_ADD_EDIT_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_DIGIT_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_EMPTY_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_ERROR_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_FIELD
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_LENGTH_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_ERROR_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_FIELD
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.CREATE_NEW_ADD_ON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.NO_ITEMS_IN_ADDON
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.destinations.AddOnItemScreenDestination
import com.niyaj.popos.util.Constants
import com.niyaj.popos.util.Constants.ADDON_NOT_AVAIlABLE
import com.niyaj.popos.util.Constants.CREATE_NEW_ADDON_BTN
import com.niyaj.popos.util.Constants.CREATE_NEW_ADDON_SCREEN
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@HiltAndroidTest
@UninstallModules(RealmModule::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class AddOnItemScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

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
                val engine = rememberAnimatedNavHostEngine()
                val navController = rememberAnimatedNavController()
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                navController.navigatorProvider += bottomSheetNavigator

                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetShape = RoundedCornerShape(4.dp),
                ) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        engine = engine,
                        navController = navController,
                        startRoute = AddOnItemScreenDestination,
                        dependenciesContainerBuilder = {
                            dependency(bottomSheetScaffoldState)
                            dependency(scaffoldState)
                            dependency(bottomSheetNavigator)
                        },
                    )
                }

            }
        }
    }

    @Test
    fun checkAddOnItemScreenVisible() {
        composeRule.onNodeWithTag(Constants.ADDON_SCREEN).assertExists("Unable to find AddOn Screen")
    }

    @Test
    fun checkAddOnItemScreenEmptyWithMessageAndButton() {
        composeRule.onNodeWithTag(ADDON_NOT_AVAIlABLE).assertExists("Empty AddOn Item Screen Box Not Visible")

        composeRule.onNodeWithText(NO_ITEMS_IN_ADDON).assertExists("Empty AddOn Items Message Not Visible")

        composeRule.onNodeWithTag(CREATE_NEW_ADDON_BTN).assertExists("Create New AddOn Item Button Not Visible")
    }

    @Test
    fun navigateToCreateNewAddOnScreen() {
        composeRule.onNodeWithTag(CREATE_NEW_ADDON_BTN).performClick()

        composeRule.onNodeWithTag(CREATE_NEW_ADDON_SCREEN).assertExists("Create New AddOn Item Screen Not Visible")

        composeRule.onNodeWithText(CREATE_NEW_ADD_ON).assertExists("Title Is Not Visible")

        composeRule.onNodeWithTag(ADDON_NAME_FIELD).assertExists("Addon Name Field Not Visible")

        composeRule.onNodeWithText(ADDON_PRICE_FIELD).assertExists("Addon Price Field Not Visible")

        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).assertExists("Addon AddEdit Button Not Visible")
    }

    @Test
    fun performInputAndCheckValidationError() {
        composeRule.onNodeWithTag(CREATE_NEW_ADDON_BTN).performClick()

        //Click CreateNewAddonButton without any input return error
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertExists("Addon Name Error Not Visible")
        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertTextEquals(ADDON_NAME_EMPTY_ERROR)
        composeRule.onNodeWithTag(ADDON_PRICE_ERROR_TAG).assertTextEquals(ADDON_PRICE_EMPTY_ERROR)

        //perform text input in addon name field only string with less than 5 characters return error
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput("test")
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertTextEquals(ADDON_NAME_LENGTH_ERROR)

        //perform text input in addon name field with string and digit return error
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput("test2")
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertTextEquals(ADDON_NAME_DIGIT_ERROR)

        //perform text input in addon name field with whitelist data string and digit return error
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput("Cold2")
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertDoesNotExist()

        //perform text input in addon name field with correct data return success
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput("testing")
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertDoesNotExist()

        //Clear AddOn Name field
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextClearance()

        //Click CreateNewAddonButton without any input return error
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_PRICE_ERROR_TAG).assertExists("Addon Price Error Not Visible")
        composeRule.onNodeWithTag(ADDON_PRICE_ERROR_TAG).assertTextEquals(ADDON_PRICE_EMPTY_ERROR)

        //perform text input in addon price field with string return error
        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextInput("s")
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_PRICE_ERROR_TAG).assertTextEquals(ADDON_PRICE_EMPTY_ERROR)

        //perform text input in addon price field with digit less than 5 return error
        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextInput("4")
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_PRICE_ERROR_TAG).assertTextEquals(ADDON_PRICE_LESS_THAN_FIVE_ERROR)

        //perform text input in addon price field with correct data return true
        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextInput("6")
        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDON_PRICE_ERROR_TAG).assertDoesNotExist()
    }
}