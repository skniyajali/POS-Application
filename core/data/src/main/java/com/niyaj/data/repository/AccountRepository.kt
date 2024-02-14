package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Account
import com.niyaj.model.RESTAURANT_ID
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    fun getAccountInfo(resId : String = RESTAURANT_ID) : Flow<Account>

    suspend fun register(account : Account) : Resource<Boolean>

    suspend fun login(emailOrPhone : String, password : String) : Resource<Boolean>

    suspend fun logOut(resId : String = RESTAURANT_ID) : Resource<Boolean>

    suspend fun changePassword(
        resId : String = RESTAURANT_ID,
        currentPassword : String,
        newPassword : String
    ) : Resource<Boolean>

    fun checkIsLoggedIn(resId : String = RESTAURANT_ID) : Boolean
}