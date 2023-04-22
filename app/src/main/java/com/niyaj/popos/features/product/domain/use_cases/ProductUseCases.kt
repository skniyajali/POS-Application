package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.product.domain.use_cases.validation.ValidateCategoryName
import com.niyaj.popos.features.product.domain.use_cases.validation.ValidateProductName
import com.niyaj.popos.features.product.domain.use_cases.validation.ValidateProductPrice

data class ProductUseCases(
    val validateProductName: ValidateProductName,
    val validateCategoryName: ValidateCategoryName,
    val validateProductPrice: ValidateProductPrice,
    val getAllProducts: GetAllProducts,
    val getProductById: GetProductById,
    val createNewProduct: CreateNewProduct,
    val updateProduct: UpdateProduct,
    val deleteProduct: DeleteProduct,
    val increaseProductPrice: IncreaseProductPrice,
    val decreaseProductPrice: DecreaseProductPrice,
    val importProducts: ImportProducts,
)
