package com.niyaj.popos.features.network_connectivity.domain.provider

import com.niyaj.popos.features.network_connectivity.domain.model.ConnectivityStatus
import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun observeConnectivity(): Flow<ConnectivityStatus>
}