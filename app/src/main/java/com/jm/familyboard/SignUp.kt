package com.jm.familyboard

import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.NewPasswordSupportingText
import com.jm.familyboard.reusable.ConfirmPasswordSupportingText
import com.jm.familyboard.reusable.TextFieldPlaceholderOrSupporting
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.checkEmailDuplicate
import com.jm.familyboard.reusable.checkGroupName
import com.jm.familyboard.reusable.isEmailValid
import com.jm.familyboard.reusable.rolesRadioButton
import com.jm.familyboard.reusable.signUp
import com.jm.familyboard.reusable.textFieldColors
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun SignUpScreen(loginNavController: NavHostController) {
    val context = LocalContext.current
    val screenName = stringResource(id = R.string.sign_up)
    val signUpNavController = rememberNavController()
    NavHost(signUpNavController, startDestination =  context.getString(R.string.sign_up_nav_route_1)) {
        composable(context.getString(R.string.sign_up_nav_route_1)) {
            Column {
                AppBar(screenName) { loginNavController.popBackStack() }
                EnterInfo(context, signUpNavController)
            }
        }
        composable(context.getString(R.string.sign_up_nav_route_2)) {
            Column {
                AppBar(screenName) { signUpNavController.popBackStack() }
                DoneSignUp(context, loginNavController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterInfo(context: Context, signUpNavController: NavHostController) {
    val nameValue = remember { mutableStateOf("") }
    val nameBoolean = remember { mutableStateOf(true) }

    val emailValue = remember { mutableStateOf("") }
    val emailTest = remember { mutableIntStateOf(0) }
    val isEmailTFFocused = remember { mutableStateOf(false) }
    val passwordValue = remember { mutableStateOf("") }
    val passwordBoolean = remember { mutableStateOf(false) }
    val passwordConfirmValue = remember { mutableStateOf("") }
    val passwordConfirmBoolean = remember { mutableStateOf(false) }

    val groupNameValue = remember { mutableStateOf("") }
    val groupNameTest = remember { mutableIntStateOf(0) }
    val isGroupNameTFFocused = remember { mutableStateOf(false) }

    val rolesValue = remember { mutableStateOf("") }
    val rolesBoolean = remember { mutableStateOf(false) }
    Column {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 30.dp)
        ) {
            nameBoolean.value = nameValue.value.isNotEmpty()
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_name),
                tfValue = nameValue,
                keyboardOptions = textFieldKeyboard(ImeAction.Next, KeyboardType.Text),
                isCorrect = nameBoolean,
                visualTransformation = VisualTransformation.None
            ) {}
            if(!isEmailTFFocused.value && emailValue.value.trim().isNotEmpty()) {
                LaunchedEffect(this) {
                    emailTest.intValue = isEmailValid(emailValue.value)
                    if(emailTest.intValue >= 3) {
                        checkEmailDuplicate(emailValue.value.replace("@", "_").replace(".", "_"), emailTest)
                    }
                }
            }
            WhatMean(mean = stringResource(id = R.string.sign_up_email), essential = true)
            TextField(
                value = emailValue.value,
                onValueChange = { emailValue.value = it},
                textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                placeholder = { TextFieldPlaceholderOrSupporting(true, "${stringResource(id = R.string.sign_up_email)} ${stringResource(id = R.string.sign_up_placeholder)}",true) },
                visualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .onFocusChanged { isEmailTFFocused.value = it.isFocused }
                    .fillMaxSize(),
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                supportingText = { if(!isEmailTFFocused.value) {
                        when(emailTest.intValue) {
                            1 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_essential_text), correct = false) }
                            2 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_invalid), correct = false) }
                            3 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_valid_but_duplicate), correct = false)}
                            4 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_valid), correct = true) }
                        }
                    }

                },
                singleLine = true,
                colors = textFieldColors(Color.Blue.copy(0.2f))
            )
            Spacer(modifier = Modifier.height(10.dp))
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_password),
                tfValue = passwordValue,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                isCorrect = passwordBoolean,
                visualTransformation = PasswordVisualTransformation('*'),
            ) { NewPasswordSupportingText(passwordValue.value)}
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_confirm_password),
                tfValue = passwordConfirmValue,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                isCorrect = passwordConfirmBoolean,
                visualTransformation = PasswordVisualTransformation('*'),
            ) { ConfirmPasswordSupportingText(passwordValue.value, passwordConfirmValue.value)}
            if(!isGroupNameTFFocused.value && groupNameValue.value.trim().isNotEmpty()) {
                LaunchedEffect(this) {
                    checkGroupName(groupNameValue.value, groupNameTest)
                }
            }
            WhatMean(mean = stringResource(id = R.string.sign_up_group_name), essential = true)
            TextField(
                value = groupNameValue.value,
                onValueChange = { groupNameValue.value = it},
                textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                placeholder = { TextFieldPlaceholderOrSupporting(true, "${stringResource(id = R.string.sign_up_group_name)} ${stringResource(id = R.string.sign_up_placeholder)}",true) },
                visualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .onFocusChanged { isGroupNameTFFocused.value = it.isFocused }
                    .fillMaxSize(),
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                supportingText = {
                    if(!isGroupNameTFFocused.value) {
                        when(groupNameTest.intValue) {
                            -1 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_essential_text), correct = false) }
                            1 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_group_name_duplicate), correct = false) }
                            2 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_group_name_not_duplicate), correct = true) }
                        }
                    }
                },
                singleLine = true,
                colors = textFieldColors(Color.Blue.copy(0.2f))
            )
            rolesValue.value.ifEmpty { rolesBoolean.value = !rolesBoolean.value }
            WhatMean(mean = stringResource(id = R.string.sign_up_roles), essential = true)
            rolesValue.value = rolesRadioButton()
        }
        val condition = nameValue.value.trim().isNotEmpty()
                && emailValue.value.trim().isNotEmpty()
                && emailTest.intValue == 4
                && passwordValue.value.trim().isNotEmpty()
                && passwordConfirmValue.value.trim().isNotEmpty()
                && groupNameValue.value.trim().isNotEmpty()
                && groupNameTest.intValue == 2
                && rolesValue.value.trim().isNotEmpty()
                && (passwordValue.value == passwordConfirmValue.value)

        CompleteButton(isEnable = condition, text = stringResource(id = R.string.sign_up), modifier = Modifier.fillMaxWidth()) {
            signUp(context, groupNameValue.value, nameValue.value, rolesValue.value, emailValue.value, passwordValue.value, signUpNavController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterInfoColumn(mean: String, tfValue: MutableState<String>, keyboardOptions: KeyboardOptions, isCorrect: MutableState<Boolean>, visualTransformation: VisualTransformation, supportingText: @Composable () -> Unit) {
    Column(modifier = Modifier
        .padding(bottom = 14.dp)
        .fillMaxSize()) {
        WhatMean(mean = mean, essential = true)
        TextField(
            value = tfValue.value,
            onValueChange = { tfValue.value = it},
            textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            placeholder = { TextFieldPlaceholderOrSupporting(true, "$mean ${stringResource(id = R.string.sign_up_placeholder)}",true) },
            interactionSource = MutableInteractionSource(),
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxSize(),
            keyboardOptions = keyboardOptions,
            supportingText = { supportingText() },
            singleLine = true,
            colors = textFieldColors(Color.Blue.copy(0.2f))
        )
    }
}

@Composable
fun CompleteButton(isEnable: Boolean, text: String, modifier: Modifier, onClickButton: () -> Unit) {
    Button(
        enabled = isEnable,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(0.5f), disabledContainerColor = Color.LightGray),
        onClick = onClickButton,
        shape = RectangleShape,
        modifier = modifier.height(48.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun DoneSignUp(context: Context, loginNavController: NavHostController) {
    Column {
        CompleteButton(isEnable = true, text = stringResource(id = R.string.sign_up_complete), modifier = Modifier) {
            loginNavController.navigate(context.getString(R.string.first))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SignUpPreview() {
    FamilyBoardTheme {
        SignUpScreen(rememberNavController())
    }
}