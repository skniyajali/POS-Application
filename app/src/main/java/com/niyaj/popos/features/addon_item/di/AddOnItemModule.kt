package com.niyaj.popos.features.addon_item.di

import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.ValidationRepository
import com.niyaj.popos.features.addon_item.domain.use_cases.AddOnItemUseCases
import com.niyaj.popos.features.addon_item.domain.use_cases.CreateNewAddOnItem
import com.niyaj.popos.features.addon_item.domain.use_cases.DeleteAddOnItem
import com.niyaj.popos.features.addon_item.domain.use_cases.GetAddOnItemById
import com.niyaj.popos.features.addon_item.domain.use_cases.GetAllAddOnItems
import com.niyaj.popos.features.addon_item.domain.use_cases.UpdateAddOnItem
import com.niyaj.popos.features.addon_item.domain.use_cases.validation.ValidateItemName
import com.niyaj.popos.features.addon_item.domain.use_cases.validation.ValidateItemPrice
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddOnItemModule {
    @Provides
    @Singleton
    fun provideAddOnItemUseCases(
        addOnItemRepository: AddOnItemRepository,
        validationRepository: ValidationRepository,
    ): AddOnItemUseCases {
        return AddOnItemUseCases(
            validateItemName = ValidateItemName(validationRepository),
            validateItemPrice = ValidateItemPrice(validationRepository),
            getAllAddOnItems = GetAllAddOnItems(addOnItemRepository),
            getAddOnItemById = GetAddOnItemById(addOnItemRepository),
            createNewAddOnItem = CreateNewAddOnItem(addOnItemRepository),
            updateAddOnItem = UpdateAddOnItem(addOnItemRepository, validationRepository),
            deleteAddOnItem = DeleteAddOnItem(addOnItemRepository),
        )
    }
}