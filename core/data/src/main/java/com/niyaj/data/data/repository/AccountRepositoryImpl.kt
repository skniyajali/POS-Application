package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.AccountRepository
import com.niyaj.database.model.AccountEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Account
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher,
) : AccountRepository {

    val realm = Realm.open(config)

    override fun getAccountInfo(resId: String): Flow<Account> {
        return channelFlow {
            val info = realm.query<AccountEntity>("restaurantId == $0", resId).first().asFlow()

            info.collectLatest {
                when (it) {
                    is InitialObject -> {
                        send(it.obj.toExternalModel())
                    }

                    is UpdatedObject -> {
                        send(it.obj.toExternalModel())
                    }

                    else -> {}
                }
            }
        }
    }

    override suspend fun register(account: Account): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val findAccount =
                        this.query<AccountEntity>("restaurantId == $0", account.restaurantId)
                            .first()
                            .find()

                    if (findAccount != null) {
                        findAccount.email = account.email
                        findAccount.password = account.password
                        findAccount.phone = account.phone
                        findAccount.isLoggedIn = account.isLoggedIn
                        findAccount.updatedAt = System.currentTimeMillis().toString()
                    } else {
                        val newAccount = AccountEntity()
                        newAccount.restaurantId = account.restaurantId
                        newAccount.email = account.email
                        newAccount.password = account.password
                        newAccount.phone = account.phone
                        newAccount.isLoggedIn = account.isLoggedIn
                        newAccount.createdAt = System.currentTimeMillis().toString()

                        this.copyToRealm(newAccount)
                    }
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create an account")
        }
    }

    override suspend fun login(emailOrPhone: String, password: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val findByEmail = realm.query<AccountEntity>(
                    "email == $0 || phone == $1",
                    emailOrPhone,
                    emailOrPhone
                ).first().find()

                if (findByEmail != null) {
                    if (findByEmail.password != password) {
                        Resource.Error("Password does not match")
                    } else {
                        realm.write {
                            findLatest(findByEmail)?.apply {
                                this.isLoggedIn = true
                            }
                        }

                        Resource.Success(true)
                    }
                } else {
                    Resource.Error("Could not find any account using this email or phone")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Unable to login your account!")
        }
    }

    override suspend fun logOut(resId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val findAccount =
                    realm.query<AccountEntity>("restaurantId == $0", resId).first().find()

                if (findAccount != null) {
                    realm.write {
                        findLatest(findAccount)?.apply {
                            isLoggedIn = false
                            updatedAt = System.currentTimeMillis().toString()
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Could not find any account")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Could not log out")
        }
    }

    override suspend fun changePassword(
        resId: String,
        currentPassword: String,
        newPassword: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val account = realm.query<AccountEntity>(
                    "restaurantId == $0 AND password == $1",
                    resId,
                    currentPassword
                ).first().find()

                if (account != null) {
                    realm.write {
                        findLatest(account)?.apply {
                            password = newPassword
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Current password is invalid")
                }
            }

        } catch (e: Exception) {
            Resource.Error("Could not change password")
        }
    }

    override fun checkIsLoggedIn(resId: String): Boolean {
        return try {
            realm.query<AccountEntity>("restaurantId == $0 AND isLoggedIn == $1", resId, true)
                .first()
                .find() != null
        } catch (e: Exception) {
            false
        }
    }
}