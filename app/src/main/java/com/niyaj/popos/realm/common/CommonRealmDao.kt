package com.niyaj.popos.realm.common

interface CommonRealmDao {

    fun countTotalPrice(cartOrderId: String): Pair<Int, Int>

}