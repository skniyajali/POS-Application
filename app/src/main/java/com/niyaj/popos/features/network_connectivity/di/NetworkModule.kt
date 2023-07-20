package com.niyaj.popos.features.network_connectivity.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.niyaj.popos.features.network_connectivity.data.provider.ConnectivityObserverImpl
import com.niyaj.popos.features.network_connectivity.domain.provider.ConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideNetworkRequest() : NetworkRequest {
        return NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    @Provides
    fun provideConnectivityManager(@ApplicationContext context : Context) : ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(
        networkRequest : NetworkRequest,
        connectivityManager : ConnectivityManager,
    ) : ConnectivityObserver {
        return ConnectivityObserverImpl(networkRequest, connectivityManager)
    }
}