package com.niyaj.popos.features.product.di

import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.use_cases.CreateNewProduct
import com.niyaj.popos.features.product.domain.use_cases.DecreaseProductPrice
import com.niyaj.popos.features.product.domain.use_cases.DeleteProduct
import com.niyaj.popos.features.product.domain.use_cases.FindProductByName
import com.niyaj.popos.features.product.domain.use_cases.GetAllProducts
import com.niyaj.popos.features.product.domain.use_cases.GetProductById
import com.niyaj.popos.features.product.domain.use_cases.GetProductsByCategoryId
import com.niyaj.popos.features.product.domain.use_cases.ImportProducts
import com.niyaj.popos.features.product.domain.use_cases.IncreaseProductPrice
import com.niyaj.popos.features.product.domain.use_cases.ProductUseCases
import com.niyaj.popos.features.product.domain.use_cases.UpdateProduct
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
    fun provideProductCases(productRepository: ProductRepository): ProductUseCases {
        return ProductUseCases(
            getAllProducts = GetAllProducts(productRepository),
            getProductById = GetProductById(productRepository),
            getProductsByCategoryId = GetProductsByCategoryId(productRepository),
            createNewProduct = CreateNewProduct(productRepository),
            updateProduct = UpdateProduct(productRepository),
            deleteProduct = DeleteProduct(productRepository),
            increaseProductPrice = IncreaseProductPrice(productRepository),
            decreaseProductPrice = DecreaseProductPrice(productRepository),
            importProducts = ImportProducts(productRepository),
            findProductByName = FindProductByName(productRepository),
        )
    }
}