package com.niyaj.popos.features.account.presentation.register.components.login_info

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.niyaj.popos.R
import com.niyaj.popos.features.cart_order.presentation.add_edit.PhoneNoCountBox
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.components.ImageCard
import com.niyaj.popos.features.components.NoteCard
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.utils.Constants

@Composable
fun LoginInfo(
    modifier : Modifier,
    lazyListState : LazyListState,
    name : String,
    nameError : String? = null,
    email : String,
    emailError : String? = null,
    phone : String,
    phoneError : String? = null,
    password : String,
    passwordError : String? = null,
    secondaryPhone : String,
    secondaryPhoneError : String? = null,
    resLogo : Bitmap? = null,
    defaultLogo : Int = Constants.RESTAURANT_LOGO.toInt(),
    onChangeName : (LoginInfoEvent) -> Unit,
    onChangePhone : (LoginInfoEvent) -> Unit,
    onChangeEmail : (LoginInfoEvent) -> Unit,
    onChangeSecondaryPhone : (LoginInfoEvent) -> Unit,
    onChangePassword : (LoginInfoEvent) -> Unit,
    onChangeLogo: () -> Unit,
) {
    var showPassword by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.CenterVertically),
        state = lazyListState,
    ) {
        item("Title") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                Text(
                    text = stringResource(R.string.setup_profile),
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = stringResource(R.string.setup_profile_text),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Normal,
                    color = TextGray,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        item("Res_Logo") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ImageCard(
                    defaultImage = defaultLogo,
                    bitmap = resLogo,
                    onEditClick = onChangeLogo
                )

                NoteCard(
                    text = stringResource(id = R.string.login_info_note),
                )
            }
        }

        item("Name_field") {
            StandardOutlinedTextField(
                text = name,
                label = "Restaurant Name",
                leadingIcon = Icons.Default.Restaurant,
                error = nameError,
                onValueChange = {
                    onChangeName(LoginInfoEvent.NameChanged(it))
                }
            )
        }

        item("Phone Field") {
            StandardOutlinedTextField(
                text = phone,
                label = "Phone No",
                leadingIcon = Icons.Default.PhoneAndroid,
                error = phoneError,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    onChangePhone(LoginInfoEvent.PhoneChanged(it))
                },
                trailingIcon = {
                    PhoneNoCountBox(count = phone.length)
                }
            )
        }

        item("Secondary Phone") {
            StandardOutlinedTextField(
                text = secondaryPhone,
                label = "Secondary Phone",
                leadingIcon = Icons.Default.Phone,
                error = secondaryPhoneError,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    onChangeSecondaryPhone(LoginInfoEvent.SecondaryPhoneChanged(it))
                },
                trailingIcon = {
                    PhoneNoCountBox(count = secondaryPhone.length)
                }
            )
        }

        item("Email_Field") {
            StandardOutlinedTextField(
                text = email,
                label = "Email Address",
                leadingIcon = Icons.Default.Email,
                error = emailError,
                keyboardType = KeyboardType.Email,
                onValueChange = {
                    onChangeEmail(LoginInfoEvent.EmailChanged(it))
                }
            )

        }

        item("Password_Field") {
            StandardOutlinedTextField(
                label = stringResource(R.string.password),
                text = password,
                leadingIcon = Icons.Default.Password,
                error = passwordError,
                isPasswordToggleDisplayed = true,
                isPasswordVisible = showPassword,
                onPasswordToggleClick = {
                    showPassword = !showPassword
                },
                onValueChange = {
                    onChangePassword(LoginInfoEvent.PasswordChanged(it))
                }
            )
        }

    }
}