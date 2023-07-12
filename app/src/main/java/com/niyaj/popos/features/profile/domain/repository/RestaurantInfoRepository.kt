package com.niyaj.popos.features.profile.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import kotlinx.coroutines.flow.Flow

interface RestaurantInfoRepository {

    fun getRestaurantInfo(): Flow<Resource<RestaurantInfo>>

    suspend fun updateRestaurantLogo(imageName: String): Resource<Boolean>

    suspend fun updatePrintLogo(imageName: String): Resource<Boolean>

    suspend fun updateRestaurantInfo(restaurantInfo: RestaurantInfo): Resource<Boolean>
}