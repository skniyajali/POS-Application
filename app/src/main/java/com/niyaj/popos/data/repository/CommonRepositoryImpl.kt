package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.repository.CommonRepository
import com.niyaj.popos.realm.common.CommonRealmDao

class CommonRepositoryImpl(
    private val commonRealmDao: CommonRealmDao
) : CommonRepository {

    override fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        return commonRealmDao.countTotalPrice(cartOrderId)
    }
}