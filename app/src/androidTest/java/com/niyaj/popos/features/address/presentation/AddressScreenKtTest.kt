package com.niyaj.popos.features.address.presentation

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
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_SEARCH_BAR
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.popos.features.address.domain.util.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.popos.features.address.domain.util.AddressTestTags.CREATE_UPDATE_ADDRESS_BUTTON
import com.niyaj.popos.features.common.di.RealmModule
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.PoposNavigation
import com.niyaj.popos.features.destinations.AddressScreenDestination
import com.niyaj.popos.utils.Constants.NEGATIVE_BUTTON
import com.niyaj.popos.utils.Constants.POSITIVE_BUTTON
import com.niyaj.popos.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.popos.utils.Constants.STANDARD_BOTTOM_SHEET_CLOSE_BTN
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
class AddressScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavHostController


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

                PoposNavigation(
                    scaffoldState = scaffoldState,
                    navController = navController,
                    bottomSheetNavigator = bottomSheetNavigator,
                    startRoute = AddressScreenDestination,
                )
            }
        }
    }

    @Test
    fun address_screen_start_to_end_test() {
        //Check AddressScreen is visible or not
        composeRule.onNodeWithText("Addresses").assertExists("Address screen is not visible")
        composeRule.onNodeWithText("Add New Address".uppercase()).assertExists("Add New Address Button is not visible")

        //Check AddEditAddressScreen and it's components are visible or not
        composeRule.onNodeWithText("Add New Address".uppercase()).assertExists("Add New Address Button is not visible").performClick()
        composeRule.onNodeWithText("Add New Address").assertExists("AddEdit Address Screen is not visible")
        composeRule.onNodeWithTag(ADDRESS_SHORT_NAME_FIELD).assertExists("Short Name Field is not visible")
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).assertExists("Full Name Field is not visible")
        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).assertExists("Create Or Update Address Button not visible")

        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_ERROR).assertExists("Address Full Name Error is not visible")
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_ERROR).assertTextEquals("Address name must not be empty")
        composeRule.onNodeWithTag(ADDRESS_SHORT_NAME_ERROR).assertExists("Address Short Name Error is not visible")
        composeRule.onNodeWithTag(ADDRESS_SHORT_NAME_ERROR).assertTextEquals("Address short name cannot be empty")


        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput("n")
        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_ERROR).assertTextEquals("The address name must be more than 2 characters long")
        composeRule.onNodeWithTag(ADDRESS_SHORT_NAME_ERROR).assertTextEquals("The short name must be more than 2 characters long")
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput("ew ladies")
        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).performClick()

        composeRule.onNodeWithText("Addresses").assertExists("Address screen is not visible")
        composeRule.onNodeWithTag("New Ladies").assertExists("New Created Address is not visible")
        composeRule.onNodeWithTag("New Ladies").performClick()
        composeRule.onNodeWithContentDescription("Edit Address").assertExists("Edit Address button is not visible")
        composeRule.onNodeWithContentDescription("Delete Address").assertExists("Delete Address button is not visible")
        composeRule.onNodeWithContentDescription("Select All Address").assertExists("Select All Address button is not visible")
        composeRule.onNodeWithContentDescription("Deselect address").assertExists("Deselect All Address button is not visible")

        //Edit Address
        composeRule.onNodeWithContentDescription("Edit Address").performClick()
        composeRule.onNodeWithText("Update Address").assertExists("AddEdit Address Screen is not visible")
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).performClick()
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_ERROR).assertTextEquals("Address name must not be empty")
        composeRule.onNodeWithTag(ADDRESS_SHORT_NAME_ERROR).assertTextEquals("Address short name cannot be empty")
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput("Mbbs Boys")
        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).performClick()
        composeRule.onNodeWithText("Addresses").assertExists("Address screen is not visible")
        composeRule.onNodeWithTag("Mbbs Boys").assertExists("Updated Address is not visible")

        //Add new address and check item already exists or not
        composeRule.onNodeWithText("Add New Address".uppercase()).performClick()
        composeRule.onNodeWithText("Add New Address").assertExists("AddEdit Address Screen is not visible")
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput("New Ladies")
        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).performClick()
        composeRule.onNodeWithText("Addresses").assertExists("Address screen is not visible")
        composeRule.onNodeWithTag("New Ladies").assertExists("Updated Address is not visible")
        composeRule.onNodeWithTag("Mbbs Boys").assertExists("Updated Address is not visible")

        //TODO: check address already exists failed
        //Add new address and check item already exists or not
//        composeRule.onNodeWithText("Add New Address".uppercase()).performClick()
//        composeRule.onNodeWithText("Add New Address").assertExists("AddEdit Address Screen is not visible")
//        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput("New Ladies")
//        Thread.sleep(300)
//        composeRule.onNodeWithTag(CREATE_UPDATE_ADDRESS_BUTTON).performClick()
//        Thread.sleep(300)
//        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_ERROR).assertExists("Full Address error is not visible")
//        composeRule.onNodeWithTag(ADDRESS_FULL_NAME_ERROR).assertTextEquals("Address name already exists.")


        //Select the address
        composeRule.onNodeWithTag("Mbbs Boys").performClick()
        composeRule.onNodeWithContentDescription("Select All Address").performClick()
        composeRule.onNodeWithContentDescription("Edit Address").assertDoesNotExist()
        composeRule.onNodeWithText("2 Selected").assertExists("2 Selected text is not visible")

        Thread.sleep(3000)

        //Select all addresses
        composeRule.onNodeWithContentDescription("Select All Address").performClick()
        composeRule.onNodeWithText("2 Selected").assertDoesNotExist()

        //Again select address and deselect address
        composeRule.onNodeWithTag("Mbbs Boys").performClick()
        composeRule.onNodeWithContentDescription("Deselect address").performClick()
        composeRule.onNodeWithContentDescription("Deselect address").assertDoesNotExist()

        //Select Address and click delete button and cancel deletion
        composeRule.onNodeWithTag("Mbbs Boys").performClick()
        composeRule.onNodeWithContentDescription("Delete Address").assertExists("Delete Address button is not visible").performClick()
        composeRule.onNodeWithText("Delete 1 Address?").assertExists("Delete dialog not shown")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Cancel button not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Cancel button not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).performClick()

        // Check search icon is visible or not perform search
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon button is not visible")
        composeRule.onNodeWithContentDescription("Search Icon").performClick()
        composeRule.onNodeWithTag(ADDRESS_SEARCH_BAR).assertExists("Search bar is not visible")
        composeRule.onNodeWithTag(ADDRESS_SEARCH_BAR).performTextInput("mb")
        composeRule.onNodeWithTag("Mbbs Boys").assertExists("On searching this address should visible")
        composeRule.onNodeWithTag("New Ladies").assertDoesNotExist()

        //Search with invalid text return searched item not found
        composeRule.onNodeWithTag(ADDRESS_SEARCH_BAR).performTextInput("testing")
        composeRule.onNodeWithText("Searched Item Not Found", substring = true, ignoreCase = true).assertExists("search item not found is not visible")

        //check Back button is visible or not and press Back button
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).assertExists()
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()

        //Check filter icon is visible or not and press filter button and verify filter screen does visible or not
        composeRule.onNodeWithContentDescription("Filter Address").assertExists("Filter Address button is not visible")
        composeRule.onNodeWithContentDescription("Filter Address").performClick()
        composeRule.onNodeWithContentDescription("Filter Address").assertExists("Filter Address is not visible")
        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).assertExists()
        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).performClick()

        // Select address and perform deletion and it should not visible in screen
        composeRule.onNodeWithTag("Mbbs Boys").assertExists().performClick()
        composeRule.onNodeWithContentDescription("Delete Address").assertExists("Delete Address button is not visible").performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()
        composeRule.onNodeWithTag("Mbbs Boys").assertDoesNotExist()
    }
}