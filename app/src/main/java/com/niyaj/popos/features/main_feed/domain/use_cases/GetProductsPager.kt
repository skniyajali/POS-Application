package com.niyaj.popos.features.main_feed.domain.use_cases

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.niyaj.popos.features.main_feed.domain.model.ProductWithQuantity
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository

class GetProductsPager(
    private val mainFeedRepository: MainFeedRepository
): PagingSource<Int, ProductWithQuantity>() {

    override fun getRefreshKey(state: PagingState<Int, ProductWithQuantity>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductWithQuantity> {
        return try {
            val page = params.key ?: 0
            val size = params.loadSize
            val from = page * size

            val data = mainFeedRepository.getProducts(limit = size)

            if (params.placeholdersEnabled) {
                val itemsAfter = data.size - from + data.size
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (data.isEmpty()) null else page + 1,
                    itemsAfter = if (itemsAfter > size) size else itemsAfter.toInt(),
                    itemsBefore = from
                )
            } else {
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (data.isEmpty()) null else page + 1
                )
            }
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}