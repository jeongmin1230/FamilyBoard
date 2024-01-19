package com.jm.familyboard

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.EmailSupportingText
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.sendResetPasswordEmail
import com.jm.familyboard.reusable.textFieldColors
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.reusable.textSetting

@Composable
fun ResetPassword(loginNavController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBar(enabled = false, screenName = stringResource(id = R.string.find_password), imageButtonSource = null, imageFunction = {}) { loginNavController.popBackStack()}
        FindPasswordScreen(context) {
            Toast.makeText(context, context.getString(R.string.send_email), Toast.LENGTH_SHORT).show()
            loginNavController.popBackStack() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindPasswordScreen(context: Context, onClickBack: () -> Unit) {
    val inputEmail = remember { mutableStateOf("") }
    Column(Modifier.padding(horizontal = 10.dp)) {
        Column(Modifier.weight(1f)) {
            WhatMean(mean = stringResource(id = R.string.input_email), essential = false)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = inputEmail.value,
                    onValueChange = { inputEmail.value = it },
                    textStyle = textSetting(true),
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(end = 4.dp)
                        .fillMaxWidth(),
                    colors = textFieldColors(color = Color.Blue.copy(0.2f)),
                    singleLine = true
                )
                TextComposable(
                    text = stringResource(id = R.string.check),
                    style = MaterialTheme.typography.labelMedium.copy(Color.Black),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable(enabled = inputEmail.value.isNotEmpty()) {
                        onClickBack()
                        sendResetPasswordEmail(context, inputEmail.value)
                    }
                )
            }

            if(inputEmail.value.isNotEmpty()) {
                EmailSupportingText(email = inputEmail.value)
            }
        }
    }
}