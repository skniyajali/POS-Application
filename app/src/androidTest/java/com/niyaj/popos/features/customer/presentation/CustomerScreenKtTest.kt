package com.niyaj.popos.features.customer.presentation

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.niyaj.popos.features.common.di.RealmModule
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.PoposNavigation
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.ADD_EDIT_CUSTOMER_BUTTON
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_EMAIL_ERROR
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_EMAIL_FIELD
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_NAME_ERROR
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_NAME_FIELD
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_PHONE_ERROR
import com.niyaj.popos.features.customer.domain.util.CustomerTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.popos.features.destinations.CustomerScreenDestination
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
class CustomerScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavHostController

    private val newCustomer = Customer(
        customerName = "New Customer",
        customerPhone = "9078563421",
        customerEmail = "new@gmail.com",
    )

    private val updatedCustomer = Customer(
        customerName = "Updated Customer",
        customerPhone = "8078563421",
        customerEmail = "updated@gmail.com",
    )

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class
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
                    startRoute = CustomerScreenDestination,
                )
            }
        }
    }


    @Test
    fun a_check_validation_errors_and_create_update_customers () {
        //Check customer screen and add new customer button is visible
        composeRule.onNodeWithText("Customers").assertExists("Customers screen is not visible")
        composeRule.onNodeWithText("Add New Customer".uppercase()).assertExists("Add New Customer button is not visible")


        //Click on add new customer button check AddEdit Customer screen is visible or not
        composeRule.onNodeWithText("Add New Customer".uppercase()).performClick()
        composeRule.onNodeWithText("Add New Customer").assertExists("Add New Customer Screen is not visible")
        composeRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BUTTON).assertExists("AddEdit Customer button is not visible")
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).assertExists("Customer phone field is not visible")
        composeRule.onNodeWithTag(CUSTOMER_NAME_FIELD).assertExists("Customer name field is not visible")
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_FIELD).assertExists("Customer email field is not visible")


        //Check empty validation error on phone field
        composeRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BUTTON).performClick()
        composeRule.onNodeWithTag(CUSTOMER_PHONE_ERROR).assertExists()
        composeRule.onNodeWithTag(CUSTOMER_PHONE_ERROR).assertTextEquals("Phone no must not be empty")

        //check validation error with invalid data
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput("78676767")
        composeRule.onNodeWithTag(CUSTOMER_NAME_FIELD).performTextInput("c")
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextInput("dc")
        composeRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BUTTON).performClick()
        composeRule.onNodeWithTag(CUSTOMER_PHONE_ERROR).assertTextEquals("The phone no must be 10 digits")
        composeRule.onNodeWithTag(CUSTOMER_NAME_ERROR).assertTextEquals("Customer name must be 3 characters long")
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_ERROR).assertTextEquals("Customer email is not a valid email address.")

        //check validation error with invalid data
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput("df")
        composeRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BUTTON).performClick()
        composeRule.onNodeWithTag(CUSTOMER_PHONE_ERROR).assertTextEquals("The phone no should not contains any characters")
        composeRule.onNodeWithTag(CUSTOMER_NAME_ERROR).assertTextEquals("Customer name must be 3 characters long")
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_ERROR).assertTextEquals("Customer email is not a valid email address.")

        //Create new customer with valid data and check item is visible in the customer screen
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput(newCustomer.customerPhone)
        composeRule.onNodeWithTag(CUSTOMER_NAME_FIELD).performTextInput(newCustomer.customerName!!)
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextInput(newCustomer.customerEmail!!)
        composeRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BUTTON).performClick()

        //Check recently created customer is visible
        composeRule.onNodeWithText("Customers").assertExists("Customers screen is not visible")
        composeRule.onNodeWithTag(newCustomer.customerPhone).assertExists("New Customers is not visible")

        //Check Filter and Search icon is visible
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")
        composeRule.onNodeWithContentDescription("Filter Customer").assertExists("Filter icon is not visible")

        //Select item check all icons are visible
        composeRule.onNodeWithTag(newCustomer.customerPhone).performClick()
        composeRule.onNodeWithContentDescription("Select All Customers").assertExists("Select All Customers is not visible")
        composeRule.onNodeWithContentDescription("Delete Customer").assertExists("Delete Customer is not visible")
        composeRule.onNodeWithContentDescription("Edit Customer").assertExists("Edit Customer is not visible")
        composeRule.onNodeWithContentDescription("Close Icon").assertExists("Close Icon is not visible")

        //Click on edit icon to edit data
        composeRule.onNodeWithContentDescription("Edit Customer").performClick()
        composeRule.onNodeWithText("Update Customer Details").assertExists("Update Customer Details screen is not visible")
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput(updatedCustomer.customerPhone)
        composeRule.onNodeWithTag(CUSTOMER_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CUSTOMER_NAME_FIELD).performTextInput(updatedCustomer.customerName!!)
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextInput(updatedCustomer.customerEmail!!)
        composeRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BUTTON).performClick()

        //Check recently updated customer is visible
        composeRule.onNodeWithText("Customers").assertExists("Customers screen is not visible")
        composeRule.onNodeWithTag(updatedCustomer.customerPhone).assertExists("Updated Customers is not visible")


        //Create new customer with valid data and check item is visible in the customer screen
        composeRule.onNodeWithText("Add New Customer".uppercase()).performClick()
        composeRule.onNodeWithText("Add New Customer").assertExists("Add New Customer Screen is not visible")
        composeRule.onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput(newCustomer.customerPhone)
        composeRule.onNodeWithTag(CUSTOMER_NAME_FIELD).performTextInput(newCustomer.customerName!!)
        composeRule.onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextInput(newCustomer.customerEmail!!)
        composeRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BUTTON).performClick()

        //Check recently created customer is visible
        composeRule.onNodeWithText("Customers").assertExists("Customers screen is not visible")
        composeRule.onNodeWithTag(newCustomer.customerPhone).assertExists("New Customers is not visible")


        // Select one item and remaining select using select all icon
        composeRule.onNodeWithTag(newCustomer.customerPhone).performClick()
        composeRule.onNodeWithContentDescription("Select All Customers").performClick()
        composeRule.onNodeWithContentDescription("Edit Customer").assertDoesNotExist()
        composeRule.onNodeWithText("2 Selected").assertExists("2 Selected customers is not visible")
        composeRule.onNodeWithContentDescription("Select All Customers").performClick()
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")
        composeRule.onNodeWithContentDescription("Filter Customer").assertExists("Filter icon is not visible")

        // Select one item and remaining select using deselect all icon
        composeRule.onNodeWithTag(newCustomer.customerPhone).performClick()
        composeRule.onNodeWithContentDescription("Select All Customers").performClick()
        composeRule.onNodeWithContentDescription("Edit Customer").assertDoesNotExist()
        composeRule.onNodeWithText("2 Selected").assertExists("2 Selected customers is not visible")
        composeRule.onNodeWithContentDescription("Close Icon").performClick()
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")
        composeRule.onNodeWithContentDescription("Filter Customer").assertExists("Filter icon is not visible")
    }

    @Test
    fun b_perform_search_and_check_item_are_visible () {
        //Click on search icon and check that it is visible
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")
        composeRule.onNodeWithContentDescription("Search Icon").performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).assertExists("Search bar is not visible")
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).assertExists("Search bar back button is not visible")

        //Search new customer using 'new' text should only new customer will visible
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("new")
        composeRule.onNodeWithTag(newCustomer.customerPhone).assertExists("New customer should be visible")
        composeRule.onNodeWithTag(updatedCustomer.customerPhone).assertDoesNotExist()

        //clear search and search using 'update' text should only updated customer will visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertExists("Search bar clear button should be visible")
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("update")
        composeRule.onNodeWithTag(updatedCustomer.customerPhone).assertExists("Updated customer should be visible")
        composeRule.onNodeWithTag(newCustomer.customerPhone).assertDoesNotExist()

        //search using random text nothing will visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertExists("Search bar clear button should be visible")
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("xyz")
        composeRule.onNodeWithTag(newCustomer.customerPhone).assertDoesNotExist()
        composeRule.onNodeWithTag(updatedCustomer.customerPhone).assertDoesNotExist()
        composeRule.onNodeWithText("Searched Item Not Found").assertExists("Searched Item Not Found is not visible")

        //close search bar using the search bar back button
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
        composeRule.onNodeWithText("Customers").assertExists("Customer screen is not visible")
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")

    }

    @Test
    fun c_open_filter_screen_and_close () {
        composeRule.onNodeWithContentDescription("Filter Customer").assertExists("Filter icon is not visible")
        composeRule.onNodeWithContentDescription("Filter Customer").performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithText("Filter Customer").assertExists("Filter customer screen is not visible")
        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).assertExists("Filter customer screen close btn is not visible")

        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).assertExists("Filter customer screen close btn is not visible")

    }

    @Test
    fun d_perform_deletion() {
        //Select item and cancel deletion
        composeRule.onNodeWithTag(newCustomer.customerPhone).performClick()
        composeRule.onNodeWithContentDescription("Delete Customer").performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Customer is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete Customer is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).performClick()

        //Select all customers and delete them
        composeRule.onNodeWithTag(newCustomer.customerPhone).performClick()
        composeRule.onNodeWithContentDescription("Select All Customers").performClick()
        composeRule.onNodeWithContentDescription("Delete Customer").performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Customer is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete Customer is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        composeRule.onNodeWithTag(newCustomer.customerPhone).assertDoesNotExist()
        composeRule.onNodeWithTag(updatedCustomer.customerPhone).assertDoesNotExist()
        composeRule.onNodeWithText("Customers Not Found").assertExists("Customers Not Found is not visible")
    }
}