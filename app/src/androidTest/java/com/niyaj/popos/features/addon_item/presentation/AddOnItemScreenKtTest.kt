package com.niyaj.popos.features.addon_item.presentation

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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.features.MainActivity
import com.niyaj.popos.features.RealmModule
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_ADD_EDIT_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_DELETE_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_DESELECT_ALL_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_EDIT_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_FILTER_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_FILTER_BY_NAME
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_ITEM_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_ALREADY_EXIST_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_DIGIT_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_EMPTY_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_ERROR_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_FIELD
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_LENGTH_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NOT_AVAIlABLE
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_ERROR_TAG
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_FIELD
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_SCREEN
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_SEARCH_BAR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_SEARCH_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_SELECT_ALL_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADD_EDIT_ADDON_SCREEN
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADD_EDIT_SCREEN_CLOSE_BUTTON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.CREATE_NEW_ADD_ON
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.FILTER_ADD_ON_ITEM
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.NO_ITEMS_IN_ADDON
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.Navigation
import com.niyaj.popos.features.components.util.SheetLayout
import com.niyaj.popos.features.destinations.AddOnItemScreenDestination
import com.niyaj.popos.util.Constants.NEGATIVE_BUTTON
import com.niyaj.popos.util.Constants.POSITIVE_BUTTON
import com.niyaj.popos.util.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.popos.util.Constants.SORT_ASCENDING
import com.niyaj.popos.util.Constants.STANDARD_BACK_BUTTON
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@UninstallModules(RealmModule::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class AddOnItemScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var currentBottomSheet = mutableStateOf<BottomSheetScreen?>(null)

    private lateinit var navController: NavHostController

    //Basic Details
    private val name = "testing"
    private val price = "6"
    private val itemTag = ADDON_ITEM_TAG.plus(name).plus(price)

    private val updatedName = "updated"
    private val updatedPrice = "8"
    private val updatedItemTag = ADDON_ITEM_TAG.plus(updatedName).plus(updatedPrice).plus("0")

    private val newItem = "newitem"
    private val newPrice = "10"
    private val newTag = ADDON_ITEM_TAG.plus(newItem).plus(newPrice)


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
                        startRoute = AddOnItemScreenDestination,
                    )
                }

            }
        }
    }

    private fun createNewAddOnItem(itemName: String = newItem, itemPrice: String = newPrice): String {
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(CREATE_NEW_ADD_ON).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(ADD_EDIT_ADDON_SCREEN).assertExists("Create New AddOn Item Screen Not Visible")

        // Create new AddOn Item
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput(itemName)
        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(itemPrice)

        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()

        composeRule.waitForIdle()

        //Verify addon Item created
        val currentBackStackEntry = navController.currentBackStackEntry?.destination?.route

        assertThat(currentBackStackEntry).isEqualTo(AddOnItemScreenDestination.route)

        val tag = ADDON_ITEM_TAG.plus(itemName).plus(itemPrice)

        composeRule.onNodeWithTag(tag).assertExists("Add On Item Not Found")

        composeRule.waitForIdle()

        composeRule.onRoot().performClick()

        return tag
    }

    @Test
    fun a_checkAddOnItemScreenVisible() {
        composeRule.onNodeWithTag(ADDON_SCREEN).assertExists("Unable to find AddOn Screen")
    }

    @Test
    fun b_checkAddOnItemScreenEmptyWithMessageAndButton() {
        composeRule.onNodeWithTag(ADDON_NOT_AVAIlABLE).assertExists("Empty AddOn Item Screen Box Not Visible")

        composeRule.onNodeWithText(NO_ITEMS_IN_ADDON).assertExists("Empty AddOn Items Message Not Visible")

        composeRule.onNodeWithTag(CREATE_NEW_ADD_ON).assertExists("Create New AddOn Item Button Not Visible")

        composeRule.onNodeWithTag(ADDON_SEARCH_BUTTON).assertDoesNotExist()

        composeRule.onNodeWithTag(ADDON_FILTER_BUTTON).assertDoesNotExist()
    }

    @Test
    fun c_navigateToCreateNewAddOnScreen() {
        composeRule.onNodeWithTag(CREATE_NEW_ADD_ON).performClick()

        composeRule.onNodeWithTag(ADD_EDIT_ADDON_SCREEN).assertExists("Create New AddOn Item Screen Not Visible")

        composeRule.onNodeWithText(CREATE_NEW_ADD_ON).assertExists("Title Is Not Visible")

        composeRule.onNodeWithTag(ADDON_NAME_FIELD).assertExists("Addon Name Field Not Visible")

        composeRule.onNodeWithText(ADDON_PRICE_FIELD).assertExists("Addon Price Field Not Visible")

        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).assertExists("Addon AddEdit Button Not Visible")
    }

    @Test
    fun d_performInputAndCheckValidationError() {
        composeRule.onNodeWithTag(CREATE_NEW_ADD_ON).performClick()

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

    @Test
    fun e_createNewAddonItemAndNavigateBack() {
        createNewAddOnItem(itemName = name, price)
    }

    @Test
    fun f_createNewAddonItemThatAlreadyExistReturnError() {

        try {
            composeRule.onNodeWithTag(itemTag).assertExists()
        }catch (e: AssertionError) {
            createNewAddOnItem(itemName = name, price)
        }

        composeRule.mainClock.advanceTimeBy(1000L)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(CREATE_NEW_ADD_ON).performClick()
        composeRule.onNodeWithTag(ADD_EDIT_ADDON_SCREEN).assertExists("Create New AddOn Item Screen Not Visible")

        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput(name)

        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(price)

        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()

        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertExists("Add On Item Name Error Not Visible")

        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertTextEquals(ADDON_NAME_ALREADY_EXIST_ERROR)

        composeRule.onNodeWithTag(ADD_EDIT_SCREEN_CLOSE_BUTTON).performClick()

        val currentBackStackEntry = navController.currentBackStackEntry?.destination?.route

        assertThat(currentBackStackEntry).isEqualTo(AddOnItemScreenDestination.route)

    }

    @Test
    fun g_selectAndDeselectSingleAddOnItem() {
        composeRule.onNodeWithTag(itemTag).assertExists("Add On Item Not Found")

        composeRule.onNodeWithTag(itemTag).performClick()

        composeRule.onNodeWithTag(ADDON_EDIT_BUTTON).assertExists("AddOn Edit Button Not Displayed")
        composeRule.onNodeWithTag(ADDON_DELETE_BUTTON).assertExists("AddOn Delete Button Not Displayed")
        composeRule.onNodeWithTag(ADDON_SELECT_ALL_BUTTON).assertExists("AddOn Select All Button Not Displayed")
        composeRule.onNodeWithTag(ADDON_DESELECT_ALL_BUTTON).assertExists("AddOn Deselect All Button Not Displayed")


        composeRule.onNodeWithTag(itemTag).performClick()

        composeRule.onNodeWithTag(ADDON_EDIT_BUTTON).assertDoesNotExist()
        composeRule.onNodeWithTag(ADDON_DELETE_BUTTON).assertDoesNotExist()
        composeRule.onNodeWithTag(ADDON_SELECT_ALL_BUTTON).assertDoesNotExist()
        composeRule.onNodeWithTag(ADDON_DESELECT_ALL_BUTTON).assertDoesNotExist()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun h_selectAndDeselectMultipleAddOnItem() = runTest {
        try {
            composeRule.onNodeWithTag(itemTag).assertExists()
        }catch (e: AssertionError){
            createNewAddOnItem(name, price)
            composeRule.mainClock.advanceTimeBy(1000L)
        }

        try {
            composeRule.onNodeWithTag(newTag).assertExists()
        }catch (e: AssertionError){
            createNewAddOnItem()
            composeRule.mainClock.advanceTimeBy(1000L)
        }

        //Select and deselect manually
        composeRule.onNodeWithTag(itemTag).assertExists().performClick()
        composeRule.onNodeWithTag(newTag).assertExists().performClick()
        composeRule.onNodeWithTag(ADDON_DESELECT_ALL_BUTTON).assertExists().performClick()

        //Select one item the remaining selection with SelectAll Button
        composeRule.onNodeWithTag(itemTag).assertExists().performClick()
        composeRule.onNodeWithTag(ADDON_SELECT_ALL_BUTTON).assertExists().performClick()
        composeRule.onNodeWithTag(newTag).assertExists().performClick()

        //Deselect using SelectAll Button and check isn't visible
        composeRule.onNodeWithTag(ADDON_SELECT_ALL_BUTTON).assertExists().performClick()

        composeRule.mainClock.advanceTimeBy(1000L)
        composeRule.onNodeWithTag(ADDON_SELECT_ALL_BUTTON).assertDoesNotExist()
    }

    @Test
    fun i_selectSingleItemAndEditDataReturnFalse() = runTest {

        //Select Single Item
        composeRule.onNodeWithTag(itemTag).assertExists("Add On Item Not Found")
        composeRule.onNodeWithTag(itemTag).performClick()

        composeRule.onNodeWithTag(ADDON_EDIT_BUTTON).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(5000L)

        composeRule.onNodeWithTag(ADD_EDIT_ADDON_SCREEN).assertExists("Create New AddOn Item Screen Not Visible")

        // Create new AddOn Item.
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput(newItem)

        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(newPrice)

        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()

        composeRule.waitForIdle()
        delay(5000L)

        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertExists("Add On Item Name Error Not Visible")
        composeRule.onNodeWithTag(ADDON_NAME_ERROR_TAG).assertTextEquals(ADDON_NAME_ALREADY_EXIST_ERROR)
        composeRule.onNodeWithTag(ADD_EDIT_SCREEN_CLOSE_BUTTON).performClick()

        val currentBackStackEntry = navController.currentBackStackEntry?.destination?.route
        assertThat(currentBackStackEntry).isEqualTo(AddOnItemScreenDestination.route)
    }

    @Test
    fun j_selectSingleItemAndEditDataReturnSuccess() = runTest {

        //Select Single Item
        composeRule.onNodeWithTag(newTag).assertExists("Add On Item Not Found")
        composeRule.onNodeWithTag(newTag).performClick()

        composeRule.waitForIdle()
        delay(5000L)

        // Check edit buttons are visible
        composeRule.onNodeWithTag(ADDON_EDIT_BUTTON).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(5000L)

        composeRule.onNodeWithTag(ADD_EDIT_ADDON_SCREEN).assertExists("Create New AddOn Item Screen Not Visible")

        // Create new AddOn Item
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput(updatedName)

        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(updatedPrice)

        composeRule.onNodeWithTag(ADDON_ADD_EDIT_BUTTON).performClick()

        composeRule.waitForIdle()
        delay(5000L)

        //Verify addon Item created
        val currentBackStackEntry = navController.currentBackStackEntry?.destination?.route

        // Verify navigated back
        assertThat(currentBackStackEntry).isEqualTo(AddOnItemScreenDestination.route)

    }

    @Test
    fun k_selectSingleItemAndPerformDeleteAndCancelDeletion() = runTest {

        //Select Single Item
        composeRule.onNodeWithTag(updatedItemTag).assertExists().performClick()

        // Check all buttons are visible
        composeRule.onNodeWithTag(ADDON_DELETE_BUTTON).assertExists("Delete Button not visible").performClick()

        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(1000L)

        composeRule.mainClock.advanceTimeBy(1000L)

        //Check that the item is not deleted
        composeRule.onNodeWithTag(updatedItemTag).assertExists("Add On Item Not Found")
    }

    @Test
    fun l_selectMultipleItemAndPerformDeleteAndCancelDeletion() = runTest{

        //Select Multiple Item
        composeRule.onNodeWithTag(updatedItemTag).assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(itemTag).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(5000L)

        // Check all buttons are visible
        composeRule.onNodeWithTag(ADDON_EDIT_BUTTON).assertDoesNotExist()
        composeRule.onNodeWithTag(ADDON_DELETE_BUTTON).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(5000L)

        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(50000L)

        // Verify Buttons aren't visible
        composeRule.onNodeWithTag(ADDON_EDIT_BUTTON).assertDoesNotExist()
        composeRule.onNodeWithTag(ADDON_DELETE_BUTTON).assertDoesNotExist()
        composeRule.onNodeWithTag(ADDON_SELECT_ALL_BUTTON).assertDoesNotExist()
        composeRule.onNodeWithTag(ADDON_DESELECT_ALL_BUTTON).assertDoesNotExist()

        //Check that the item is not deleted
        composeRule.onNodeWithTag(itemTag).assertIsDisplayed()
        composeRule.onNodeWithTag(updatedItemTag).assertIsDisplayed()
    }

    @Test
    fun m_selectSingleItemAndDeleteData() = runTest{

        //Select Single Item
        composeRule.onNodeWithTag(updatedItemTag).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(5000L)

        // Check all delete button is visible
        composeRule.onNodeWithTag(ADDON_DELETE_BUTTON).assertExists("AddOn Delete Button Not Displayed").performClick()

        composeRule.waitForIdle()
        delay(5000L)

        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        delay(5000L)

        //Check that the item is not deleted
        composeRule.onNodeWithTag(updatedItemTag).assertDoesNotExist()
    }

    @Test
    fun n_selectMultipleItemAndDeleteData() = runTest {
        try {
            composeRule.onNodeWithTag(itemTag).assertExists()
        }catch (e: AssertionError){
            createNewAddOnItem(name, price)
            composeRule.mainClock.advanceTimeBy(1000L)
        }

        try {
            composeRule.onNodeWithTag(newTag).assertExists()
        }catch (e: AssertionError){
            createNewAddOnItem()
            composeRule.mainClock.advanceTimeBy(1000L)
        }

        composeRule.mainClock.advanceTimeBy(1000L)

        composeRule.onNodeWithTag(itemTag).assertExists().performClick()
        composeRule.onNodeWithTag(newTag).assertExists().performClick()

        composeRule.mainClock.advanceTimeBy(1000L)

        composeRule.onNodeWithTag(ADDON_DELETE_BUTTON).assertExists().performClick()

        composeRule.waitForIdle()
        delay(1000L)

        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertIsDisplayed()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(5000L)
        delay(50000L)

        //Check that the item is deleted
        composeRule.onNodeWithTag(itemTag).assertDoesNotExist()
        composeRule.onNodeWithTag(newTag).assertDoesNotExist()
    }

    @Test
    fun o_checkSearchItemVisibleAndSearchItem() {
        runTest {
            try {
                composeRule.onNodeWithTag(itemTag).assertExists()
            }catch (e: AssertionError){
                composeRule.waitForIdle()
                delay(1000L)

                createNewAddOnItem(name, price)
            }

            composeRule.waitForIdle()
            composeRule.mainClock.advanceTimeBy(5000L)
            delay(1000L)

            try {
                composeRule.onNodeWithTag(newTag).assertExists()
            }catch (e: AssertionError){
                composeRule.waitForIdle()
                delay(1000L)

                createNewAddOnItem()
                delay(1000L)
            }

            composeRule.waitForIdle()
            composeRule.mainClock.advanceTimeBy(5000L)
            delay(1000L)

            composeRule.onNodeWithTag(itemTag).assertExists("AddOnItem not visible")
            composeRule.onNodeWithTag(newTag).assertExists("New AddOn Item not visible")

            composeRule.onNodeWithTag(ADDON_SEARCH_BUTTON).assertExists("Search icon not visible").performClick()

            composeRule.waitForIdle()
            delay(5000L)

            composeRule.onNodeWithTag(ADDON_SEARCH_BAR).assertExists("Search bar not visible")

            // Perform search using string
            composeRule.onNodeWithTag(ADDON_SEARCH_BAR).performTextInput("item")

            composeRule.waitForIdle()
            composeRule.mainClock.advanceTimeBy(5000L)
            delay(1000L)

            composeRule.onNodeWithTag(newTag).assertExists()

            composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertExists("Close button not visible").performClick()

            composeRule.waitForIdle()
            composeRule.mainClock.advanceTimeBy(5000L)
            delay(1000L)

            composeRule.onNodeWithTag(itemTag).assertExists("First Search AddOnItem not visible")
            composeRule.onNodeWithTag(newTag).assertExists("First Search New AddOn Item not visible")

            // Perform search using number
            composeRule.onNodeWithTag(ADDON_SEARCH_BAR).performTextInput("6")

            composeRule.waitForIdle()
            composeRule.mainClock.advanceTimeBy(5000L)
            delay(5000L)

            composeRule.onNodeWithTag(itemTag).assertExists("Searched item with input 6 is not found")

            composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).assertExists("Back Button not visible").performClick()

            composeRule.waitForIdle()
            composeRule.mainClock.advanceTimeBy(5000L)
            delay(5000L)

            composeRule.onNodeWithTag(CREATE_NEW_ADD_ON).assertExists()
            composeRule.onNodeWithTag(itemTag).assertExists("AddOnItem not visible")
            composeRule.onNodeWithTag(newTag).assertExists("New AddOn Item not visible")

        }
    }

    @Test
    fun p_filterAddonItemsTest() {
        val data = createListOfAddonItems()

        composeRule.waitUntil {
            data.isNotEmpty()
        }

        composeRule.waitForIdle()
        Thread.sleep(1000)

        data.forEach {tag ->
            composeRule.waitForIdle()
            composeRule.onNodeWithTag(tag).assertExists("Item with tag $tag not visible")
        }

        composeRule.onNodeWithTag(ADDON_FILTER_BUTTON).assertExists()

        validateOrderByIdAscending()

    }

    private fun validateOrderByIdAscending(): Boolean {
        composeRule.waitForIdle()
        Thread.sleep(500)

        composeRule.onNodeWithTag(ADDON_FILTER_BUTTON).assertExists()
        composeRule.onNodeWithTag(ADDON_FILTER_BUTTON).performClick()
        Thread.sleep(500)

        try {
            composeRule.onNodeWithTag(FILTER_ADD_ON_ITEM).assertExists("Filter AddOn Screen Not Found")
            Thread.sleep(500)

            composeRule.onNodeWithTag(SORT_ASCENDING).performClick()

            composeRule.onNodeWithTag(ADDON_FILTER_BUTTON).performClick()
            Thread.sleep(500)

            composeRule.onNodeWithTag(ADDON_FILTER_BY_NAME).performClick()
        }catch(e: AssertionError) {
            composeRule.onNodeWithTag(ADDON_FILTER_BUTTON).performClick()
            Thread.sleep(500)

            composeRule.onNodeWithTag(FILTER_ADD_ON_ITEM).assertExists("Retry Filter AddOn Screen Not Found")
            Thread.sleep(500)

            composeRule.onNodeWithTag(SORT_ASCENDING).performClick()

            Thread.sleep(500)
            composeRule.onNodeWithTag(ADDON_FILTER_BUTTON).performClick()
            composeRule.onNodeWithTag(ADDON_FILTER_BY_NAME).performClick()
        }

        return true
    }


    private fun createListOfAddonItems(): List<String> {
        val addOnItems = mutableListOf<AddOnItem>()
        val tags = mutableListOf<String>()

        ('A'..'E').forEachIndexed { index, c ->
            addOnItems.add(
                AddOnItem(
                    addOnItemId = index.toString(),
                    itemName = c.toString().plus("item"),
                    itemPrice = index.plus(5),
                    createdAt = System.currentTimeMillis().plus(index).toString()
                )
            )
        }
        addOnItems.shuffle()

        addOnItems.forEach { addOnItem ->
            val tag = createNewAddOnItem(addOnItem.itemName, addOnItem.itemPrice.toString())

            tags.add(tag)

            Thread.sleep(5000)
        }

        return tags
    }

}