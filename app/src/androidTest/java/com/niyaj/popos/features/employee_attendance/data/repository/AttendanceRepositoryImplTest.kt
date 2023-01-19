package com.niyaj.popos.features.employee_attendance.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.niyaj.popos.di.TestConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class AttendanceRepositoryImplTest {

    private lateinit var repository: AttendanceRepositoryImpl

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = AttendanceRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }


    
}