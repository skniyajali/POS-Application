package com.niyaj.popos.features.account.domain.repository

import com.niyaj.popos.common.utils.Constants.RESTAURANT_ID
import com.niyaj.popos.features.account.domain.model.Account
import com.niyaj.popos.features.common.util.Resource
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