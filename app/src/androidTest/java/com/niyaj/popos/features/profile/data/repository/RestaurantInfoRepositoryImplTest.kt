package com.niyaj.popos.features.profile.data.repository

import com.niyaj.popos.di.TestConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RestaurantInfoRepositoryImplTest {

    private lateinit var repository: RestaurantInfoRepositoryImpl

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = RestaurantInfoRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }
}