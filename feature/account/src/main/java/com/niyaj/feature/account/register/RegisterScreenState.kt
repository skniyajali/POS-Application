package com.niyaj.feature.account.register

import com.niyaj.feature.account.register.utils.RegisterScreenPage

data class RegisterScreenState(
    val pageIndex : Int,
    val pageCount : Int,
    val shouldShowPreviousButton : Boolean,
    val shouldShowDoneButton : Boolean,
    val screenPage : RegisterScreenPage,
)
