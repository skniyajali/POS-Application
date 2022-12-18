package com.niyaj.popos.realm.main_feed

import com.niyaj.popos.realm.product.ProductRealm

data class ProductWithQuantityRealm(
    val productRealm: ProductRealm,
    val quantity: Int = 0
)
