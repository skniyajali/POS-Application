package com.niyaj.popos.domain.repository

interface CommonRepository {

    fun countTotalPrice(cartOrderId: String): Pair<Int, Int>

}