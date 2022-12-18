package com.niyaj.popos.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {

    private val _emailText = mutableStateOf("")
    val emailText: State<String> = _emailText

    private val _passwordText = mutableStateOf("")
    val passwordText: State<String> = _passwordText

    private val _passwordToggle = mutableStateOf(false)
    val passwordToggle: State<Boolean> = _passwordToggle

    private val _emailError = mutableStateOf("")
    val emailError: State<String> = _emailError


    private val _passwordError = mutableStateOf("")
    val passwordError: State<String> = _passwordError


    fun setEmail(email: String){
        _emailText.value = email
    }

    fun setPassword(password: String){
        _passwordText.value = password
    }

    fun togglePassword(status: Boolean){
        _passwordToggle.value = status
    }

}