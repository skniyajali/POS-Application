package com.niyaj.popos.features.reports.data.repository

import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.data.repository.CartRepositoryImpl
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ReportsRepositoryImplTest {
    private lateinit var repository: ReportsRepositoryImpl
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var cartRepository: CartRepository


    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        settingsRepository = SettingsRepositoryImpl(config, dispatcher)

        cartRepository = CartRepositoryImpl(config, settingsRepository, dispatcher)

        repository = ReportsRepositoryImpl(config, cartRepository, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }
}