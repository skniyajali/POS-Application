package com.niyaj.popos.features.main_feed.presentation

sealed class MainFeedEvent {
    object RefreshMainFeed: MainFeedEvent()
}
