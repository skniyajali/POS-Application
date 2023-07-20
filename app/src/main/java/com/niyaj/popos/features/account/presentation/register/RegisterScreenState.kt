package com.niyaj.popos.features.account.presentation.register

import com.niyaj.popos.features.account.presentation.register.utils.RegisterScreenPage

data class RegisterScreenState(
    val pageIndex : Int,
    val pageCount : Int,
    val shouldShowPreviousButton : Boolean,
    val shouldShowDoneButton : Boolean,
    val screenPage : RegisterScreenPage,
)
