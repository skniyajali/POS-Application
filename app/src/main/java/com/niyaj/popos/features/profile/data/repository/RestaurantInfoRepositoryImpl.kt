package com.niyaj.popos.features.profile.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository
import com.niyaj.popos.util.Constants.RESTAURANT_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import timber.log.Timber

class RestaurantInfoRepositoryImpl(config: RealmConfiguration) : RestaurantInfoRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Product Session")
    }

    override fun getRestaurantInfo(): Resource<RestaurantInfo> {
        return try {
            val info = realm.query<RestaurantInfo>("restaurantId == $0", RESTAURANT_ID).first().find()

            if (info != null) {
                Resource.Success(info)
            }else {
                Resource.Success(RestaurantInfo())
            }

        }catch (e: Exception){
            Resource.Error(e.message ?: "Unable to get restaurant info", RestaurantInfo())
        }
    }

    override suspend fun updateRestaurantInfo(restaurantInfo: RestaurantInfo): Resource<Boolean> {
        return try {

            realm.write {
                val restaurant = this.query<RestaurantInfo>("restaurantId == $0", RESTAURANT_ID).first().find()

                if (restaurant != null) {
                    restaurant.name = restaurantInfo.name
                    restaurant.tagline = restaurantInfo.tagline
                    restaurant.email = restaurantInfo.email
                    restaurant.primaryPhone = restaurantInfo.primaryPhone
                    restaurant.secondaryPhone = restaurantInfo.secondaryPhone
                    restaurant.description = restaurantInfo.description
                    restaurant.paymentQrCode = restaurantInfo.paymentQrCode
                    restaurant.logo = restaurantInfo.logo
                    restaurant.updatedAt = restaurantInfo.updatedAt
                }else {
                    val newRestaurant = RestaurantInfo()
                    newRestaurant.restaurantId = RESTAURANT_ID
                    newRestaurant.name = restaurantInfo.name
                    newRestaurant.tagline = restaurantInfo.tagline
                    newRestaurant.email = restaurantInfo.email
                    newRestaurant.primaryPhone = restaurantInfo.primaryPhone
                    newRestaurant.secondaryPhone = restaurantInfo.secondaryPhone
                    newRestaurant.description = restaurantInfo.description
                    newRestaurant.paymentQrCode = restaurantInfo.paymentQrCode
                    newRestaurant.logo = restaurantInfo.logo
                    newRestaurant.createdAt = System.currentTimeMillis().toString()

                    this.copyToRealm(newRestaurant)
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update restaurant info", false)
        }
    }
}