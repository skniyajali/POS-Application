package com.niyaj.popos.features.delivery_partner.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.*
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.delivery_partner.domain.util.PartnerStatus
import com.niyaj.popos.features.delivery_partner.domain.util.PartnerType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class PartnerRepositoryImplTest {

    private lateinit var repository: PartnerRepositoryImpl

    private val newPartner = DeliveryPartner(
        partnerId = "1111",
        partnerName = "New Partner",
        partnerEmail = "new@partner.com",
        partnerPhone = "9078563421",
        partnerPassword = "Password@2k22",
        partnerStatus = PartnerStatus.Active.partnerStatus,
        partnerType = PartnerType.FullTime.partnerType,
        createdAt = System.currentTimeMillis().toString()
    )

    private val updatedPartner = DeliveryPartner(
        partnerId = "1111",
        partnerName = "Updated Partner",
        partnerEmail = "updated@partner.com",
        partnerPhone = "7078563421",
        partnerPassword = "Password@2k22",
        partnerStatus = PartnerStatus.Active.partnerStatus,
        partnerType = PartnerType.FullTime.partnerType,
        createdAt = System.currentTimeMillis().toString()
    )

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = PartnerRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }



    @Test
    fun a_create_new_partner_with_invalid_data_return_false() = runTest {
        val result = repository.createNewPartner(DeliveryPartner())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate partner")
    }

    @Test
    fun b_create_new_partner_with_valid_data_return_true() = runTest {
        val result = repository.createNewPartner(newPartner)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun c_get_partner_with_invalid_id_return_null() = runTest {
        val result = repository.getPartnerById("90ss")

        assertThat(result.data).isNull()
    }

    @Test
    fun d_get_partner_with_valid_id_return_partner() = runTest {
        val result = repository.getPartnerById(newPartner.partnerId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.partnerId).isEqualTo(newPartner.partnerId)
        assertThat(result.data?.partnerName).isEqualTo(newPartner.partnerName)
        assertThat(result.data?.partnerEmail).isEqualTo(newPartner.partnerEmail)
        assertThat(result.data?.partnerPhone).isEqualTo(newPartner.partnerPhone)
    }

    @Test
    fun e_validate_partner_name_with_empty_data_return_false() = runTest {
        val result = repository.validatePartnerName("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Name is required")
    }

    @Test
    fun f_validate_partner_name_with_invalid_data_return_false() = runTest {
        val result = repository.validatePartnerName("nd")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Name must be at least 4 characters long")
    }

    @Test
    fun g_validate_partner_name_with_invalid_data_amd_digit_return_false() = runTest {
        val result = repository.validatePartnerName("ndee2")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Name must not contain any digit")
    }

    @Test
    fun h_validate_partner_name_with_valid_data_return_true() = runTest {
        val result = repository.validatePartnerName("customer")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun i_validate_partner_email_with_empty_data_return_false() = runTest {
        val result = repository.validatePartnerEmail("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Email is required")
    }

    @Test
    fun j_validate_partner_email_with_invalid_data_return_false() = runTest {
        val result = repository.validatePartnerEmail("ndj")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Email is invalid")
    }

    @Test
    fun k_validate_partner_email_that_already_exists_return_false() = runTest {
        val result = repository.validatePartnerEmail(newPartner.partnerEmail)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Email already exists")
    }

    @Test
    fun l_validate_partner_email_that_already_exists_with_id_return_true() = runTest {
        val result = repository.validatePartnerEmail(newPartner.partnerEmail, newPartner.partnerId)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun m_validate_partner_email_with_valid_data_return_true() = runTest {
        val result = repository.validatePartnerEmail("test@gmail.com")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun n_validate_partner_password_with_empty_data_return_false() = runTest {
        val result = repository.validatePartnerPassword("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Password is required")
    }

    @Test
    fun o_validate_partner_password_with_invalid_data_return_false() = runTest {
        val result = repository.validatePartnerPassword("password")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Password must be at least 8 characters long and it must contain a lowercase & uppercase letter and at least one special character and one digit.")
    }

    @Test
    fun p_validate_partner_password_with_valid_data_return_true() = runTest {
        val result = repository.validatePartnerPassword("Password@2k22")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun q_validate_partner_phone_with_empty_data_return_false() = runTest {
        val result = repository.validatePartnerPhone("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone no is required")
    }

    @Test
    fun r_validate_partner_phone_with_less_data_return_false() = runTest {
        val result = repository.validatePartnerPhone("78983")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The phone no must be 10 digits")
    }

    @Test
    fun s_validate_partner_phone_with_data_and_digit_return_false() = runTest {
        val result = repository.validatePartnerPhone("789838933c")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone no must not contain any letter")
    }

    @Test
    fun t_validate_partner_phone_with_data_that_already_exist_return_false() = runTest {
        val result = repository.validatePartnerPhone(newPartner.partnerPhone)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone no already exists")
    }

    @Test
    fun u_validate_partner_phone_with_data_that_already_exist_with_id_return_true() = runTest {
        val result = repository.validatePartnerPhone(newPartner.partnerPhone, newPartner.partnerId)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun v_validate_partner_phone_with_valid_data_return_true() = runTest {
        val result = repository.validatePartnerPhone("7856341290")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun w_update_partner_with_invalid_data_return_false() = runTest {
        val repository = repository.updatePartner(DeliveryPartner(), "")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to validate partner")
    }

    @Test
    fun x_update_partner_with_valid_data_and_invalid_id_return_false() = runTest {
        val repository = repository.updatePartner(updatedPartner, "89dhjd")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to find partner")
    }

    @Test
    fun y_update_partner_with_valid_data_return_true() = runTest {
        val resource = repository.updatePartner(updatedPartner, newPartner.partnerId)

        assertThat(resource.data).isNotNull()
        assertThat(resource.data).isTrue()
        assertThat(resource.message).isNull()

        val result = repository.getPartnerById(updatedPartner.partnerId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.partnerId).isEqualTo(updatedPartner.partnerId)
        assertThat(result.data?.partnerName).isEqualTo(updatedPartner.partnerName)
        assertThat(result.data?.partnerEmail).isEqualTo(updatedPartner.partnerEmail)
        assertThat(result.data?.partnerPhone).isEqualTo(updatedPartner.partnerPhone)
    }

    @Test
    fun z0_find_partner_by_email_return_true() = runTest {
        val result = repository.getPartnerByEmail(updatedPartner.partnerEmail)

        assertThat(result).isTrue()
    }

    @Test
    fun z1_find_partner_by_email_with_id_return_false() = runTest {
        val result = repository.getPartnerByEmail(updatedPartner.partnerEmail, updatedPartner.partnerId)

        assertThat(result).isFalse()
    }

    @Test
    fun z2_find_partner_by_phone_return_true() = runTest {
        val result = repository.getPartnerByPhone(updatedPartner.partnerPhone)

        assertThat(result).isTrue()
    }

    @Test
    fun z3_find_partner_by_phone_with_id_return_true() = runTest {
        val result = repository.getPartnerByPhone(updatedPartner.partnerPhone, updatedPartner.partnerId)

        assertThat(result).isFalse()
    }

    @Test
    fun z4_delete_partner_with_invalid_id_return_false() = runTest {
        val repository = repository.deletePartner("09sd")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to find partner")
    }

    @Test
    fun z5_delete_partner_with_valid_id_return_true() = runTest {
        val repository = repository.deletePartner(updatedPartner.partnerId)

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isTrue()
        assertThat(repository.message).isNull()
    }

    @Test
    fun z6_get_all_partners_return_partners() = runTest {
        val data = createNewPartners()
        assertThat(data).isTrue()

        repository.getAllPartner().onEach {result ->
            when (result) {
                is Resource.Success -> {
                    assertThat(result.data).isNotNull()
                    assertThat(result.message).isNull()
                    assertThat(result.data?.size).isEqualTo(5)
                }
                else -> {}
            }
        }

    }

    private fun createNewPartners(): Boolean {
        return try {
            val partners = mutableListOf<DeliveryPartner>()

            ('A'..'E').filterIndexed { index, c ->
                partners.add(
                    DeliveryPartner(
                        partnerId = index.plus(11111).toString(),
                        partnerName = c.plus("partner"),
                        partnerEmail = c.plus("new@partner.com"),
                        partnerPhone = index.plus(8298330000).toString(),
                        partnerPassword = c.plus("Password@2k22"),
                        partnerStatus = PartnerStatus.Active.partnerStatus,
                        partnerType = PartnerType.FullTime.partnerType,
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
            }

            runTest {
                partners.forEach { partner ->
                    val result = repository.createNewPartner(partner)

                    assertThat(result.data).isNotNull()
                    assertThat(result.data).isTrue()
                    assertThat(result.message).isNull()
                }
            }

            true
        }catch (e: AssertionError) {
            false
        }
    }
}