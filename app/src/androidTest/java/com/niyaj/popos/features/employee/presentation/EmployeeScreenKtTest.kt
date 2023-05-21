package com.niyaj.popos.features.employee.presentation

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.niyaj.popos.features.MainActivity
import com.niyaj.popos.features.common.di.RealmModule
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.Navigation
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.util.EmployeeSalaryType
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.ADD_EDIT_EMPLOYEE_BUTTON
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_DETAILS_SCREEN
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_JOINED_DATE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_MONTHLY_SALARY_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_MONTHLY_SALARY_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_NAME_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_NAME_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_PHONE_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_PHONE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_POSITION_ERROR
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_POSITION_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_SALARY_TYPE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_TYPE_FIELD
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.REMAINING_AMOUNT_TEXT
import com.niyaj.popos.features.employee.domain.util.EmployeeType
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.features.employee.domain.util.SalaryType
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ABSENT_DATE_FIELD
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ABSENT_REASON_FIELD
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentScreenTestTags.ADD_EDIT_ABSENT_ENTRY_BTN
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.ADD_EDIT_PAYMENT_ENTRY_BUTTON
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.GIVEN_AMOUNT_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.GIVEN_AMOUNT_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.GIVEN_DATE_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.PAYMENT_TYPE_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_EMPLOYEE_NAME_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_EMPLOYEE_NAME_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_NOTE_ERROR
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_NOTE_FIELD
import com.niyaj.popos.features.employee_salary.domain.util.SalaryScreenTags.SALARY_TYPE_FIELD
import com.niyaj.popos.utils.Constants
import com.niyaj.popos.utils.Constants.NEGATIVE_BUTTON
import com.niyaj.popos.utils.Constants.POSITIVE_BUTTON
import com.niyaj.popos.utils.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.popos.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.popos.utils.Constants.STANDARD_SEARCH_BAR
import com.niyaj.popos.utils.toDate
import com.niyaj.popos.utils.toRupee
import com.niyaj.popos.utils.toSalaryDate
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
class EmployeeScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavHostController

    private val newEmployee = Employee(
        employeeName = "New Employee",
        employeePhone = "9078563412",
        employeeSalary = "12000",
        employeeSalaryType = EmployeeSalaryType.Weekly.salaryType,
        employeeType = EmployeeType.PartTime.employeeType,
        employeePosition = "Master",
    )

    private val updatedEmployee = Employee(
        employeeName = "Updated Employee",
        employeePhone = "8078563412",
        employeeSalary = "15000",
        employeeSalaryType = EmployeeSalaryType.Daily.salaryType,
        employeeType = EmployeeType.FullTime.employeeType,
        employeePosition = "Chef",
    )

    private val newEmployeeTag = newEmployee.employeeName.plus("Tag")
    private val updatedEmployeeTag = updatedEmployee.employeeName.plus("Tag")

    private val todayDate = System.currentTimeMillis().toString().toDate

    private val newAbsentDate = 14
    private val newAbsentDateTag = newEmployee.employeeName.plus(newAbsentDate)

    private val updatedAbsentDate = 16
    private val updatedAbsentDateTag = updatedEmployee.employeeName.plus(updatedAbsentDate)

    private val newUpdatedAbsentDateTag = newEmployee.employeeName.plus(updatedAbsentDate)


    private val joinedDate = 12
    private val todayFormattedDate = System.currentTimeMillis().toString().toSalaryDate

    private val newTodayDateTag = newEmployee.employeeName.plus(System.currentTimeMillis().toString().toDate)
    private val updatedTodayDateTag = updatedEmployee.employeeName.plus(System.currentTimeMillis().toString().toDate)

    private val newEditTag = newEmployee.employeeName.plus("Edit")
    private val newPaymentTag = newEmployee.employeeName.plus("Payment")
    private val newAbsentTag = newEmployee.employeeName.plus("Absent")
    private val newDeleteTag = newEmployee.employeeName.plus("Delete")

    private val updatedEditTag = updatedEmployee.employeeName.plus("Edit")
    private val updatedPaymentTag = updatedEmployee.employeeName.plus("Payment")
    private val updatedAbsentTag = updatedEmployee.employeeName.plus("Absent")
    private val updatedDeleteTag = updatedEmployee.employeeName.plus("Delete")

    private val newSalary = EmployeeSalary(
        salaryType = SalaryType.Salary.salaryType,
        employeeSalary = "500",
        salaryPaymentType = PaymentType.Cash.paymentType,
    )

    private val updatedSalary = EmployeeSalary(
        salaryType = SalaryType.Advanced.salaryType,
        employeeSalary = "800",
        salaryPaymentType = PaymentType.Online.paymentType,
    )

    private val newAmountTag = newEmployee.employeeName.plus(newSalary.employeeSalary)
    private val newUpdatedAmountTag = newEmployee.employeeName.plus(updatedSalary.employeeSalary)
    private val updatedAmountTag = updatedEmployee.employeeName.plus(updatedSalary.employeeSalary)
    private val updatedNewAmountTag = updatedEmployee.employeeName.plus(newSalary.employeeSalary)


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
                    startRoute = EmployeeScreenDestination,
                )
            }
        }
    }

    //Employee feature test methods
    @Test
    fun a_create_update_employee_test() {
        checkScreenVisibleOrNot()

        checkAddNewEmployeeButtonAndScreenIsVisible()

        checkEmptyValidationErrorOnCreatingEmployee()

        checkLengthValidationOnInvalidDataWhileCreatingEmployee()

        checkCharactersAndDigitValidationErrorWhileCreatingEmployee()

        createNewEmployeeAndNavigateBackToEmployeeScreenAndCheckDetails()

        clickOnEmployeeAndCheckDoesItNavigateToDetailsScreenAndOtherDataIsPresentOrNot()

        clickOnEmployeeDetailsCardAndCheckAllEmployeeDetailsAreVisible()

        pressBackAndNavigateToEmployeeScreen()

        swipeRightOnNewEmployeeCardAndCheckPaymentEntryAndEditButtonsAreVisible()

        clickOnEditButtonAndCheckEditEmployeeScreenIsVisibleAndEditEmployeeData()

        checkRecentlyUpdatedEmployeeIsVisibleOrNotOnEmployeeScreen()

        clickOnUpdatedEmployeeAndCheckDoesItNavigateToDetailsScreen()

        clickOnEmployeeDetailsCardAndCheckAllUpdatedEmployeeDetailsAreVisible()

        navigateBackAndSwipeRightOnUpdatedEmployeeCardAndClickOnPaymentEntryButtonAndCheckPaymentEntryScreenIsVisibleOrNot()

        swipeRightOnUpdatedEmployeeAndCheckAbsentAndDeleteEmployeeButtonIsVisible()

        clickOnAbsentButtonAndCheckAddAbsentEntryScreenIsVisibleOrNot()

        clickOnDeleteButtonAndCheckDeleteDialogIsVisibleOrNotAndCancelDeletion()

        createNewEmployeeWithValidDataAndCheckItsVisibleOrNotOnEmployeeScreen()

        checkSearchAndFilterIconIsVisibleOnEmployeeScreen()

        clickOnSearchButtonAndSearchUsingNewKeywordAndCheckOnNewEmployeeIsVisibleOnEmployeeScreen()

        searchUsingUpdatedKeywordAndOnlyUpdatedEmployeeIsVisibleOnEmployeeScreen()

        searchUsingRandomStringAndShouldShownSearchedItemNotFoundOnEmployeeScreen()

        pressBackAndCloseSearchBarAllItemShouldVisibleOnScreen()

        clickOnFilterButtonAndCheckFilterScreenIsVisible()

    }

    @Test
    fun b_alreadyExistsValidationCheck() {
        createNewEmployeeThatAlreadyExistsReturnsValidationError()
        updateEmployeeDataThatAlreadyExistsReturnValidationError()
    }

    @Test
    fun c_deleteEmployeeTest() {
        swipeRightOnNewEmployeeAndClickOnDeleteAndDeleteThemAndCheckItsNotVisibleOnScreen()
    }

    private fun checkScreenVisibleOrNot() {
        //Check Employee screen and Add New Employee button is visible or not
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
    }

    private fun checkAddNewEmployeeButtonAndScreenIsVisible() {
        //Click on Add New Employee button and check it navigates to Add New Employee Screen
        composeRule.onNodeWithText("Add New Employee".uppercase()).performClick()
        composeRule.onNodeWithText("Create New Employee").assertExists("Add New Employee screen is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).assertExists("Employee Name field is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).assertExists("Employee Phone field is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).assertExists("Employee Monthly Salary field is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD).assertExists("Employee Salary Type field is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_TYPE_FIELD).assertExists("Employee Type field is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_FIELD).assertExists("Employee Positions field is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_JOINED_DATE_FIELD).assertExists("Employee Join Date field is not visible")
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).assertExists("Employee AddEdit button is not visible")

    }

    private fun checkEmptyValidationErrorOnCreatingEmployee() {
        //Click on Add New Employee button and check validation error
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_NAME_ERROR).assertExists("Employee Name error is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertExists("Employee Phone error is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_ERROR).assertExists("Employee Monthly Salary Error is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_ERROR).assertExists("Employee Position Error is not visible")

        composeRule.onNodeWithTag(EMPLOYEE_NAME_ERROR).assertTextEquals("Employee name must not be empty")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertTextEquals("Phone no must not be empty")
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_ERROR).assertTextEquals("Salary must not be empty")
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_ERROR).assertTextEquals("Employee position is required")
    }

    private fun checkLengthValidationOnInvalidDataWhileCreatingEmployee() {
        // Check with invalid data return validation error
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput("hi")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput("89")
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextInput("90")

        Espresso.closeSoftKeyboard()

        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()

        composeRule.onNodeWithTag(EMPLOYEE_NAME_ERROR).assertTextEquals("Employee name must be more than 4 characters")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertTextEquals("Phone must be 10(2) digits")
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_ERROR).assertTextEquals("Salary is in invalid")
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_ERROR).assertTextEquals("Employee position is required")
    }

    private fun checkCharactersAndDigitValidationErrorWhileCreatingEmployee() {
        // Check with invalid data return validation error
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput("hello5")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput("908967hi")
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextInput("7hi")

        Espresso.closeSoftKeyboard()

        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithTag(EMPLOYEE_NAME_ERROR).assertTextEquals("Employee name must not contain any digit")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertTextEquals("Phone must not contain a letter")
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_ERROR).assertTextEquals("Salary must not contain any characters")
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_ERROR).assertTextEquals("Employee position is required")

    }

    private fun createNewEmployeeAndNavigateBackToEmployeeScreenAndCheckDetails() {
        // Create employee with valid data should navigate back to employee screen and created employee should visible in screen
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput(newEmployee.employeeName)
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput(newEmployee.employeePhone)
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextInput(newEmployee.employeeSalary)
        composeRule.onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeeSalaryType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeeType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeePosition).performClick()

        Espresso.closeSoftKeyboard()
        composeRule.waitForIdle()
        Thread.sleep(500)
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()

        //Check Recently created employee is visible or not
        composeRule.waitForIdle()
        Thread.sleep(500)

        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertExists()
        composeRule.onNodeWithText(newEmployee.employeePhone).assertExists("New Employee phone is not visible")

    }

    private fun clickOnEmployeeAndCheckDoesItNavigateToDetailsScreenAndOtherDataIsPresentOrNot() {
        //Click on employee and it should navigate to employee details screen
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].performClick()
        composeRule.onNodeWithTag(EMPLOYEE_DETAILS_SCREEN).assertExists("Employee Details Screen is not visible")
        composeRule.onNodeWithTag("CalculateSalary").assertExists("CalculateSalary Card is not visible")
        composeRule.onNodeWithTag("EmployeeDetails").assertExists("Employee Details card is not visible")
        composeRule.onNodeWithTag("PaymentDetails").assertExists("PaymentDetails card is not visible")
        composeRule.onNodeWithTag("AbsentDetails").assertExists("Absent Details card is not visible")
    }

    private fun clickOnEmployeeDetailsCardAndCheckAllEmployeeDetailsAreVisible() {
        //Click on employee details and check all details are visible
        composeRule.onNodeWithTag("EmployeeDetails").performClick()

        composeRule.onNodeWithTag(newEmployee.employeeName, true).assertExists("Employee Name is not visible")
        composeRule.onNodeWithTag(newEmployee.employeePhone, true).assertExists("Employee Phone is not visible")
        composeRule.onNodeWithTag(newEmployee.employeeSalary.toRupee, true).assertExists("Employee salary is not visible")
        composeRule.onNodeWithTag(newEmployee.employeeType, true).assertExists("Employee Type is not visible")
        composeRule.onNodeWithTag(newEmployee.employeeSalaryType, true).assertExists("Employee Salary Type is not visible")
        composeRule.onNodeWithTag(newEmployee.employeePosition, true).assertExists("Employee Position is not visible")

//        composeRule.onNodeWithTag(newEmployee.employeeName, true).assertTextContains(newEmployee.employeeName, true)
//        composeRule.onNodeWithTag(newEmployee.employeePhone, true).assertTextContains(newEmployee.employeePhone, true)
//        composeRule.onNodeWithTag(newEmployee.employeeSalary.toRupee, true).assertTextContains(newEmployee.employeeSalary.toRupee, true)
//        composeRule.onNodeWithTag(newEmployee.employeeType, true).assertTextContains(newEmployee.employeeType, true)
//        composeRule.onNodeWithTag(newEmployee.employeeSalaryType, true).assertTextContains(newEmployee.employeeSalaryType, true)
//        composeRule.onNodeWithTag(newEmployee.employeePosition, true).assertTextContains(newEmployee.employeePosition, true)
//        composeRule.onNodeWithTag(newTodayDateTag).assertTextContains(todayFormattedDate, true)
    }

    private fun pressBackAndNavigateToEmployeeScreen() {
        composeRule.waitForIdle()

        //Click on back button and navigate back to employee screen
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
    }

    private fun swipeRightOnNewEmployeeCardAndCheckPaymentEntryAndEditButtonsAreVisible() {
        composeRule.waitForIdle()

        //Swipe left the employee and click on edit button to edit employee data
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].performTouchInput {
            swipeRight()
        }
        composeRule.onNodeWithContentDescription("Add Payment Entry").assertExists("Add Payment Entry button is not visible")
        composeRule.onNodeWithContentDescription("Edit Employee").assertExists("Edit Employee button is not visible")

    }

    private fun clickOnEditButtonAndCheckEditEmployeeScreenIsVisibleAndEditEmployeeData () {
        //Click on edit button to update employee
        composeRule.onNodeWithContentDescription("Edit Employee").performClick()
        composeRule.onNodeWithText("Update Employee").assertExists("Update Employee screen is not visible")

        composeRule.waitForIdle()

        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput(updatedEmployee.employeeName)
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput(updatedEmployee.employeePhone)
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextInput(updatedEmployee.employeeSalary)
        composeRule.onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(updatedEmployee.employeeSalaryType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(updatedEmployee.employeeType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_FIELD).performClick()
        composeRule.onNodeWithText(updatedEmployee.employeePosition).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_JOINED_DATE_FIELD).performClick()
        composeRule.onAllNodesWithTag("dialog_date_selection_$joinedDate")[0].assertExists("Date picker is not visible")
        composeRule.onAllNodesWithTag("dialog_date_selection_$joinedDate")[0].performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()
    }

    private fun checkRecentlyUpdatedEmployeeIsVisibleOrNotOnEmployeeScreen() {
        //Check Recently updated employee is visible or not
        composeRule.waitForIdle()
        Thread.sleep(500)
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].assertExists()
        composeRule.onNodeWithText(updatedEmployee.employeePhone).assertExists("New Employee phone is not visible")

    }

    private fun clickOnUpdatedEmployeeAndCheckDoesItNavigateToDetailsScreen() {
        //Click on employee and it should navigate to employee details screen
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].performClick()
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].performClick()
        composeRule.onNodeWithTag(EMPLOYEE_DETAILS_SCREEN).assertExists("Employee Details Screen is not visible")
    }

    private fun clickOnEmployeeDetailsCardAndCheckAllUpdatedEmployeeDetailsAreVisible() {
        //Click updated employee details are visible
        composeRule.onNodeWithTag("EmployeeDetails").performClick()
        composeRule.onNodeWithTag(updatedEmployee.employeeName, true).assertExists("Employee Name is not visible")
        composeRule.onNodeWithTag(updatedEmployee.employeePhone, true).assertExists("Employee Phone is not visible")
        composeRule.onNodeWithTag(updatedEmployee.employeeSalary.toRupee, true).assertExists("Employee salary is not visible")
        composeRule.onNodeWithTag(updatedEmployee.employeeType, true).assertExists("Employee Type is not visible")
        composeRule.onNodeWithTag(updatedEmployee.employeeSalaryType, true).assertExists("Employee Salary Type is not visible")
        composeRule.onNodeWithTag(updatedEmployee.employeePosition, true).assertExists("Employee Position is not visible")
    }

    private fun navigateBackAndSwipeRightOnUpdatedEmployeeCardAndClickOnPaymentEntryButtonAndCheckPaymentEntryScreenIsVisibleOrNot() {
        //PressBack
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")


        //Swipe left the employee and click on add payment entry button and check payment entry screen is visible or not
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].performTouchInput { swipeRight() }
        composeRule.onNodeWithContentDescription("Add Payment Entry").assertExists("Add Payment Entry button is not visible")
        composeRule.onNodeWithContentDescription("Edit Employee").assertExists("Edit Employee button is not visible")
        composeRule.onNodeWithContentDescription("Add Payment Entry").performClick()
        composeRule.onNodeWithText("Add Payment Entry").assertExists("Add Payment Entry Screen is not visible")
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
    }

    private fun swipeRightOnUpdatedEmployeeAndCheckAbsentAndDeleteEmployeeButtonIsVisible () {
        //Swipe right and check absent entry button and delete button is visible or not
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].performClick()
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].performTouchInput { swipeLeft() }
        composeRule.onNodeWithContentDescription("Mark as Absent").assertExists("Mark as Absent button is not visible")
        composeRule.onNodeWithContentDescription("Delete Employee").assertExists("Delete Employee button is not visible")
    }

    private fun clickOnAbsentButtonAndCheckAddAbsentEntryScreenIsVisibleOrNot() {
        //Click on mark as absent button and check absent screen is visible or not
        composeRule.onNodeWithContentDescription("Mark as Absent").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("Add Absent Entry").assertExists("Add Absent Entry Screen is not visible")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
    }

    private fun clickOnDeleteButtonAndCheckDeleteDialogIsVisibleOrNotAndCancelDeletion() {
        //Click on delete button and check delete dialog is visible or not and press cancel to cancel deletion
        composeRule.onNodeWithContentDescription("Delete Employee").performClick()
        composeRule.onNodeWithText("Delete Employee?").assertExists("Delete Employee dialog is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Employee dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete Employee dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).performClick()
    }

    private fun createNewEmployeeWithValidDataAndCheckItsVisibleOrNotOnEmployeeScreen() {
        //Create new employee with valid data
        composeRule.onNodeWithText("Add New Employee".uppercase()).performClick()
        composeRule.onNodeWithText("Create New Employee").assertExists("Add New Employee screen is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput(newEmployee.employeeName)
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput(newEmployee.employeePhone)
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextInput(newEmployee.employeeSalary)
        composeRule.onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeeSalaryType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeeType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeePosition).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_JOINED_DATE_FIELD).performClick()
        composeRule.onAllNodesWithTag("dialog_date_selection_$newAbsentDate")[0].assertExists("Date picker is not visible")
        composeRule.onAllNodesWithTag("dialog_date_selection_$newAbsentDate")[0].performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()

        //Check Recently created employee is visible or not
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertExists("New Employee is not visible")
        composeRule.onNodeWithText(newEmployee.employeePhone).assertExists("New Employee phone is not visible")
    }

    private fun checkSearchAndFilterIconIsVisibleOnEmployeeScreen () {
        // Check search icon and filter icon is visible or not
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")
        composeRule.onNodeWithContentDescription("Filter Employee").assertExists("Filter icon is not visible")
    }

    private fun clickOnSearchButtonAndSearchUsingNewKeywordAndCheckOnNewEmployeeIsVisibleOnEmployeeScreen () {
        //Click on search button and perform search using 'new' keyword and only $newEmployee should visible
        composeRule.onNodeWithContentDescription("Search Icon").performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).assertExists("Search bar is not visible")
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("New")
        Thread.sleep(500)
        composeRule.waitForIdle()
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertExists("New Employee is not visible")
        composeRule.onNodeWithText(newEmployee.employeePhone).assertExists("New Employee phone is not visible")
        composeRule.onAllNodesWithTag(updatedEmployeeTag, false)[0].assertDoesNotExist()
        composeRule.onNodeWithText(updatedEmployee.employeePhone).assertDoesNotExist()
    }

    private fun searchUsingUpdatedKeywordAndOnlyUpdatedEmployeeIsVisibleOnEmployeeScreen () {
        //Search using 'updated' keyword and $updatedEmployee should be visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Updated")
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].assertExists("Updated Employee is not visible")
        composeRule.onNodeWithText(updatedEmployee.employeePhone).assertExists("Updated Employee phone is not visible")
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertDoesNotExist()
        composeRule.onNodeWithText(newEmployee.employeePhone).assertDoesNotExist()
    }

    private fun searchUsingRandomStringAndShouldShownSearchedItemNotFoundOnEmployeeScreen() {
        //Search using random text nothing will be visible and searched item not found message will be displayed
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("xyz")
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].assertDoesNotExist()
        composeRule.onNodeWithText(updatedEmployee.employeePhone).assertDoesNotExist()
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertDoesNotExist()
        composeRule.onNodeWithText(newEmployee.employeePhone).assertDoesNotExist()
        composeRule.onNodeWithText("Searched Item Not Found").assertExists("Searched Item Not Found is not visible")
    }

    private fun pressBackAndCloseSearchBarAllItemShouldVisibleOnScreen() {
        //Press back button to close search bar and all employees are visible
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertExists("New Employee is not visible")
        composeRule.onNodeWithText(newEmployee.employeePhone).assertExists("New Employee phone is not visible")
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].assertExists("Updated Employee is not visible")
        composeRule.onNodeWithText(updatedEmployee.employeePhone).assertExists("Updated Employee phone is not visible")
    }

    private fun clickOnFilterButtonAndCheckFilterScreenIsVisible() {
        //Click on filter button and check Employee Filter screen is visible or not
        composeRule.onNodeWithContentDescription("Filter Employee").performClick()
        composeRule.onNodeWithTag("Filter Employee").assertExists("Filter Employee screen is not visible")
        composeRule.onNodeWithTag(Constants.STANDARD_BOTTOM_SHEET_CLOSE_BTN).assertExists("Filter Employee screen close button is not visible")
        composeRule.onNodeWithTag(Constants.STANDARD_BOTTOM_SHEET_CLOSE_BTN).performClick()
    }

    private fun createNewEmployeeThatAlreadyExistsReturnsValidationError() {
        //Add new data with same name return validation error
        composeRule.onNodeWithText("Add New Employee".uppercase()).performClick()
        composeRule.onNodeWithText("Create New Employee").assertExists("Add New Employee screen is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput(newEmployee.employeeName)
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput(newEmployee.employeePhone)
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextInput(newEmployee.employeeSalary)
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(EMPLOYEE_NAME_ERROR).assertTextEquals("Employee name already exists.")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertTextEquals("Phone no already exists")
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
    }

    private fun updateEmployeeDataThatAlreadyExistsReturnValidationError() {
        //Update data with already existing data return validation error
        //Swipe left the employee and click on edit button to edit employee data
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].performTouchInput {
            swipeRight()
        }
        composeRule.onNodeWithTag(newPaymentTag).assertExists("Add Payment Entry button is not visible")
        composeRule.onNodeWithTag(newEditTag).assertExists("Edit Employee button is not visible")

        composeRule.onNodeWithTag(newEditTag).performClick()
        composeRule.onNodeWithText("Update Employee").assertExists("Update Employee screen is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput(updatedEmployee.employeeName)
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextClearance()
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput(updatedEmployee.employeePhone)
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(EMPLOYEE_NAME_ERROR).assertTextEquals("Employee name already exists.")
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertTextEquals("Phone no already exists")
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()

    }

    private fun swipeRightOnNewEmployeeAndClickOnDeleteAndDeleteThemAndCheckItsNotVisibleOnScreen () {
        //Swipe right and check absent entry button and delete button is visible or not
        composeRule.waitForIdle()
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].performTouchInput { swipeLeft() }
        composeRule.onNodeWithTag(newAbsentTag).assertExists("Mark as Absent button is not visible")
        composeRule.onNodeWithTag(newDeleteTag).assertExists("Delete Employee button is not visible")

        //Click on delete employee and delete employee and it should be removed from screen
        composeRule.onNodeWithTag(newDeleteTag).performClick()
        composeRule.onNodeWithText("Delete Employee?").assertExists("Delete Employee dialog is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Employee dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete Employee dialog is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        //Check Recently delete employee should not be visible
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertDoesNotExist()
        composeRule.onNodeWithText(newEmployee.employeePhone).assertDoesNotExist()
    }

    @Test
    fun d_create_new_employee () {
        //Create new employee
        composeRule.onNodeWithText("Add New Employee".uppercase()).performClick()
        composeRule.waitForIdle()
        // Create employee with valid data should navigate back to employee screen and created employee should visible in screen
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput(newEmployee.employeeName)
        composeRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput(newEmployee.employeePhone)
        composeRule.onNodeWithTag(EMPLOYEE_MONTHLY_SALARY_FIELD).performTextInput(newEmployee.employeeSalary)
        composeRule.onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeeSalaryType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_TYPE_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeeType).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_POSITION_FIELD).performClick()
        composeRule.onNodeWithText(newEmployee.employeePosition).performClick()
        composeRule.onNodeWithTag(EMPLOYEE_JOINED_DATE_FIELD).performClick()
        composeRule.onAllNodesWithTag("dialog_date_selection_$joinedDate")[0].assertExists("Date picker is not visible")
        composeRule.onAllNodesWithTag("dialog_date_selection_$joinedDate")[0].performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BUTTON).performClick()

        //Check Recently created employee is visible or not
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Employees").assertExists("Employee Screen is not visible")
        composeRule.onNodeWithText("Add New Employee".uppercase()).assertExists("Add New Employee button is not visible")
        composeRule.onAllNodesWithTag(newEmployeeTag, true)[0].assertExists()
        composeRule.onNodeWithText(newEmployee.employeePhone).assertExists("New Employee phone is not visible")

    }

    //Employee Attendance and Absent Screen
    @Test
    fun e_attendance_screen_test() {
        composeRule.onAllNodesWithTag(updatedEmployeeTag, true)[0].performTouchInput { swipeLeft() }

        //Add absent entry
        composeRule.onNodeWithTag(updatedAbsentTag).performClick()
        composeRule.onNodeWithTag("Add Absent Entry").assertExists("Add Absent Entry Screen is not visible")
        composeRule.onNodeWithTag(EMPLOYEE_NAME_FIELD).assertExists("Employee Name Field is not visible")
        composeRule.onNodeWithTag(ABSENT_DATE_FIELD).assertExists("Absent Date field is not visible")
        composeRule.onNodeWithTag(ABSENT_REASON_FIELD).assertExists("Absent Reason field is not visible")
        composeRule.onNodeWithTag(ADD_EDIT_ABSENT_ENTRY_BTN).assertExists("AddEdit Absent Entry Button is not visible")
        composeRule.onNodeWithTag(ADD_EDIT_ABSENT_ENTRY_BTN).performClick()

        //Navigate to Attendance Screen and check that employee absent report is available
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Goto Attendance Screen").assertExists("Goto Attendance Screen button is not visible")
        composeRule.onNodeWithContentDescription("Goto Attendance Screen").performClick()
        composeRule.onNodeWithText("Absent Reports").assertExists("Absent Reports screen is not visible")

        //Click on employee and check absent data added on today date
        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Employee absent report not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(updatedTodayDateTag).assertExists("Employee absent on today date is not visible")

        //Add New Employee absent on newAbsent date
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).assertExists("Add Absent Entry button is not visible")
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).performClick()
        composeRule.onNodeWithTag(AbsentScreenTestTags.ABSENT_EMPLOYEE_NAME_FIELD).performClick()
        composeRule.onNodeWithTag(newEmployee.employeeName).performClick()
        composeRule.onNodeWithTag(ABSENT_DATE_FIELD).performClick()
        composeRule.onAllNodesWithTag("dialog_date_selection_$newAbsentDate")[0].assertExists("Date picker is not visible")
        composeRule.onAllNodesWithTag("dialog_date_selection_$newAbsentDate")[0].performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        composeRule.onNodeWithTag(ADD_EDIT_ABSENT_ENTRY_BTN).performClick()

        //Click on employee and check absent data added on today date
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("Employee absent report not visible")
        composeRule.onNodeWithTag(newEmployeeTag).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(newAbsentDateTag).assertExists("Employee absent on absent date is not visible")

        //Click on new employee absent date and check all buttons are visible
        composeRule.onNodeWithTag(newAbsentDateTag).performClick()
        composeRule.onNodeWithContentDescription("Edit Absent Reports").assertExists("Edit absent date button is not visible")
        composeRule.onNodeWithContentDescription("Delete Absent Reports").assertExists("Delete absent report button is not visible")
        composeRule.onNodeWithContentDescription("Close Icon").assertExists("Close icon button is not visible")

        //Click on edit button and check Update Absent Entry screen is visible
        composeRule.onNodeWithContentDescription("Edit Absent Reports").performClick()
        composeRule.onNodeWithText("Update Absent Entry").assertExists("Update absent entry screen is not visible")

        //Change absent date only and check its navigate back to absent reports screen and date is visible
        composeRule.onNodeWithTag(ABSENT_DATE_FIELD).performClick()
        composeRule.onAllNodesWithTag("dialog_date_selection_$updatedAbsentDate")[0].assertExists("Date picker is not visible")
        composeRule.onAllNodesWithTag("dialog_date_selection_$updatedAbsentDate")[0].performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        composeRule.onNodeWithTag(ADD_EDIT_ABSENT_ENTRY_BTN).performClick()

        composeRule.waitForIdle()
        Thread.sleep(500)
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("Employee absent report not visible")
        composeRule.onNodeWithTag(newUpdatedAbsentDateTag).assertExists("Employee updated absent date is not visible")

        // Select again and change employee and check its visible on to the changed employee
        composeRule.onNodeWithTag(newUpdatedAbsentDateTag).performClick()
        composeRule.onNodeWithContentDescription("Edit Absent Reports").performClick()
        composeRule.onNodeWithText("Update Absent Entry").assertExists("Update absent entry screen is not visible")
        composeRule.onNodeWithTag(AbsentScreenTestTags.ABSENT_EMPLOYEE_NAME_FIELD).performClick()
        composeRule.onNodeWithTag(updatedEmployee.employeeName).performClick()
        composeRule.onNodeWithTag(ADD_EDIT_ABSENT_ENTRY_BTN).performClick()

        composeRule.waitForIdle()
        Thread.sleep(500)
        composeRule.onNodeWithTag(newEmployeeTag).assertDoesNotExist()
        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Employee absent report not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(updatedAbsentDateTag).assertExists("Employee updated absent date is not visible")


        //Add New Employee absent on newAbsent date
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).assertExists("Add Absent Entry button is not visible")
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).performClick()
        composeRule.onNodeWithTag(AbsentScreenTestTags.ABSENT_EMPLOYEE_NAME_FIELD).performClick()
        composeRule.onNodeWithTag(newEmployee.employeeName).performClick()
        composeRule.onNodeWithTag(ABSENT_DATE_FIELD).performClick()
        composeRule.onAllNodesWithTag("dialog_date_selection_$newAbsentDate")[0].assertExists("Date picker is not visible")
        composeRule.onAllNodesWithTag("dialog_date_selection_$newAbsentDate")[0].performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        composeRule.onNodeWithTag(ADD_EDIT_ABSENT_ENTRY_BTN).performClick()

        //Click on employee and check absent data added on today date
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("Employee absent report not visible")
        composeRule.onNodeWithTag(newEmployeeTag).performClick()
        composeRule.onNodeWithTag(newAbsentDateTag).assertExists("Employee absent on absent date is not visible")

        //Check search button is visible or not
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")

        //Click on search icon and check search bar is visible
        composeRule.onNodeWithContentDescription("Search Icon").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).assertExists("Search bar is not visible")
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).assertExists("Back button is not visible")

        //Search using 'new' keyword and only newEmployee should be visible on screen
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("New")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("New Employee absent report not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).assertDoesNotExist()

        //Clear Search and search using 'updated' keyword and only updatedEmployee should visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Updated")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Updated Employee absent report not visible")
        composeRule.onNodeWithTag(newEmployeeTag).assertDoesNotExist()

        // Updated -> Today(24), updatedDate(16) && New -> newDate(12)

        //Search using 'updatedDate(16)' and click on 'updatedEmployee' and check only updatedDate is visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput(updatedAbsentDate.toString())
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Updated Employee absent report not visible")
//        composeRule.onNodeWithTag(newEmployeeTag).assertDoesNotExist()
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(newTodayDateTag).assertDoesNotExist()
        composeRule.onNodeWithTag(updatedAbsentDateTag).assertExists("Updated Absent Date is not visible")

        //Search using random text and nothing will be visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("xyz")
        composeRule.onNodeWithTag(newEmployeeTag).assertDoesNotExist()
        composeRule.onNodeWithTag(updatedEmployeeTag).assertDoesNotExist()
        composeRule.onNodeWithText("Searched Item Not Found").assertExists("Searched Item Not Found is not visible")

        //Clear search and close search bar and check absent reports and Add Absent Entry button is visible
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
        composeRule.onNodeWithText("Absent Reports").assertExists("Absent Reports screen is not visible")
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).assertExists("Add Absent Entry button is not visible")
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("New Employee absent reports is not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Updated Employee absent reports is not visible")

        // Updated -> Today(24), updatedDate(16) && New -> newDate(12)

        //Click on updatedEmployee and select today date
        // and click on delete button and check delete dialog is visible
        // and cancel deletion
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(updatedAbsentDateTag).assertExists("Updated Employee absent date is not visible")
        composeRule.onNodeWithTag(updatedTodayDateTag).assertExists("Updated today date is not visible")
        composeRule.onNodeWithTag(updatedTodayDateTag).performClick()
        composeRule.onNodeWithContentDescription("Delete Absent Reports").assertExists("Delete absent report button is not visible")
        composeRule.onNodeWithContentDescription("Delete Absent Reports").performClick()

        composeRule.onNodeWithText("Delete Absent Report?").assertExists("Delete Absent Reports dialog is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Absent Reports dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Delete absent report dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).performClick()


        //Click again on updatedEmployee and delete todayAbsentDate and item should be removed from screen
        composeRule.onNodeWithTag(updatedTodayDateTag).performClick()
        composeRule.onNodeWithContentDescription("Delete Absent Reports").performClick()
        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Delete Absent Reports dialog is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(updatedTodayDateTag).assertDoesNotExist()
    }


    @Test
    fun f_salary_screen_test() {

        //Check PaymentDetail button is visible and
        // click on that to check does it navigate to Payment Details screen and navigate back here
        composeRule.onNodeWithContentDescription("Goto Salary Screen").assertExists("Open Salary Screen button is not visible")
        composeRule.onNodeWithContentDescription("Goto Salary Screen").performClick()
        composeRule.onNodeWithText("Payment Details").assertExists("Payment details screen is not visible")

        //Check PaymentDetail button is visible
        composeRule.onNodeWithText("Payment Details").assertExists("Payment details screen is not visible")
        composeRule.onNodeWithText("Add Payment Entry".uppercase()).assertExists("Add Payment Entry button is not visible")

        // Click on Add Payment Entry button and check screen is visible
        composeRule.onNodeWithText("Add Payment Entry".uppercase()).performClick()
        composeRule.onNodeWithText("Add Payment Entry").assertExists("Add Payment Entry screen is not visible")
        composeRule.onNodeWithTag("AddEdit PaymentEntry Screen").performTouchInput {
            swipeUp()
        }
        //Check all fields are visible
        composeRule.onNodeWithTag(SALARY_EMPLOYEE_NAME_FIELD).assertExists("Employee name field is not visible")
        composeRule.onNodeWithTag(SALARY_TYPE_FIELD).assertExists("Salary type field is not visible")
        composeRule.onNodeWithTag(GIVEN_DATE_FIELD).assertExists("Given Date field is not visible")
        composeRule.onNodeWithTag(PAYMENT_TYPE_FIELD).assertExists("Payment type field is not visible")
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).assertExists("Given amount field is not visible")
        composeRule.onNodeWithTag(SALARY_NOTE_FIELD).assertExists("Salary note field is not visible")
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).assertExists("AddEdit Payment Button is not visible")

        //Check empty validation error on submit empty form
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()
        composeRule.onNodeWithTag(SALARY_EMPLOYEE_NAME_ERROR).assertExists("Employee name error is not visible")
        composeRule.onNodeWithTag(SALARY_EMPLOYEE_NAME_ERROR).assertTextEquals("Employee name must not be empty")
        composeRule.onNodeWithTag(GIVEN_AMOUNT_ERROR).assertExists("Given Amount error is not visible")
        composeRule.onNodeWithTag(GIVEN_AMOUNT_ERROR).assertTextEquals("Salary must not be empty")

        //check salary length error on invalid data
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextInput("2")
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_ERROR).assertTextEquals("Salary must greater than two digits")

        //check salary character error on invalid data
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextInput("2e")
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_ERROR).assertTextEquals("Salary must not contain any characters")

        //Check salary note error when choose Both Payment type
        composeRule.onNodeWithTag(PAYMENT_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(PaymentType.Both.paymentType).performClick()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()
        composeRule.onNodeWithTag(SALARY_NOTE_ERROR).assertTextEquals("Salary note required because you paid using Cash and Online.")

        //add new payment entry with valid data
        composeRule.onNodeWithTag(SALARY_EMPLOYEE_NAME_FIELD).performClick()
        composeRule.onNodeWithTag(newEmployee.employeeName).performClick()
        composeRule.onNodeWithTag(SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(newSalary.salaryType).performClick()
        composeRule.onNodeWithTag(PAYMENT_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(newSalary.salaryPaymentType).performClick()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextClearance()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextInput(newSalary.employeeSalary)
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()

        //check recently created employee payments is visible
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("New employee payment is not visible")
        composeRule.onNodeWithTag(newEmployeeTag).performClick()
        composeRule.onNodeWithTag(newAmountTag).assertExists("Give payment to employee is not visible")

        //Click on new employee payment and check all buttons are visible
        composeRule.onNodeWithTag(newAmountTag).performClick()
        composeRule.onNodeWithContentDescription("Edit Salary Item").assertExists("Edit salary button is not visible")
        composeRule.onNodeWithContentDescription("Delete Salary").assertExists("Delete Salary is not visible")
        composeRule.onNodeWithContentDescription("Close Icon").assertExists("Close icon is not visible")

        //Click on edit icon and check does it navigate to Update Payment Entry Screen or not
        composeRule.onNodeWithContentDescription("Edit Salary Item").performClick()
        composeRule.onNodeWithText("Update Salary Entry").assertExists("Update salary screen is not visible")
        composeRule.onNodeWithTag("AddEdit PaymentEntry Screen").performTouchInput {
            swipeUp()
        }

        // update data and check does it navigate back salary screen and data is updated
        composeRule.onNodeWithTag(SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(updatedSalary.salaryType).performClick()
        composeRule.onNodeWithTag(PAYMENT_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(updatedSalary.salaryPaymentType).performClick()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextClearance()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextInput(updatedSalary.employeeSalary)
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()
        composeRule.onNodeWithTag(newUpdatedAmountTag).assertExists("Updated payment to employee is not visible")

        // add new payment to new employee
        composeRule.onNodeWithTag(newEmployeeTag).performClick()
        composeRule.onNodeWithText("Add Payment Entry".uppercase()).performClick()
        composeRule.onNodeWithText("Add Payment Entry").assertExists("Add Payment Entry screen is not visible")
        composeRule.onNodeWithTag("AddEdit PaymentEntry Screen").performTouchInput {
            swipeUp()
        }
        composeRule.onNodeWithTag(SALARY_EMPLOYEE_NAME_FIELD).performClick()
        composeRule.onNodeWithTag(newEmployee.employeeName).performClick()
        composeRule.onNodeWithTag(SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(newSalary.salaryType).performClick()
        composeRule.onNodeWithTag(PAYMENT_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(newSalary.salaryPaymentType).performClick()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextInput(newSalary.employeeSalary)
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()

        composeRule.onNodeWithTag(newAmountTag).assertExists("Give payment to employee is not visible")

        // update employee data and change new employee to updated employee
        composeRule.onNodeWithTag(newAmountTag).performClick()
        composeRule.onNodeWithContentDescription("Edit Salary Item").performClick()
        composeRule.onNodeWithText("Update Salary Entry").assertExists("Update salary screen is not visible")
        composeRule.onNodeWithTag("AddEdit PaymentEntry Screen").performTouchInput {
            swipeUp()
        }

        // update data and check does it navigate back salary screen and data is updated
        composeRule.onNodeWithTag(SALARY_EMPLOYEE_NAME_FIELD).performClick()
        composeRule.onNodeWithTag(updatedEmployee.employeeName).performClick()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()

        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Updated employee payment is not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(updatedNewAmountTag).assertExists("Updated payment to updated employee is not visible")

        // add new payment to new employee
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithText("Add Payment Entry".uppercase()).performClick()
        composeRule.onNodeWithText("Add Payment Entry").assertExists("Add Payment Entry screen is not visible")
        composeRule.onNodeWithTag("AddEdit PaymentEntry Screen").performTouchInput {
            swipeUp()
        }
        composeRule.onNodeWithTag(SALARY_EMPLOYEE_NAME_FIELD).performClick()
        composeRule.onNodeWithTag(newEmployee.employeeName).performClick()
        composeRule.onNodeWithTag(SALARY_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(newSalary.salaryType).performClick()
        composeRule.onNodeWithTag(PAYMENT_TYPE_FIELD).performClick()
        composeRule.onNodeWithTag(newSalary.salaryPaymentType).performClick()
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextInput(newSalary.employeeSalary)
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()
        //Check data is visible
        composeRule.onNodeWithTag(newEmployeeTag).performClick()
        composeRule.onNodeWithTag(newAmountTag).assertExists("Give payment to employee is not visible")

        //check total payments
        composeRule.onNodeWithTag("TotalPayments").assertExists("Total payments is not visible")
        composeRule.onNodeWithTag("TotalPayments").assertTextEquals("3 Payments")
        composeRule.onNodeWithTag("TotalEmployees").assertExists("Total employee is not visible")
        composeRule.onNodeWithTag("TotalEmployees").assertTextEquals("2 Employees")
        composeRule.onNodeWithTag("TotalAmount").assertExists("Total Amount is not visible")
        val totalAmount = newSalary.employeeSalary.toLong().times(2).plus(updatedSalary.employeeSalary.toLong()).toString().toRupee
        composeRule.onNodeWithTag("TotalAmount").assertTextEquals(totalAmount)


        // check search and button is visible
        composeRule.onNodeWithContentDescription("Search Icon").assertExists("Search icon is not visible")

        // perform search using 'new' search and only new employee is visible
        composeRule.onNodeWithContentDescription("Search Icon").performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).assertExists("Standard search button is not visible")
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).assertExists("Back button is not visible")

        //Search using 'new' keyword and only newEmployee should be visible on screen
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("New")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("New Employee absent report not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).assertDoesNotExist()

        //Clear Search and search using 'updated' keyword and only updatedEmployee should visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Updated")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Updated Employee absent report not visible")
        composeRule.onNodeWithTag(newEmployeeTag).assertDoesNotExist()

        // New Employee -> (500 + 800) && Updated Employee -> (800)

        //Search using '500' and check only new employee only visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput(newSalary.employeeSalary)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("New Employee payments is not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(newEmployeeTag).performClick()
        composeRule.onNodeWithTag(newPaymentTag).assertDoesNotExist()

        //Search using random text and nothing will be visible
        composeRule.onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()
        composeRule.onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("xyz")
        composeRule.onNodeWithTag(newEmployeeTag).assertDoesNotExist()
        composeRule.onNodeWithTag(updatedEmployeeTag).assertDoesNotExist()
        composeRule.onNodeWithText("Searched Item Not Found").assertExists("Searched Item Not Found is not visible")

        //Clear search and close search bar and check absent reports and Add Absent Entry button is visible
        composeRule.onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
        composeRule.onNodeWithText("Payment Details").assertExists("Payment details screen is not visible")
        composeRule.onNodeWithText("Add Payment Entry".uppercase()).assertExists("Add Payment Entry button is not visible")
        composeRule.onNodeWithTag(newEmployeeTag).assertExists("New Employee absent reports is not visible")
        composeRule.onNodeWithTag(updatedEmployeeTag).assertExists("Updated Employee absent reports is not visible")

        // select updated employee and select updatedPayment
        composeRule.onNodeWithTag(updatedEmployeeTag).performClick()
        composeRule.onNodeWithTag(updatedNewAmountTag).assertExists("Updated Employee payments is not visible")
        composeRule.onNodeWithTag(updatedNewAmountTag).performClick()
        //click on delete icon and check delete dialog is visible
        composeRule.onNodeWithContentDescription("Delete Salary").assertExists("Delete icon is not visible")
        composeRule.onNodeWithContentDescription("Delete Salary").performClick()

        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Salary delete dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Salary delete dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).performClick()


        //select again and delete updated employee payment and check delete employee is not visible in screen

        composeRule.onNodeWithTag(updatedNewAmountTag).performClick()
        //click on delete icon and check delete dialog is visible
        composeRule.onNodeWithContentDescription("Delete Salary").assertExists("Delete icon is not visible")
        composeRule.onNodeWithContentDescription("Delete Salary").performClick()

        composeRule.onNodeWithTag(POSITIVE_BUTTON).assertExists("Salary delete dialog is not visible")
        composeRule.onNodeWithTag(NEGATIVE_BUTTON).assertExists("Salary delete dialog is not visible")
        composeRule.onNodeWithTag(POSITIVE_BUTTON).performClick()

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(updatedEmployeeTag).assertDoesNotExist()

        //Check total payments and employee
        composeRule.waitForIdle()
        Thread.sleep(500)
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Total Payments").assertExists("Total Payments card is not visible")
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).assertExists("Add absent entry button is not visible")
        composeRule.onNodeWithTag("TotalPayments").assertTextEquals("2 Payments")
        composeRule.onNodeWithTag("TotalEmployees").assertTextEquals("1 Employees")
        val newAmount = newSalary.employeeSalary.toLong().plus(updatedSalary.employeeSalary.toLong()).toString().toRupee
        composeRule.onNodeWithTag("TotalAmount").assertTextEquals(newAmount)

        //click on create absent entry and check absent screen is visible
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).assertExists("Add absent entry button is not visible")
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).performClick()

        composeRule.onNodeWithTag("Add Absent Entry").assertExists("Add Absent Entry Screen is not visible")
        Espresso.pressBack()
    }



    //Employee Detail Screen Test
    @Test
    fun g_employee_details_screen_test () {
        //click on new employee and check does it navigate to employee details screen
        composeRule.onAllNodesWithTag(newEmployeeTag)[0].performClick()
        composeRule.onNodeWithTag(EMPLOYEE_DETAILS_SCREEN).assertExists("Employee Details Screen is not visible")
        composeRule.onNodeWithTag("CalculateSalary").assertExists("Salary Calculation Card is not visible")
        composeRule.onNodeWithTag("EmployeeDetails").assertExists("Employee Details Card is not visible")
        composeRule.onNodeWithTag("PaymentDetails").assertExists("Payment Details Card is not visible")
        composeRule.onNodeWithTag("AbsentDetails").assertExists("Absent Details Card is not visible")
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).assertExists("Add Absent Entry Button is not visible")
        composeRule.onNodeWithText("Add Payment Entry".uppercase()).assertExists("Add Payment entry button is not visible")
        composeRule.onNodeWithContentDescription("Add Payment Entry").assertExists("Add Payment Entry Button is not visible")
        composeRule.onNodeWithContentDescription("Add Absent Entry").assertExists("Add absent entry button is not visible")

        //Check employee salary calculation data
        //up 26, 16
        //nw = 14
        //Remaining amount - Salary(12000), Advance Payment(500 + 800), Leave - 1 -> Remaining (12000 - 500+800+400 = 10300)
        val newRemainingAmount = newEmployee.employeeSalary.toLong()
                .minus(newSalary.employeeSalary.toLong())
                .minus(updatedSalary.employeeSalary.toLong())
                .minus(newEmployee.employeeSalary.toLong().div(30))
                .toString()
                .toRupee

        composeRule.onNodeWithTag(REMAINING_AMOUNT_TEXT).assertExists("Remaining amount text is not visible")
        composeRule.onNodeWithTag(REMAINING_AMOUNT_TEXT).assertTextEquals(newRemainingAmount)
        composeRule.onNodeWithTag("AdvancePayment").assertExists("Advance Payment text is not visible")
        composeRule.onNodeWithTag("AdvancePayment").assertTextEquals("2 Advance Payment")
        composeRule.onNodeWithTag("DaysAbsent").assertExists("Days Absent text is not visible")
        composeRule.onNodeWithTag("DaysAbsent").assertTextEquals("1 Days Absent")

        //check employee advanced payment is visible
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("PaymentDetails").performClick()
        composeRule.onNodeWithTag(newAmountTag, true).assertExists("Employee advanced payment is not visible")
        composeRule.onNodeWithTag(newUpdatedAmountTag, true).assertExists("Employee advanced payment is not visible")

        //check employee absent date is visible
//        composeRule.onNodeWithTag("AbsentDetails").performClick()
//        composeRule.onNodeWithTag("AbsentDetails").performClick()
        composeRule.onNodeWithContentDescription("Expand Absent Details").assertExists("Expand Absent Details button").performClick()
        composeRule.waitForIdle()
        Thread.sleep(5000)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(newAbsentDateTag, true).assertExists("Employee Absent date is not visible")
        composeRule.waitForIdle()
        Thread.sleep(5000)
        composeRule.waitForIdle()
        // add new payment entry to new employee
        composeRule.onNodeWithText("Add Payment Entry".uppercase()).performClick()
        composeRule.onNodeWithText("Add Payment Entry").assertExists("Add Payment Entry screen is not visible")
        composeRule.onNodeWithTag("AddEdit PaymentEntry Screen").performTouchInput {
            swipeUp()
        }
        composeRule.onNodeWithTag(GIVEN_AMOUNT_FIELD).performTextInput(updatedSalary.employeeSalary.plus(100))
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON).performClick()

        //check recently paid amount is visible or not in payment screen
        composeRule.onNodeWithTag("PaymentDetails").performClick()
        composeRule.onNodeWithTag(newAmountTag, true).assertExists("Employee advanced payment is not visible")
        composeRule.onNodeWithTag(newUpdatedAmountTag, true).assertExists("Recently paid amount is not visible")

        //check remaining amount has been changed or not
        val newRemainingAmount1 = newEmployee.employeeSalary.toLong()
                .minus(newSalary.employeeSalary.toLong())
                .minus(updatedSalary.employeeSalary.toLong())
                .minus(updatedSalary.employeeSalary.toLong().plus(100))
                .minus(newEmployee.employeeSalary.toLong().div(30))
                .toString().toRupee

        composeRule.onNodeWithTag(REMAINING_AMOUNT_TEXT).assertTextEquals(newRemainingAmount1)
        composeRule.onNodeWithTag("AdvancePayment").assertTextEquals("3 Advance Payment")
        composeRule.onNodeWithTag("DaysAbsent").assertTextEquals("1 Days Absent")

        //add new absent entry on today date
        composeRule.onNodeWithText("Add Absent Entry".uppercase()).performClick()
        composeRule.onNodeWithTag("Add Absent Entry").assertExists("Add Absent Entry Screen is not visible")
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(ADD_EDIT_ABSENT_ENTRY_BTN).performClick()

        //check today absent date is visible or not is absent details
        composeRule.onNodeWithTag("AbsentDetails").performClick()
        composeRule.onNodeWithTag(newUpdatedAbsentDateTag, true).assertExists("Employee Absent date is not visible")
        composeRule.onNodeWithTag(newTodayDateTag, true).assertExists("Employee today Absent date is not visible")


        //check remaining amount has been changed or not
        val newRemainingAmount2 = newEmployee.employeeSalary.toLong()
            .minus(newSalary.employeeSalary.toLong())
            .minus(updatedSalary.employeeSalary.toLong())
            .minus(updatedSalary.employeeSalary.toLong().plus(100))
            .minus(newEmployee.employeeSalary.toLong().div(30))
            .minus(newEmployee.employeeSalary.toLong().div(30))
            .toString().toRupee

        composeRule.onNodeWithTag(REMAINING_AMOUNT_TEXT).assertTextEquals(newRemainingAmount2)
        composeRule.onNodeWithTag("AdvancePayment").assertTextEquals("3 Advance Payment")
        composeRule.onNodeWithTag("DaysAbsent").assertTextEquals("2 Days Absent")

        // click on add payment entry navigation button and check corresponding screen is visible or not
        composeRule.onNodeWithContentDescription("Add Payment Entry").performClick()
        composeRule.onNodeWithText("Add Payment Entry").assertExists("Add Payment Entry screen is not visible")
        Espresso.pressBack()

        // click on add absent entry navigation button and check corresponding screen is visible or not
        composeRule.onNodeWithContentDescription("Add Absent Entry").performClick()
        composeRule.onNodeWithTag("Add Absent Entry").assertExists("Add Absent Entry Screen is not visible")
        Espresso.pressBack()

    }

}