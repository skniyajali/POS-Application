package com.niyaj.popos.features.addon_item.domain.use_cases.validation

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.features.addon_item.data.repository.FakeAddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.use_cases.CreateNewAddOnItem
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@HiltAndroidTest
@RunWith(JUnit4::class)
class ValidateItemNameTest {

    private lateinit var fakeAddOnItemRepository: FakeAddOnItemRepository

    private lateinit var createNewAddOnItem: CreateNewAddOnItem
    private lateinit var validateItemName: ValidateItemName


    private val newAddOnItem = AddOnItem(
        addOnItemId = "1",
        itemName = "test",
        itemPrice = 10,
        createdAt = System.currentTimeMillis().toString()
    )


    @Before
    fun setUp() {

        fakeAddOnItemRepository = FakeAddOnItemRepository()
        validateItemName = ValidateItemName(fakeAddOnItemRepository)
        createNewAddOnItem = CreateNewAddOnItem(fakeAddOnItemRepository, fakeAddOnItemRepository)
    }


    @Test
    fun `item name is empty, return error`() {
        val result = validateItemName("")

        assertThat(result.successful).isFalse()
    }

    @Test
    fun `item name contains digits, return error`() {
        val result = validateItemName("test123")

        assertThat(result.successful).isFalse()
    }

    @Test
    fun `item name already exists, return error`() {
        //Create a new item
        runBlocking {
            val result = createNewAddOnItem.invoke(newAddOnItem)

            assertThat(result.data).isTrue()
        }

        //Check for duplicate items
        val result = validateItemName(newAddOnItem.itemName)

        assertThat(result.successful).isFalse()
    }
}