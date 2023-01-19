package com.niyaj.popos.features.delivery_partner.di

import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerValidationRepository
import com.niyaj.popos.features.delivery_partner.domain.use_cases.CreateNewPartner
import com.niyaj.popos.features.delivery_partner.domain.use_cases.DeletePartner
import com.niyaj.popos.features.delivery_partner.domain.use_cases.GetAllPartners
import com.niyaj.popos.features.delivery_partner.domain.use_cases.GetPartnerByEmail
import com.niyaj.popos.features.delivery_partner.domain.use_cases.GetPartnerById
import com.niyaj.popos.features.delivery_partner.domain.use_cases.GetPartnerByPhone
import com.niyaj.popos.features.delivery_partner.domain.use_cases.PartnerUseCases
import com.niyaj.popos.features.delivery_partner.domain.use_cases.UpdatePartner
import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerEmail
import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerName
import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerPassword
import com.niyaj.popos.features.delivery_partner.domain.use_cases.validation.ValidatePartnerPhone
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeliveryPartnerModule {


    @Provides
    @Singleton
    fun providePartnerUseCases(partnerRepository: PartnerRepository, partnerValidationRepository: PartnerValidationRepository): PartnerUseCases {
        return PartnerUseCases(
            getAllPartners = GetAllPartners(partnerRepository),
            getPartnerById = GetPartnerById(partnerRepository),
            createNewPartner = CreateNewPartner(partnerRepository),
            updatePartner = UpdatePartner(partnerRepository),
            deletePartner = DeletePartner(partnerRepository),
            getPartnerByEmail = GetPartnerByEmail(partnerRepository),
            getPartnerByPhone = GetPartnerByPhone(partnerRepository),
            validatePartnerName = ValidatePartnerName(partnerValidationRepository),
            validatePartnerEmail = ValidatePartnerEmail(partnerValidationRepository),
            validatePartnerPhone = ValidatePartnerPhone(partnerValidationRepository),
            validatePartnerPassword = ValidatePartnerPassword(partnerValidationRepository),

        )
    }
}