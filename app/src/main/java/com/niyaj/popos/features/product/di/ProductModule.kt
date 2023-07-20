package com.niyaj.popos.features.product.di

import com.niyaj.popos.features.product.data.repository.ProductRepositoryImpl
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.repository.ProductValidationRepository
import com.niyaj.popos.features.product.domain.use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {

    @Provides
    fun provideProductRepositoryImpl(config : RealmConfiguration) : ProductRepository {
        return ProductRepositoryImpl(config)
    }

    @Provides
    fun provideProductValidationRepositoryImpl(config : RealmConfiguration) : ProductValidationRepository {
        return ProductRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideGetAllProductCases(productRepository : ProductRepository) : GetAllProducts {
        return GetAllProducts(productRepository)
    }
}