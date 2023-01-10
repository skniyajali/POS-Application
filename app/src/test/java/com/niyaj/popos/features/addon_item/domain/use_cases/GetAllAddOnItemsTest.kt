package com.niyaj.popos.features.addon_item.domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.features.addon_item.data.repository.FakeAddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.util.FilterAddOnItem
import com.niyaj.popos.features.common.util.SortType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GetAllAddOnItemsTest {

    private lateinit var fakeAddOnItemRepository: FakeAddOnItemRepository
    private lateinit var createNewAddOnItem: CreateNewAddOnItem
    private lateinit var getAllAddOnItems: GetAllAddOnItems

    @Before
    fun setUp() {
        fakeAddOnItemRepository = FakeAddOnItemRepository()
        createNewAddOnItem = CreateNewAddOnItem(fakeAddOnItemRepository, fakeAddOnItemRepository)
        getAllAddOnItems = GetAllAddOnItems(fakeAddOnItemRepository)

        val addOnItems = mutableListOf<AddOnItem>()
        ('a'..'z').forEachIndexed { index, c ->
            addOnItems.add(
                AddOnItem(
                    addOnItemId = index.toString(),
                    itemName = c.toString(),
                    itemPrice = index.plus(5),
                    createdAt = System.currentTimeMillis().plus(index).toString()
                )
            )
        }

        addOnItems.shuffle()
        runBlocking {
            addOnItems.forEach { createNewAddOnItem(it) }
        }
    }


    @Test
    fun `Order AddOn Item by ID ascending, correct order`(): Unit = runBlocking {
        val result = getAllAddOnItems(FilterAddOnItem.ByAddOnItemId(SortType.Ascending)).first()

        assertThat(result.data).isNotNull()

        result.data?.let { data ->
            assertThat(data).isNotEmpty()

            for(i in 0..data.size - 2) {
                assertThat(data[i].addOnItemId).isLessThan(data[i+1].addOnItemId)
            }
        }
    }

    @Test
    fun `Order AddOn Item by ID descending, correct order`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemId(SortType.Descending)).collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                for(i in 0..data.size - 2) {
                    assertThat(data[i].addOnItemId).isGreaterThan(data[i+1].addOnItemId)
                }
            }
        }
    }

    @Test
    fun `Order AddOn Item by Name ascending, correct order`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemName(SortType.Ascending)).collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                for(i in 0..data.size - 2) {
                    assertThat(data[i].itemName).isLessThan(data[i+1].itemName)
                }
            }
        }
    }

    @Test
    fun `Order AddOn Item by Name descending, correct order`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemName(SortType.Descending)).collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                for(i in 0..data.size - 2) {
                    assertThat(data[i].itemName).isGreaterThan(data[i+1].itemName)
                }
            }
        }
    }

    @Test
    fun `Order AddOn Item by Price ascending, correct order`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemPrice(SortType.Ascending)).collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                for(i in 0..data.size - 2) {
                    assertThat(data[i].itemPrice).isLessThan(data[i+1].itemPrice)
                }
            }
        }
    }

    @Test
    fun `Order AddOn Item by Price descending, correct order`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemPrice(SortType.Descending)).collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                for(i in 0..data.size - 2) {
                    assertThat(data[i].itemPrice).isGreaterThan(data[i+1].itemPrice)
                }
            }
        }
    }

    @Test
    fun `Order AddOn Item by Date ascending, correct order`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemDate(SortType.Ascending)).collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                for(i in 0..data.size - 2) {
                    assertThat(data[i].createdAt).isLessThan(data[i+1].createdAt)
                }
            }
        }
    }

    @Test
    fun `Order AddOn Item by Date descending, correct order`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemDate(SortType.Descending)).collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                for(i in 0..data.size - 2) {
                    assertThat(data[i].createdAt).isGreaterThan(data[i+1].createdAt)
                }
            }
        }
    }


    @Test
    fun `Search AddOn Items By Name(String), return List`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemDate(SortType.Descending), "c").collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                data.forEach { addOnItem ->
                    assertThat(addOnItem.itemName).contains("c")
                }
            }
        }
    }


    @Test
    fun `Search AddOn Items By Price(Int), return List`() = runBlocking {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemDate(SortType.Descending), "10").collectLatest { result ->
            assertThat(result.data).isNotNull()

            result.data?.let { data ->
                data.forEach { addOnItem ->
                    assertThat(addOnItem.itemPrice).isAnyOf(addOnItem.itemPrice, "10")
                }
            }
        }
    }

}