package com.niyaj.popos.features.addon_item.domain.use_cases.validation

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.features.addon_item.data.repository.FakeAddOnItemRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidateItemPriceTest {

    private lateinit var fakeAddOnItemRepository: FakeAddOnItemRepository
    private lateinit var validateItemPrice: ValidateItemPrice

    @Before
    fun setUp() {
        fakeAddOnItemRepository = FakeAddOnItemRepository()
        validateItemPrice = ValidateItemPrice(fakeAddOnItemRepository)
    }

    @Test
    fun `item price is 0, return error`() {
        val result = validateItemPrice(0)

        assertThat(result.successful).isFalse()
    }

    @Test
    fun `item price is less than 5, return error`() {
        val result = validateItemPrice(4)

        assertThat(result.successful).isFalse()
    }

}