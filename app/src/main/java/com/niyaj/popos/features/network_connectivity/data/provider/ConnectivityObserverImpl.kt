package com.niyaj.popos.features.network_connectivity.data.provider

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.niyaj.popos.features.network_connectivity.domain.model.ConnectivityStatus
import com.niyaj.popos.features.network_connectivity.domain.provider.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ConnectivityObserverImpl(
    private val networkRequest : NetworkRequest,
    private val connectivityManager : ConnectivityManager,
) : ConnectivityObserver {

    override fun observeConnectivity() : Flow<ConnectivityStatus> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network : Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityStatus.Available) }
                }

                override fun onLosing(network : Network, maxMsToLive : Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(ConnectivityStatus.Losing) }
                }

                override fun onLost(network : Network) {
                    super.onLost(network)
                    launch { send(ConnectivityStatus.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityStatus.Unavailable) }
                }
            }

            connectivityManager.requestNetwork(networkRequest, callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}