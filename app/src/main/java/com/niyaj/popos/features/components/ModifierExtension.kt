package com.niyaj.popos.features.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSimple
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.niyaj.popos.features.common.ui.theme.GreenAccent


enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.70f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.pressClickEffect() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val ty by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0f else -20f)

    this
        .graphicsLayer {
            translationY = ty
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.shakeClickEffect() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }

    val tx by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) 0f else -50f,
        animationSpec = repeatable(
            iterations = 2,
            animation = tween(durationMillis = 50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    this
        .graphicsLayer {
            translationX = tx
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}


fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

/**
 * Modify element to add border with appearance specified with a [border] and a [shape], pad the
 * content by the [BorderStroke.width] and clip it.
 *
 *
 * @param border [BorderStroke] class that specifies border appearance, such as size and color
 * @param shape shape of the border
 */
fun Modifier.dashedBorder(border: BorderStroke, shape: Shape = RectangleShape, on: Dp, off: Dp) =
    dashedBorder(width = border.width, brush = border.brush, shape = shape, on, off)

/**
 * Returns a [Modifier] that adds border with appearance specified with [width], [color] and a
 * [shape], pads the content by the [width] and clips it.
 *
 *
 * @param width width of the border. Use [Dp.Hairline] for a hairline border.
 * @param color color to paint the border with
 * @param shape shape of the border
 * @param on the size of the solid part of the dashes
 * @param off the size of the space between dashes
 */
fun Modifier.dashedBorder(width: Dp, color: Color, shape: Shape = RectangleShape, on: Dp, off: Dp) =
    dashedBorder(width, SolidColor(color), shape, on, off)

/**
 * Returns a [Modifier] that adds border with appearance specified with [width], [brush] and a
 * [shape], pads the content by the [width] and clips it.
 *
 *
 * @param width width of the border. Use [Dp.Hairline] for a hairline border.
 * @param brush brush to paint the border with
 * @param shape shape of the border
 */
fun Modifier.dashedBorder(width: Dp, brush: Brush, shape: Shape, on: Dp, off: Dp): Modifier =
    composed(
        factory = {
            this.then(
                Modifier.drawWithCache {
                    val outline: Outline = shape.createOutline(size, layoutDirection, this)
                    val borderSize = if (width == Dp.Hairline) 1f else width.toPx()

                    var insetOutline: Outline? = null // outline used for round-rect/generic shapes
                    var stroke: Stroke? = null // stroke to draw border for all outline types
                    var pathClip: Path? = null // path to clip round-rect/generic shapes
                    var inset = 0f // inset to translate before drawing the inset outline
                    // path to draw generic shapes or round-rect with different corner radii
                    var insetPath: Path? = null
                    if (borderSize > 0 && size.minDimension > 0f) {
                        if (outline is Outline.Rectangle) {
                            stroke = Stroke(
                                borderSize, pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(on.toPx(), off.toPx())
                                )
                            )
                        } else {
                            // Multiplier to apply to the border size to get a stroke width that is
                            // large enough to cover the corners while not being too large to overly
                            // square off the internal shape. The resultant shape will be
                            // clipped to the desired shape. Any value lower will show artifacts in
                            // the corners of shapes. A value too large will always square off
                            // the internal shape corners. For example, for a rounded rect border
                            // a large multiplier will always have squared off edges within the
                            // inner section of the stroke, however, having a smaller multiplier
                            // will still keep the rounded effect for the inner section of the
                            // border
                            val strokeWidth = 1.2f * borderSize
                            inset = borderSize - strokeWidth / 2
                            val insetSize = Size(
                                size.width - inset * 2,
                                size.height - inset * 2
                            )
                            insetOutline = shape.createOutline(insetSize, layoutDirection, this)
                            stroke = Stroke(
                                strokeWidth, pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(on.toPx(), off.toPx())
                                )
                            )
                            pathClip = when (outline) {
                                is Outline.Rounded -> {
                                    Path().apply { addRoundRect(outline.roundRect) }
                                }

                                is Outline.Generic -> {
                                    outline.path
                                }

                                else -> {
                                    // should not get here because we check for Outline.Rectangle
                                    // above
                                    null
                                }
                            }

                            insetPath =
                                if (insetOutline is Outline.Rounded &&
                                    !insetOutline.roundRect.isSimple
                                ) {
                                    // Rounded rect with non equal corner radii needs a path
                                    // to be pre-translated
                                    Path().apply {
                                        addRoundRect(insetOutline.roundRect)
                                        translate(Offset(inset, inset))
                                    }
                                } else if (insetOutline is Outline.Generic) {
                                    // Generic paths must be created and pre-translated
                                    Path().apply {
                                        addPath(insetOutline.path, Offset(inset, inset))
                                    }
                                } else {
                                    // Drawing a round rect with equal corner radii without
                                    // usage of a path
                                    null
                                }
                        }
                    }

                    onDrawWithContent {
                        drawContent()
                        // Only draw the border if a have a valid stroke parameter. If we have
                        // an invalid border size we will just draw the content
                        if (stroke != null) {
                            if (insetOutline != null && pathClip != null) {
                                val isSimpleRoundRect = insetOutline is Outline.Rounded &&
                                        insetOutline.roundRect.isSimple
                                withTransform({
                                    clipPath(pathClip)
                                    // we are drawing the round rect not as a path so we must
                                    // translate ourselves other
                                    if (isSimpleRoundRect) {
                                        translate(inset, inset)
                                    }
                                }) {
                                    if (isSimpleRoundRect) {
                                        // If we don't have an insetPath then we are drawing
                                        // a simple round rect with the corner radii all identical
                                        val roundRect = (insetOutline as Outline.Rounded).roundRect
                                        drawRoundRect(
                                            brush = brush,
                                            topLeft = Offset(roundRect.left, roundRect.top),
                                            size = Size(roundRect.width, roundRect.height),
                                            cornerRadius = roundRect.topLeftCornerRadius,
                                            style = stroke
                                        )
                                    } else if (insetPath != null) {
                                        drawPath(insetPath, brush, style = stroke)
                                    }
                                }
                                // Clip rect to ensure the stroke does not extend the bounds
                                // of the composable.
                                clipRect {
                                    // Draw a hairline stroke to cover up non-anti-aliased pixels
                                    // generated from the clip
                                    if (isSimpleRoundRect) {
                                        val simpleRoundRect = (outline as Outline.Rounded).roundRect
                                        drawRoundRect(
                                            brush = brush,
                                            topLeft = Offset(simpleRoundRect.left, simpleRoundRect.top),
                                            size = Size(simpleRoundRect.width, simpleRoundRect.height),
                                            cornerRadius = simpleRoundRect.topLeftCornerRadius,
                                            style = Stroke(
                                                Stroke.HairlineWidth,
                                                pathEffect = PathEffect.dashPathEffect(
                                                    floatArrayOf(on.toPx(), off.toPx())
                                                )
                                            )
                                        )
                                    } else {
                                        drawPath(
                                            pathClip, brush = brush, style = Stroke(
                                                Stroke.HairlineWidth,
                                                pathEffect = PathEffect.dashPathEffect(
                                                    floatArrayOf(on.toPx(), off.toPx())
                                                )
                                            )
                                        )
                                    }
                                }
                            } else {
                                // Rectangular border fast path
                                val strokeWidth = stroke.width
                                val halfStrokeWidth = strokeWidth / 2
                                drawRect(
                                    brush = brush,
                                    topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
                                    size = Size(
                                        size.width - strokeWidth,
                                        size.height - strokeWidth
                                    ),
                                    style = stroke
                                )
                            }
                        }
                    }
                }
            )
        },
        inspectorInfo = debugInspectorInfo {
            name = "border"
            properties["width"] = width
            if (brush is SolidColor) {
                properties["color"] = brush.value
                value = brush.value
            } else {
                properties["brush"] = brush
            }
            properties["shape"] = shape
        }
    )


/**
 * A custom indicator which displays a glow and progress indicator
 */
@Composable
fun GlowIndicator(
    swipeRefreshState: SwipeRefreshState,
    refreshTriggerDistance: Dp,
    progressBarColor: Color = GreenAccent,
    shapeColor : Color = MaterialTheme.colors.secondary,
) {
    Box(
        Modifier
            .drawWithCache {
                onDrawBehind {
                    val distance = refreshTriggerDistance.toPx()
                    val progress = (swipeRefreshState.indicatorOffset / distance).coerceIn(0f, 1f)
                    // We draw a translucent glow
                    val brush = Brush.verticalGradient(
                        0f to shapeColor.copy(alpha = 0.45f),
                        1f to shapeColor.copy(alpha = 0f)
                    )
                    // And fade the glow in/out based on the swipe progress
                    drawRect(brush = brush, alpha = FastOutSlowInEasing.transform(progress))
                }
            }
            .fillMaxWidth()
            .height(72.dp)
    ) {
        if (swipeRefreshState.isRefreshing) {
            // If we're refreshing, show an indeterminate progress indicator
            LinearProgressIndicator(Modifier.fillMaxWidth(), progressBarColor)
        } else {
            // Otherwise we display a determinate progress indicator with the current swipe progress
            val trigger = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
            val progress = (swipeRefreshState.indicatorOffset / trigger).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = if(progress in 0.0 .. 0.5) {
                    progressBarColor
                }else {
                    shapeColor
                }
            )
        }
    }
}