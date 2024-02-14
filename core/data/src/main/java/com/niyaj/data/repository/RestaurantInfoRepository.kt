package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.RestaurantInfo
import kotlinx.coroutines.flow.Flow

interface RestaurantInfoRepository {

    fun getRestaurantInfo(): Flow<RestaurantInfo>

    suspend fun updateRestaurantLogo(imageName: String): Resource<Boolean>

    suspend fun updatePrintLogo(imageName: String): Resource<Boolean>

    suspend fun updateRestaurantInfo(restaurantInfo: RestaurantInfo): Resource<Boolean>
}