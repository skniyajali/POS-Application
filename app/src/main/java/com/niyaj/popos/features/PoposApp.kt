package com.niyaj.popos.features

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.features.common.util.PoposNavigation
import com.niyaj.popos.features.destinations.LoginScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import io.sentry.compose.withSentryObservableEffect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun PoposApp(
    workManager: WorkManager,
    dataDeletionId: UUID,
    generateReportId: UUID,
    absentReminderId: UUID,
    dailySalaryReminderId: UUID,
    isLoggedIn: Boolean,
) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberAnimatedNavController()
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

    LaunchedEffect(key1 = true) {
        workManager.getWorkInfoByIdFlow(dataDeletionId).collectLatest { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.FAILED -> {}
                WorkInfo.State.RUNNING -> {}
                else -> {}
            }
        }
    }

    LaunchedEffect(key1 = true) {
        workManager.getWorkInfoByIdFlow(generateReportId).collectLatest { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.FAILED -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            "Unable to generate report"
                        )
                    }
                }

                WorkInfo.State.RUNNING -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            "Report Generate Running"
                        )
                    }
                }

                else -> {}
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