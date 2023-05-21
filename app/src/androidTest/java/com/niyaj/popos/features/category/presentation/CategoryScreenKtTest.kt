package com.niyaj.popos.features.category.presentation

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
import com.niyaj.popos.features.category.domain.util.CategoryTestTags.ADD_EDIT_CATEGORY_BTN
import com.niyaj.popos.features.category.domain.util.CategoryTestTags.CATEGORY_NAME_ERROR
import com.niyaj.popos.features.category.domain.util.CategoryTestTags.CATEGORY_NAME_FIELD
import com.niyaj.popos.features.common.di.RealmModule
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.Navigation
import com.niyaj.popos.features.destinations.CategoryScreenDestination
import com.niyaj.popos.utils.Constants.NEGATIVE_BUTTON
import com.niyaj.popos.utils.Constants.POSITIVE_BUTTON
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
class CategoryScreenKtTest {

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

                Navigation(
                    scaffoldState = scaffoldState,
                    navController = navController,
                    bottomSheetNavigator = bottomSheetNavigator,
                    startRoute = CategoryScreenDestination,
                )
            }
        }
    }

    @Test
    fun start_to_end_category_screen_test() {

        //Check Category screen and Create button is visible or not
        composeRule.onNodeWithText("Categories").assertExists("Category screen is not visible")
        composeRule.onNodeWithText("Create New Category".uppercase()).assertExists("Create New Category button is not visible")

        //Click on create new category button and navigate to create category screen
        composeRule.onNodeWithText("Create New Category".uppercase()).performClick()
        composeRule.onNodeWithText("Create New Category").assertExists("Create New Category Screen is not visible")
        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).assertExists("Category Name field is not visible")
        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).assertExists("Add Edit Category button is not visible")

        //Click on AddEdit category button and check validation error with empty data
        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
        composeRule.onNodeWithTag(CATEGORY_NAME_ERROR).assertExists("Category name error is not visible")
        composeRule.onNodeWithTag(CATEGORY_NAME_ERROR).assertTextEquals("Category name must not be empty")

        //Check validation error with invalid data
        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput("Ne")
        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
        composeRule.onNodeWithTag(CATEGORY_NAME_ERROR).assertTextEquals("Category name must be 3 characters long")

        //Create new category with valid data should navigate back and item should be visible
        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput("w Category")
        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
        composeRule.onNodeWithText("Categories").assertExists("Category screen is not visible")
        composeRule.onNodeWithTag("New Category").assertExists("Newly created category is not visible")

        //Create new item should return validation error
        composeRule.onNodeWithText("Create New Category".uppercase()).performClick()
        composeRule.onNodeWithText("Create New Category").assertExists("Create New Category Screen is not visible")
//        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput("New Category")
//        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
//        composeRule.onNodeWithTag(CATEGORY_NAME_ERROR).assertTextEquals("Category name already exists.")

        //Create Another Category check its visible or not in address screen
//        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput("Another Category")
        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
        composeRule.onNodeWithText("Categories").assertExists("Category screen is not visible")
        composeRule.onNodeWithTag("Another Category").assertExists("another created category is not visible")

        //Check search and filter icons are visible
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")
        composeRule.onNodeWithContentDescription("Filter Category").assertExists("Filter icon is not visible")

        //Select new category and check edit,delete,select-all and deselect icon are visible
        composeRule.onNodeWithTag("New Category").performClick()
        composeRule.onNodeWithContentDescription("Edit Category").assertExists("Edit category icon is not visible")
        composeRule.onNodeWithContentDescription("Delete Category").assertExists("Delete category icon is not visible")
        composeRule.onNodeWithContentDescription("Select All Category").assertExists("Select All Category is not visible")
        composeRule.onNodeWithContentDescription("Close Icon").assertExists("Close icon is not visible")

        //Click on edit icon and edit category that already exists return validation error
        composeRule.onNodeWithContentDescription("Edit Category").performClick()
        composeRule.onNodeWithText("Edit Category").assertExists("Edit category screen is not visible")
//        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextClearance()
//        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput("Another Category")
//        composeRule.waitForIdle()
//        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
//        composeRule.waitForIdle()
//        composeRule.onNodeWithTag(CATEGORY_NAME_ERROR).assertTextEquals("Category name already exists.")

        //Edit Category with valid name should navigate back and item should be visible
        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput("Updated Category")
        composeRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
        composeRule.onNodeWithText("Categories").assertExists("Category screen is not visible")
        composeRule.onNodeWithTag("Updated Category").assertExists("updated category is not visible")

        //first select one item and then click select all item to select all items
        composeRule.onNodeWithTag("Updated Category").performClick()
        composeRule.onNodeWithContentDescription("Select All Category").performClick()
        composeRule.onNodeWithText("2 Selected").assertExists("Selected Category not visible")
        composeRule.onNodeWithContentDescription("Edit Category").assertDoesNotExist()

        // click again select all to deselect all items
        composeRule.onNodeWithContentDescription("Select All Category").performClick()
        composeRule.onNodeWithText("Categories").assertExists("Categories screen is not visible")
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")
        composeRule.onNodeWithContentDescription("Filter Category").assertExists("Filter icon is not visible")

        //select again and deselect all using deselect icon
        composeRule.onNodeWithTag("Updated Category").performClick()
        composeRule.onNodeWithContentDescription("Close Icon").performClick()
        composeRule.onNodeWithContentDescription("Close Icon").assertDoesNotExist()

        //select again and click delete icon to show delete dialog
        composeRule.onNodeWithTag("Updated Category").performClick()
        composeRule.onNodeWithContentDescription("Delete Category").performClick()
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete Category dialog not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Category dialog not visible")

        //Perform cancel deletion and check delete and other icons are not visible
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).performClick()
        composeRule.onNodeWithContentDescription("Close Icon").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Delete Category").assertDoesNotExist()

        //click on search button and perform search
        composeRule.onNodeWithContentDescription("Search Icon").performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).assertExists("Search bar is not visible")
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("up")
        composeRule.onNodeWithTag("Updated Category").assertExists("Search item should be visible")
        composeRule.onNodeWithTag("Another Category").assertDoesNotExist()

        //Search again
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextClearance()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("ano")
        composeRule.onNodeWithTag("Another Category").assertExists("Search item should be visible")
        composeRule.onNodeWithTag("Updated Category").assertDoesNotExist()

        //Search again with invalid data should show not found
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("zdks")
        composeRule.onNodeWithTag("Another Category").assertDoesNotExist()
        composeRule.onNodeWithTag("Updated Category").assertDoesNotExist()
        composeRule.onNodeWithText("Searched Item Not Found").assertExists("Search item not found is not visible")

        //close search bar and all item should be visible
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
        composeRule.onNodeWithText("Categories").assertExists("Category screen is not visible")
        composeRule.onNodeWithTag("Another Category").assertExists("another category item should be visible")
        composeRule.onNodeWithTag("Updated Category").assertExists("updated category item should be visible")

        //click on filter icon and check filter screen is visible or not
        composeRule.onNodeWithContentDescription("Filter Category").performClick()
        composeRule.onNodeWithText("Filter Category").assertExists("Filter category screen is not visible")
        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).assertExists("Filter category screen close icon is not visible")
        composeRule.onNodeWithTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN).performClick()

        //select all item and delete category
        composeRule.onNodeWithTag("Updated Category").performClick()
        composeRule.onNodeWithContentDescription("Select All Category").performClick()
        composeRule.onNodeWithContentDescription("Delete Category").performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Category dialog not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        //show category not available
        composeRule.onNodeWithText("Categories Not Available").assertExists("Categories Not Available is not visible")

    }
}