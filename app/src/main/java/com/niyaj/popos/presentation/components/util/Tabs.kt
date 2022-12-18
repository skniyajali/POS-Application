package com.niyaj.popos.presentation.components.util

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.niyaj.popos.presentation.cart.CartTabItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<CartTabItem>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()

    // OR ScrollableTabRow()
    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions: List<TabPosition> ->
            FancyAnimatedIndicator(tabPositions, pagerState)
        }
    ) {
        // Add tabs for all of our pages
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = { Icon(imageVector = tab.icon, contentDescription = "") },
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FancyAnimatedIndicator(
    tabPositions: List<TabPosition>,
    pagerState: PagerState
) {
    val colors = listOf(Color.Yellow, Color.Red, Color.Green)

    val transition = updateTransition(pagerState.currentPage, label = "")

    val indicatorStart by transition.animateDp(
        transitionSpec = {
            // Handle directionality here, if we are moving to the right, we
            // want the right side of the indicator to move faster, if we are
            // moving to the left, we want the left side to move faster.
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        }, label = ""
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            // Handle directionality here, if we are moving to the right, we
            // want the right side of the indicator to move faster, if we are
            // moving to the left, we want the left side to move faster.
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        }, label = ""
    ) {
        tabPositions[it].right
    }

    val indicatorColor by transition.animateColor(label = "") {
        colors[it % colors.size]
    }

    Box(
        Modifier
            .drawBehind {
                drawLine(
                    color = indicatorColor,
                    start = Offset(
                        size.width + indicatorStart.value,
                        size.height
                    ),
                    end = Offset(
                        indicatorEnd.value - size.width,
                        size.height
                    ),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsContent(tabs: List<CartTabItem>, pagerState: PagerState) {
    HorizontalPager(
        state = pagerState,
        count = tabs.size,
    ) { page ->
        tabs[page].screen()
    }
}