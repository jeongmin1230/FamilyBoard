package com.jm.familyboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.NewPasswordSupportingText
import com.jm.familyboard.reusable.TextFieldPlaceholderOrSupporting
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.isEmailValid
import com.jm.familyboard.reusable.sendResetPasswordEmail
import com.jm.familyboard.reusable.textFieldColors
import com.jm.familyboard.reusable.textFieldKeyboard

@Composable
fun FindIdAndPasswordScreen(loginNavController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBar(enabled = false, screenName = stringResource(id = R.string.find_password), imageButtonSource = null, imageFunction = {}) { loginNavController.popBackStack()}
        FindPasswordScreen {
            Toast.makeText(context, context.getString(R.string.send_email), Toast.LENGTH_SHORT).show()
            loginNavController.popBackStack() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindPasswordScreen(onClickBack: () -> Unit) {
    val inputEmail = remember { mutableStateOf("") }
    var emailResult: Int
    Column(Modifier.padding(horizontal = 10.dp)) {
        Column(Modifier.weight(1f)) {
            WhatMean(mean = stringResource(id = R.string.input_email), essential = false)
            TextField(
                value = inputEmail.value,
                onValueChange = { inputEmail.value = it },
                keyboardActions = KeyboardActions(
                    onDone = { if(inputEmail.value.trim().isNotEmpty()) {
                        sendResetPasswordEmail(inputEmail.value)
                        onClickBack()
                    } }),
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                visualTransformation = VisualTransformation.None,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(color = Color.Blue.copy(0.2f)),
                supportingText = {
                    emailResult = isEmailValid(inputEmail.value)
                    if(inputEmail.value.isNotEmpty()) {
                        when(emailResult) {
                            3 -> {
                                TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.find_password_valid), correct = true)
                            }
                            4 -> {
                                TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.find_password_not_valid), correct = false)
                            }
                        }
                    }
                },
                singleLine = true
            )
        }
    }
}