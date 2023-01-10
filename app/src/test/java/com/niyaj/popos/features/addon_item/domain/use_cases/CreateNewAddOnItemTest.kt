package com.niyaj.popos.features.addon_item.domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.features.addon_item.data.repository.FakeAddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.model.InvalidAddOnItemException
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CreateNewAddOnItemTest {

    private lateinit var fakeAddOnItemRepository: FakeAddOnItemRepository
    private lateinit var createNewAddOnItem: CreateNewAddOnItem
    private  lateinit var getAddOnItemById: GetAddOnItemById

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


    @Test(expected = InvalidAddOnItemException::class)
    fun `create new add on item with empty data, throws exception`() {
        runBlocking {
            createNewAddOnItem.invoke(AddOnItem())
        }
    }

    @Test(expected = InvalidAddOnItemException::class)
    fun `create new add on item with price 0, throws exception`() {
        runBlocking {
            createNewAddOnItem.invoke(AddOnItem(itemName = "ss", itemPrice = 0))
        }
    }

    @Test(expected = InvalidAddOnItemException::class)
    fun `create new add on item with price less than 5, throws exception`() {
        runBlocking {
            createNewAddOnItem.invoke(AddOnItem(itemName = "ss", itemPrice = 3))
        }
    }

    @Test
    fun `create new addon item, should return successful`() {
        runBlocking {
            val result = createNewAddOnItem.invoke(newAddOnItem)

            assertThat(result.data).isNotNull()

            assertThat(result.data).isTrue()

            assertThat(result.message).isNull()

            val findAddOnItem = getAddOnItemById.invoke("1")

            assertThat(findAddOnItem.data).isNotNull()

            assertThat(findAddOnItem.data).isEqualTo(newAddOnItem)
        }
    }
}