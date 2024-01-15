package com.niyaj.popos.features

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.features.common.util.PoposNavigation
import com.niyaj.popos.features.destinations.LoginScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import io.sentry.compose.withSentryObservableEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PoposApp(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
        .withSentryObservableEffect(
            enableNavigationBreadcrumbs = true,
            enableNavigationTracing = true
        )
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val bottomSheetNavigator = remember { BottomSheetNavigator(sheetState) }
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    val isLoggedIn = mainViewModel.isLoggedIn
    val deleteState = mainViewModel.deleteState.collectAsStateWithLifecycle().value
    val reportState = mainViewModel.reportState.collectAsStateWithLifecycle().value
    val dailySalaryState = mainViewModel.salaryReminderState.collectAsStateWithLifecycle().value
    val attendanceState = mainViewModel.attendanceState.collectAsStateWithLifecycle().value


    LaunchedEffect(key1 = deleteState) {
        if (deleteState) {
            scaffoldState.snackbarHostState.showSnackbar("Data Deletion Running")
        }
    }

    LaunchedEffect(key1 = reportState) {
        if (reportState) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Report Generate Running")
            }
        }
    }

    LaunchedEffect(key1 = dailySalaryState) {
        if (dailySalaryState) {
            scaffoldState.snackbarHostState.showSnackbar("Daily Salary Reminder Running")
        }
    }

    LaunchedEffect(key1 = attendanceState) {
        if (attendanceState) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Attendance Reminder Running")
            }
        }
    }

    systemUiController.setStatusBarColor(
        color = MaterialTheme.colors.primary,
        darkIcons = false
    )

    val destination = if (isLoggedIn) MainFeedScreenDestination else LoginScreenDestination

    PoposNavigation(
        scaffoldState = scaffoldState,
        navController = navController,
        bottomSheetNavigator = bottomSheetNavigator,
        startRoute = destination,
    )
}