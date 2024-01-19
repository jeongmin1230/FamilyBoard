package com.jm.familyboard.reusable

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jm.familyboard.R
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun today(context: Context): String {
    return SimpleDateFormat(context.getString(R.string.announcement_date_format)).format(Date(System.currentTimeMillis()))
}

@Composable
fun EmailSupportingText(email: String) {
    if(email.isNotEmpty()) {
        when(isEmailValid(email)) {
            true -> { TextFieldPlaceholderOrSupporting(false, stringResource(id = R.string.sign_up_email_valid), true) }
            false -> { TextFieldPlaceholderOrSupporting(false, stringResource(id = R.string.sign_up_email_invalid), false) }
        }
    }
}

@Composable
fun NewPasswordSupportingText(newPassword: String) {
    if(newPassword.isNotEmpty()) {
        when(checkPasswordFormat(newPassword)) {
            true -> { TextFieldPlaceholderOrSupporting(false, stringResource(id = R.string.sign_up_password_appropriate), true) }
            false -> { TextFieldPlaceholderOrSupporting(false, stringResource(id = R.string.sign_up_password_hint), false) }
        }
    }
}

@Composable
fun ConfirmPasswordSupportingText(newPw: String, confirmPw: String) {
    if(confirmPw.isNotEmpty()) {
        when(newPw == confirmPw) {
            true -> { TextFieldPlaceholderOrSupporting(false, stringResource(id = R.string.sign_up_confirm_password_equal), true) }
            false -> { TextFieldPlaceholderOrSupporting(false, stringResource(id = R.string.sign_up_confirm_password_not_equal), false) }
        }
    }
}

fun isEmailValid(email: String): Boolean {
    val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}")
    return email.matches(emailPattern)
}

fun checkPasswordFormat(password: String): Boolean {
    return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#\$%^&*]).{8,20}\$".toRegex())
}

fun generateInvitationCode(): String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return List(8) {charset.random()}.joinToString("")
}