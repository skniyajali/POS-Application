package com.niyaj.popos.realm.addon_item.di

import com.niyaj.popos.realm.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.realm.addon_item.domain.use_cases.AddOnItemUseCases
import com.niyaj.popos.realm.addon_item.domain.use_cases.CreateNewAddOnItem
import com.niyaj.popos.realm.addon_item.domain.use_cases.DeleteAddOnItem
import com.niyaj.popos.realm.addon_item.domain.use_cases.FindAddOnItemByName
import com.niyaj.popos.realm.addon_item.domain.use_cases.GetAddOnItemById
import com.niyaj.popos.realm.addon_item.domain.use_cases.GetAllAddOnItems
import com.niyaj.popos.realm.addon_item.domain.use_cases.UpdateAddOnItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddOnItemModule {


//    private val schema = setOf(AddOnItem::class)
//
//    private val config = RealmConfiguration
//        .Builder(schema)
//        .deleteRealmIfMigrationNeeded()
//        .name("popos.realm")
//        .log(LogLevel.ALL)
//        .build()
//
//    @Provides
//    fun provideAddOnItemRealmDaoImpl(): AddOnItemRepository {
//        return AddOnItemRepositoryImpl(config)
//    }

    @Provides
    @Singleton
    fun provideAddOnItemUseCases(addOnItemRepository: AddOnItemRepository): AddOnItemUseCases {
        return AddOnItemUseCases(
            getAllAddOnItems = GetAllAddOnItems(addOnItemRepository),
            getAddOnItemById = GetAddOnItemById(addOnItemRepository),
            findAddOnItemByName = FindAddOnItemByName(addOnItemRepository),
            createNewAddOnItem = CreateNewAddOnItem(addOnItemRepository),
            updateAddOnItem = UpdateAddOnItem(addOnItemRepository),
            deleteAddOnItem = DeleteAddOnItem(addOnItemRepository),
        )
    }
}