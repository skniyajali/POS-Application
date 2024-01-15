package com.niyaj.popos.features.data_deletion.data.repository

import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters


@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DataDeletionRepositoryImplTest {

    private lateinit var repository: DataDeletionRepositoryImpl
    private lateinit var settingsRepository: SettingsRepository


    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)
        settingsRepository = SettingsRepositoryImpl(config, dispatcher)


        repository = DataDeletionRepositoryImpl(config, settingsRepository, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }
}