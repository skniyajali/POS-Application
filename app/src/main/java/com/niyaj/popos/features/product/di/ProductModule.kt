package com.niyaj.popos.features.product.di

import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.repository.ProductValidationRepository
import com.niyaj.popos.features.product.domain.use_cases.*
import com.niyaj.popos.features.product.domain.use_cases.validation.ValidateCategoryName
import com.niyaj.popos.features.product.domain.use_cases.validation.ValidateProductName
import com.niyaj.popos.features.product.domain.use_cases.validation.ValidateProductPrice
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
    fun provideProductCases(productRepository: ProductRepository, productValidationRepository: ProductValidationRepository): ProductUseCases {
        return ProductUseCases(
            getAllProducts = GetAllProducts(productRepository),
            getProductById = GetProductById(productRepository),
            createNewProduct = CreateNewProduct(productRepository),
            updateProduct = UpdateProduct(productRepository),
            deleteProduct = DeleteProduct(productRepository),
            increaseProductPrice = IncreaseProductPrice(productRepository),
            decreaseProductPrice = DecreaseProductPrice(productRepository),
            importProducts = ImportProducts(productRepository),
            validateProductName = ValidateProductName(productValidationRepository),
            validateCategoryName = ValidateCategoryName(productValidationRepository),
            validateProductPrice = ValidateProductPrice(productValidationRepository),
        )
    }
}