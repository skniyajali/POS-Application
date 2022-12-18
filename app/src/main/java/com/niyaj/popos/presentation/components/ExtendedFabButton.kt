package com.niyaj.popos.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExtendedFabButton(
    modifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector = Icons.Default.Add,
    visible: Boolean = true,
    showScrollToTop: Boolean = false,
    onScrollToTopClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val density = LocalDensity.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ScrollToTop(onScrollToTopClick)
        }
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically {
                with(density) { 40.dp.roundToPx() }
            } + fadeIn(),
            exit = fadeOut(
                animationSpec = keyframes {
                    this.durationMillis = 120
                }
            ),
        ) {
            Spacer(modifier = Modifier.height(SpaceSmall))
            Card(
                onClick = {
                    onClick()
                },
                modifier = modifier
                    .widthIn(min = 64.dp)
                    .height(64.dp)
                    .padding(SpaceSmall)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutLinearInEasing
                        )
                    ),
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 8.dp,
                shape = CutCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(SpaceSmall)
                        .wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text
                    )

                    if (!showScrollToTop) {
                        Spacer(modifier = Modifier.width(SpaceSmall))
                        Text(
                            text = text,
                            style = MaterialTheme.typography.button,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}