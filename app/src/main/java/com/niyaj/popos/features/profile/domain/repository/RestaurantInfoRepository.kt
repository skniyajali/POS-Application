package com.niyaj.popos.features.profile.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo

interface RestaurantInfoRepository {

    fun getRestaurantInfo(): Resource<RestaurantInfo>

    suspend fun updateRestaurantInfo(restaurantInfo: RestaurantInfo): Resource<Boolean>
}