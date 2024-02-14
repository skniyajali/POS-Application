package com.niyaj.common.utils

import java.util.concurrent.TimeUnit

object Constants {

    const val UPDATE_MANAGER_REQUEST_CODE = 123
    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 888
    const val NETWORK_PERMISSION_REQUEST_CODE = 777

    // github login token - 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

    const val SPLASH_SCREEN_DURATION = 100

    const val PRINTER_ID = "PRINTER11"

    const val PRINTER_DPI = 176

    const val PRINTER_WIDTH_MM = 58f

    const val PRINTER_NBR_LINE = 31

    const val PRODUCT_NAME_LENGTH = 18

    const val PRINT_PRODUCT_WISE_REPORT_LIMIT = 30

    const val PRINT_ADDRESS_WISE_REPORT_LIMIT = 15

    const val PRINT_CUSTOMER_WISE_REPORT_LIMIT = 15

    const val JSON_FILE_TYPE = "application/json"

    const val JSON_FILE_EXTENSION = ".json"

    const val BACKUP_REALM_NAME = "backup.realm"

    const val SAVEABLE_FILE_NAME = "popos"

    const val PAID = "Paid"

    const val NOT_PAID = "Not Paid"

    const val DELETE_DATA_NOTIFICATION_CHANNEL_ID = "delete_data"
    const val DELETE_DATA_NOTIFICATION_CHANNEL_NAME = "Data Deletion"
    const val DELETE_DATA_INTERVAL_HOUR: Long = 15

    const val GENERATE_REPORT_CHANNEL_ID = "generate_report"
    const val GENERATE_REPORT_CHANNEL_NAME = "Generating Report"
    const val GENERATE_REPORT_INTERVAL_HOUR: Long = 1

    const val SELECTED_CART_ORDER_ID = "33333333"
    const val SETTINGS_ID = "11111111"

    //Dialog positive button test tag
    const val POSITIVE_BUTTON = "positive"
    const val NEGATIVE_BUTTON = "negative"

    const val STANDARD_SEARCH_BAR = "Standard Search Bar"
    const val STANDARD_BACK_BUTTON = "Standard Back Button"
    const val SEARCH_BAR_CLEAR_BUTTON = "SearchBar Close Button"

    const val TEXT_FIELD_LEADING_ICON = "TextFieldLeadingIcon"
    const val TEXT_FIELD_TRAILING_ICON = "TextFieldLeadingIcon"

    const val SEARCH_ITEM_NOT_FOUND = "Searched Item Not Found."
    const val SEARCH_ITEM_PLACEHOLDER = "Search for items..."

    const val FAB_TEXT = "Create New"
    const val LOADING_INDICATION = "loadingIndicator"
    const val SEARCH_ICON = "SearchIcon"
    const val SEARCH_PLACEHOLDER = "SearchPlaceHolder"
    const val SETTINGS_ICON = "SettingsIcon"
    const val EDIT_ICON = "EditIcon"
    const val DELETE_ICON = "DeleteIcon"
    const val CLEAR_ICON = "ClearIcon"
    const val DRAWER_ICON = "DrawerIcon"
    const val SELECT_ALL_ICON = "SelectAllIcon"
    const val PASSWORD_HIDDEN_ICON = "Password Hidden"
    const val PASSWORD_SHOWN_ICON = "Password Shown"

    const val STANDARD_BOTTOM_SHEET = "BottomSheet"
    const val STANDARD_BOTTOM_SHEET_CLOSE_BTN = "Standard Bottom Close Button"

    const val SORT_ASCENDING = "Sort Ascending"
    const val SORT_DESCENDING = "Sort Descending"

    const val ABSENT_REMINDER_NOTE = "Selected employees will be mark as absent."
    const val DAILY_SALARY_REMINDER_NOTE = "Selected employees will be mark as paid."

    const val ABSENT_REMINDER_ID = "EAR889977"
    const val ABSENT_REMINDER_NAME = "Employee Attendance"
    const val ABSENT_REMINDER_TITLE = "Did You Marked Employee Attendance?"
    const val ABSENT_REMINDER_TEXT = "Don't forget to mark absent employees."
    const val ABSENT_REMINDER_REQ_CODE = 9977
    const val ABSENT_REMINDER_INTERVAL = 2
    val ABSENT_REMINDER_TIME_UNIT: TimeUnit = TimeUnit.HOURS

    const val DAILY_SALARY_REMINDER_ID = "DSR907856"
    const val DAILY_SALARY_REMINDER_NAME = "Daily Salary Reminder"
    const val DAILY_SALARY_REMINDER_TITLE = "Did You Paid Employee Salary?"
    const val DAILY_SALARY_REMINDER_TEXT = "Don't forget to add salary entries."
    const val DAILY_SALARY_REQ_CODE = 9078
    const val DAILY_SALARY_REMINDER_INTERVAL = 2
    val DAILY_SALARY_REMINDER_TIME_UNIT: TimeUnit = TimeUnit.HOURS


    private const val HOST = "http://skniyajali.me/"
    private const val HOST_SECURE = "https://skniyajali.me/"

    const val SALARY_HOST = "${HOST}reminder/reminder_id=$DAILY_SALARY_REMINDER_ID"
    const val SALARY_HOST_SECURE = "${HOST_SECURE}reminder/reminder_id=$DAILY_SALARY_REMINDER_ID"

    const val ABSENT_HOST = "${HOST}reminder/reminder_id=$ABSENT_REMINDER_ID"
    const val ABSENT_HOST_SECURE = "${HOST_SECURE}reminder/reminder_id=$ABSENT_REMINDER_ID"

    enum class ImportExportType {
        IMPORT,
        EXPORT
    }
}