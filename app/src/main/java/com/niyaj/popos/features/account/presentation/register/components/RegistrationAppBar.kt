package com.niyaj.popos.features.account.presentation.register.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.stronglyDeemphasizedAlpha
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedButton


@Composable
private fun TopAppBarTitle(
    questionIndex : Int,
    totalQuestionsCount : Int,
    modifier : Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = (questionIndex + 1).toString(),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onPrimary
        )
        Text(
            text = stringResource(R.string.question_count, totalQuestionsCount),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onPrimary.copy(alpha = stronglyDeemphasizedAlpha)
        )
    }
}


@Composable
fun RegisterTopAppBar(
    questionIndex : Int,
    totalQuestionsCount : Int,
    onClosePressed : () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        TopAppBar(
            title = {
                TopAppBarTitle(
                    questionIndex = questionIndex,
                    totalQuestionsCount = totalQuestionsCount,
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close_icon),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        )

        val animatedProgress by animateFloatAsState(
            targetValue = (questionIndex + 1) / totalQuestionsCount.toFloat(),
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
        )
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colors.secondaryVariant,
        )
    }
}

@Composable
fun RegisterBottomBar(
    shouldShowPreviousButton : Boolean,
    shouldShowDoneButton : Boolean,
    isNextButtonEnabled : Boolean,
    onPreviousPressed : () -> Unit,
    onNextPressed : () -> Unit,
    onDonePressed : () -> Unit
) {
    AnimatedVisibility(
        visible = isNextButtonEnabled || shouldShowPreviousButton,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { fullHeight ->
                fullHeight / 4
            }
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { fullHeight ->
                fullHeight / 4
            }
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            elevation = 7.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                if (shouldShowPreviousButton) {
                    StandardOutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        text = stringResource(id = R.string.previous),
                        icon = Icons.AutoMirrored.Filled.NavigateBefore,
                        onClick = onPreviousPressed
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                if (shouldShowDoneButton) {
                    StandardButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        text = stringResource(id = R.string.submit),
                        icon = Icons.Default.Done,
                        onClick = onDonePressed,
                        enabled = isNextButtonEnabled,
                    )
                } else {
                    StandardButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        text = stringResource(id = R.string.next),
                        icon = Icons.AutoMirrored.Filled.NavigateNext,
                        onClick = onNextPressed,
                        enabled = isNextButtonEnabled,
                    )
                }
            }
        }
    }
}