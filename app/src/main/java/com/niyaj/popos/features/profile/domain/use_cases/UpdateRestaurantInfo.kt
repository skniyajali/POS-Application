package com.niyaj.popos.features.profile.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository

class UpdateRestaurantInfo(private val restaurantInfoRepository: RestaurantInfoRepository) {

    suspend operator fun invoke(restaurantInfo: RestaurantInfo): Resource<Boolean> {
        return restaurantInfoRepository.updateRestaurantInfo(restaurantInfo)
    }
}