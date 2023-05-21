package com.niyaj.popos.features.product.di

import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {

    @Provides
    @Singleton
    fun provideGetAllProductCases(productRepository: ProductRepository): GetAllProducts {
        return GetAllProducts(productRepository)
    }
}