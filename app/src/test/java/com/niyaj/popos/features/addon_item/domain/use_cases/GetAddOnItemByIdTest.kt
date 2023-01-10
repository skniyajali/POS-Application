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
class GetAddOnItemByIdTest {

    private lateinit var fakeAddOnItemRepository: FakeAddOnItemRepository
    private lateinit var createNewAddOnItem: CreateNewAddOnItem
    private lateinit var getAddOnItemById: GetAddOnItemById

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
        getAddOnItemById = GetAddOnItemById(fakeAddOnItemRepository)
    }

    @Test
    fun `get addon item by invalid id, return null`() {
        runBlocking {
            val result = getAddOnItemById.invoke("2")

            assertThat(result.data).isNull()
        }
    }

    @Test
    fun `get addon item by valid id, return addon item`() {
        runBlocking {
            val result1 = createNewAddOnItem.invoke(newAddOnItem)

            assertThat(result1.data).isTrue()

            val result = getAddOnItemById.invoke(newAddOnItem.addOnItemId)

            assertThat(result.data).isNotNull()

            assertThat(result.data).isEqualTo(newAddOnItem)
        }
    }
}