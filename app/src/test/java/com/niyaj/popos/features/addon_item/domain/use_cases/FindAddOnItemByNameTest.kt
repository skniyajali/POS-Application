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
class FindAddOnItemByNameTest {

    private lateinit var fakeAddOnItemRepository: FakeAddOnItemRepository
    private lateinit var createNewAddOnItem: CreateNewAddOnItem
    private lateinit var findAddOnItemByName: FindAddOnItemByName

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
        findAddOnItemByName = FindAddOnItemByName(fakeAddOnItemRepository)
    }


    @Test
    fun `find addon item by name, return false`() {
        runBlocking {
            val result = findAddOnItemByName.invoke("test-2")

            assertThat(result).isFalse()
        }
    }

    @Test
    fun `find addon item by name, return true`() {
        runBlocking {
            val result = createNewAddOnItem.invoke(newAddOnItem)

            assertThat(result.data).isTrue()

            val result2 = findAddOnItemByName.invoke(newAddOnItem.itemName)

            assertThat(result2).isTrue()
        }
    }
}