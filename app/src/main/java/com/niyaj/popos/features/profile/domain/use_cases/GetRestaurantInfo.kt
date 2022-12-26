package com.niyaj.popos.features.profile.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository

class GetRestaurantInfo(private val restaurantInfoRepository: RestaurantInfoRepository) {
    operator fun invoke(): Resource<RestaurantInfo> {
        return restaurantInfoRepository.getRestaurantInfo()
    }
}