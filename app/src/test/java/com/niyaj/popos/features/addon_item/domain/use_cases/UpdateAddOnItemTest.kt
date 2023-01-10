package com.niyaj.popos.features.addon_item.domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.features.addon_item.data.repository.FakeAddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UpdateAddOnItemTest {

    private lateinit var fakeAddOnItemRepository: FakeAddOnItemRepository
    private lateinit var createNewAddOnItem: CreateNewAddOnItem
    private lateinit var updateAddOnItem: UpdateAddOnItem

    private val newAddOnItem = AddOnItem(
        addOnItemId = "1",
        itemName = "test",
        itemPrice = 10,
        createdAt = System.currentTimeMillis().toString()
    )

    @Before
    fun setUp() {
        fakeAddOnItemRepository = FakeAddOnItemRepository()
        createNewAddOnItem = CreateNewAddOnItem(fakeAddOnItemRepository, fakeAddOnItemRepository)
        updateAddOnItem = UpdateAddOnItem(fakeAddOnItemRepository, fakeAddOnItemRepository)
    }


    @Test
    fun `update addon item with invalid id, should return false`() {
        runBlocking {
            val result = updateAddOnItem.invoke(newAddOnItem, "2")

            assertThat(result.data).isFalse()
        }
    }


    @Test
    fun `update addon item with valid data and id,  should return true`() {
        runBlocking {
            val result = createNewAddOnItem.invoke(newAddOnItem)

            assertThat(result.data).isTrue()

            val result2 = updateAddOnItem.invoke(AddOnItem("2","test-second",10), newAddOnItem.addOnItemId)

            assertThat(result2.data).isTrue()
        }
    }

}