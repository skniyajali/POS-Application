package com.niyaj.popos.features.charges.presentation

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.niyaj.popos.features.MainActivity
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.ADD_EDIT_CHARGES_BUTTON
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_AMOUNT_ERROR
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_AMOUNT_FIELD
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_APPLIED_SWITCH
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_NAME_ERROR
import com.niyaj.popos.features.charges.domain.util.ChargesTestTags.CHARGES_NAME_FIELD
import com.niyaj.popos.features.common.di.RealmModule
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.Navigation
import com.niyaj.popos.features.destinations.ChargesScreenDestination
import com.niyaj.popos.utils.Constants.NEGATIVE_BUTTON
import com.niyaj.popos.utils.Constants.POSITIVE_BUTTON
import com.niyaj.popos.utils.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.popos.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.popos.utils.Constants.STANDARD_BOTTOM_SHEET_CLOSE_BTN
import com.niyaj.popos.utils.Constants.STANDARD_SEARCH_BAR
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
class ChargesScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavHostController

    private val newCharges = Charges(
        chargesName = "New Charges",
        chargesPrice = 15,
    )

    private val updatedCharges = Charges(
        chargesName = "Updated Charges",
        chargesPrice = 20,
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
                navController = rememberAnimatedNavController()
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                navController.navigatorProvider += bottomSheetNavigator

                Navigation(
                    scaffoldState = scaffoldState,
                    navController = navController,
                    bottomSheetNavigator = bottomSheetNavigator,
                    startRoute = ChargesScreenDestination,
                )
            }
        }
    }


    @Test
    fun a_start_to_end_charges_screen_test() {

        //Check Charges Screen and create new charges button is visible or not
        composeRule.onNodeWithText("Charges Item").assertExists("Charges Screen is not visible")
        composeRule.onNodeWithText("Create New Charges".uppercase()).assertExists("Create New Charges button is not visible")

        composeRule.waitForIdle()

        //click on create new charges button and check Create New Charges Screen is visible or not
        composeRule.onNodeWithText("Create New Charges".uppercase()).performClick()
        composeRule.onNodeWithText("Create New Charges").assertExists("Create New Charges Screen is not visible")
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).assertExists("Charges name field is not visible")
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).assertExists("Charges amount field is not visible")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).assertExists("AddEdit Charges button is not visible")

        composeRule.waitForIdle()

        //mark as charges applied
        composeRule.onNodeWithText("Marked as not applied").assertExists("Marked as not applied not visible")
        composeRule.onNodeWithTag(CHARGES_APPLIED_SWITCH).performClick()
        composeRule.onNodeWithText("Marked as applied").assertExists("Marked as applied not visible")

        composeRule.waitForIdle()

        //Check validation error with empty data and charges applied
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()
        composeRule.onNodeWithTag(CHARGES_NAME_ERROR).assertExists("Charges name error is not visible")
        composeRule.onNodeWithTag(CHARGES_AMOUNT_ERROR).assertExists("Charges Amount error is not visible")
        composeRule.onNodeWithTag(CHARGES_NAME_ERROR).assertTextEquals("Charges Name must not be empty")
        composeRule.onNodeWithTag(CHARGES_AMOUNT_ERROR).assertTextEquals("Charges price required.")

        composeRule.waitForIdle()

        //Check validation error with invalid string data
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput("new")
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput("6")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()
        composeRule.onNodeWithTag(CHARGES_NAME_ERROR).assertExists("Charges Name must be more than 5 characters long")
        composeRule.onNodeWithTag(CHARGES_AMOUNT_ERROR).assertExists("Charges Price must be greater than 10 rupees.")

        composeRule.waitForIdle()

        //Check validation error with invalid string and digit data
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput("testing5")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()
        composeRule.onNodeWithTag(CHARGES_NAME_ERROR).assertExists("Charges Name must not contain a digit")
        composeRule.onNodeWithTag(CHARGES_AMOUNT_ERROR).assertExists("Charges Price must be greater than 10 rupees.")

        composeRule.waitForIdle()

        //Create new charges with valid data and check it's visible in screen
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(newCharges.chargesName)
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput("${newCharges.chargesPrice}")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()

        composeRule.waitForIdle()

        // Check after successful creation it navigate back to charges screen and created item should visible
        composeRule.onNodeWithText("Charges Item").assertExists("Charges Screen is not visible")
        composeRule.onNodeWithTag(newCharges.chargesName).assertExists("Charges Item is not visible")

        composeRule.waitForIdle()

        //Check search and filter icons are visible
        composeRule.onNodeWithContentDescription("Filter Charges Item").assertExists("Filter icon is not visible")
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")

        composeRule.waitForIdle()

        //Select charges item and check all icons are visible
        composeRule.onNodeWithTag(newCharges.chargesName).performClick()
        composeRule.onNodeWithContentDescription("Filter Charges Item").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Search Icon").assertDoesNotExist()
        composeRule.onNodeWithText("Create New Charges".uppercase()).assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Delete Charges").assertExists("Delete Charges icon is not visible")
        composeRule.onNodeWithContentDescription("Edit Charges Item").assertExists("Edit Charges icon is not visible")
        composeRule.onNodeWithContentDescription("Close Icon").assertExists("Close icon is not visible")

        composeRule.waitForIdle()

        //Select again to deselect item
        composeRule.onNodeWithTag(newCharges.chargesName).performClick()
        composeRule.onNodeWithContentDescription("Filter Charges Item").assertExists("Filter icon is not visible")
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")

        composeRule.waitForIdle()

        //Select again to deselect using close icon
        composeRule.onNodeWithTag(newCharges.chargesName).performClick()
        composeRule.onNodeWithContentDescription("Filter Charges Item").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Search Icon").assertDoesNotExist()
        composeRule.onNodeWithText("Create New Charges".uppercase()).assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Close Icon").performClick()
        composeRule.onNodeWithContentDescription("Filter Charges Item").assertExists("Filter icon is not visible")
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")

        composeRule.waitForIdle()

        //Select item and update data
        composeRule.onNodeWithTag(newCharges.chargesName).performClick()
        composeRule.onNodeWithContentDescription("Edit Charges Item").performClick()
        composeRule.onNodeWithText("Update Charges").assertExists("Update charges screen is not visible")
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(updatedCharges.chargesName)
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput("${updatedCharges.chargesPrice}")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()

        composeRule.waitForIdle()

        // Check after successful update it navigate back to charges screen and created item should visible
        composeRule.onNodeWithText("Charges Item").assertExists("Charges Screen is not visible")
        composeRule.onNodeWithTag(updatedCharges.chargesName).assertExists("Charges Item is not visible")

        composeRule.waitForIdle()

        //Create new charges
        composeRule.onNodeWithText("Create New Charges".uppercase()).performClick()
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(newCharges.chargesName)
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput("${newCharges.chargesPrice}")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()

        composeRule.waitForIdle()

        // Check after successful create it navigate back to charges screen and created item should visible
        composeRule.onNodeWithText("Charges Item").assertExists("Charges Screen is not visible")
        composeRule.onNodeWithTag(newCharges.chargesName).assertExists("Charges Item is not visible")

        composeRule.waitForIdle()

        //Click on filter icon and filter charges screen must be visible and close using close button
        composeRule.onNodeWithContentDescription("Filter Charges Item").performClick()
        Thread.sleep(500)
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Filter Charges Item").assertExists("Filter Charges Screen is not visible")
        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).assertExists("Filter charges screen close button is not visible")
        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).performClick()

        composeRule.waitForIdle()

        //Select item and click delete button and check dialog is shown or not and cancel deletion item should be remain same
        composeRule.onNodeWithTag(updatedCharges.chargesName).performClick()
        composeRule.onNodeWithContentDescription("Delete Charges").performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Charges dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete Charges dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).performClick()

        composeRule.waitForIdle()

        //Select again perform deletion and item should be removed from the screen
        composeRule.onNodeWithTag(updatedCharges.chargesName).performClick()
        composeRule.onNodeWithContentDescription("Delete Charges").performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Charges dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete Charges dialog is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()
        composeRule.onNodeWithTag(updatedCharges.chargesName).assertDoesNotExist()

        composeRule.waitForIdle()

        //Create new charges
        composeRule.onNodeWithText("Create New Charges".uppercase()).performClick()
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(updatedCharges.chargesName)
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput("${updatedCharges.chargesPrice}")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()

        composeRule.waitForIdle()

        // Check after successful create it navigate back to charges screen and created item should visible
        composeRule.onNodeWithText("Charges Item").assertExists("Charges Screen is not visible")
        composeRule.onNodeWithTag(updatedCharges.chargesName).assertExists("Charges Item is not visible")

    }

    @Test
    fun b_perform_search_test() {
        //Perform search and check item is visible
        composeRule.onNodeWithContentDescription("Search Icon").performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).assertExists("Search bar is not visible")
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).assertExists("Search bar back button is not visible")

        //Search using text 'new' and check item visible or not
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("new")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(newCharges.chargesName).assertExists("New Charges Item is not visible")
        composeRule.onNodeWithTag(updatedCharges.chargesName).assertDoesNotExist()

        Thread.sleep(500)

        //Search using text 'updated' and check item visible or not
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextClearance()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("updated")
        composeRule.onNodeWithTag(updatedCharges.chargesName).assertExists("Updated Charges Item is not visible")
        composeRule.onNodeWithTag(newCharges.chargesName).assertDoesNotExist()

        //search using random text will show searched item not found
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performTextClearance()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("testing")
        composeRule.onNodeWithTag(updatedCharges.chargesName).assertDoesNotExist()
        composeRule.onNodeWithTag(newCharges.chargesName).assertDoesNotExist()
        composeRule.onNodeWithText("Searched Item Not Found").assertExists("Search item not found is not visible")

        //press back button to clear search and all items should be visible
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performTextClearance()
        composeRule.onNodeWithTag(newCharges.chargesName).assertExists("New Charges Item is not visible")
        composeRule.onNodeWithTag(updatedCharges.chargesName).assertExists("Updated Charges Item is not visible")

    }

    @Test
    fun c_create_charges_that_already_exist_test () {
        composeRule.waitForIdle()

//      Create new charges with same result validation error message
        composeRule.onNodeWithText("Create New Charges".uppercase()).performClick()
        composeRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(newCharges.chargesName)
        composeRule.onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput("${newCharges.chargesPrice}")
        composeRule.onNodeWithTag(ADD_EDIT_CHARGES_BUTTON).performClick()
        composeRule.onNodeWithTag(CHARGES_NAME_ERROR).assertExists("Charges Name already exists.")

    }
}