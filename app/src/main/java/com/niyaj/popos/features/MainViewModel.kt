package com.niyaj.popos.features

import androidx.lifecycle.ViewModel
import com.niyaj.popos.features.account.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountRepository : AccountRepository
): ViewModel(){

    val isLoggedIn = accountRepository.checkIsLoggedIn()
}