package com.niyaj.popos.features.account.presentation.register.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.popos.features.account.presentation.register.RegisterScreenState

@Composable
fun RegistrationScaffold(
    scaffoldState : ScaffoldState,
    screenData : RegisterScreenState,
    isNextEnabled : Boolean,
    onClosePressed : () -> Unit,
    onPreviousPressed : () -> Unit,
    onNextPressed : () -> Unit,
    onDonePressed : () -> Unit = {},
    content : @Composable (PaddingValues) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                RegisterTopAppBar(
                    questionIndex = screenData.pageIndex,
                    totalQuestionsCount = screenData.pageCount,
                    onClosePressed = onClosePressed,
                )
            },
            content = content,
            bottomBar = {
                RegisterBottomBar(
                    shouldShowPreviousButton = screenData.shouldShowPreviousButton,
                    shouldShowDoneButton = screenData.shouldShowDoneButton,
                    isNextButtonEnabled = isNextEnabled,
                    onPreviousPressed = onPreviousPressed,
                    onNextPressed = onNextPressed,
                    onDonePressed = onDonePressed
                )
            }
        )
    }
}