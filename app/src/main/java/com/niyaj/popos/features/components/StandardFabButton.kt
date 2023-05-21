package com.niyaj.popos.features.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StandardFabButton(
    modifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector = Icons.Rounded.Add,
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
            Spacer(modifier = Modifier.height(SpaceMini))
        }
    }
}