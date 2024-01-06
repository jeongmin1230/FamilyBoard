package com.jm.familyboard

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.jm.familyboard.reusable.CompleteButton
import com.jm.familyboard.reusable.NewPasswordSupportingText
import com.jm.familyboard.reusable.ConfirmPasswordSupportingText
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.TextFieldPlaceholderOrSupporting
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.checkDuplicate
import com.jm.familyboard.reusable.checkInvitationCode
import com.jm.familyboard.reusable.isEmailValid
import com.jm.familyboard.reusable.notoSansKr
import com.jm.familyboard.reusable.selectRadioButton
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
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)) {
                AppBar(false, screenName, null, {}) { loginNavController.popBackStack() }
                EnterInfo(context, signUpNavController)
            }
        }
        composable(context.getString(R.string.sign_up_nav_route_2)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD1C4E9))) {
                AppBar(false, screenName, null, {}) { signUpNavController.popBackStack() }
                DoneSignUp {
                    val intent = Intent(context, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
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
    val emailTest = remember { mutableIntStateOf(5) }
    val isEmailTFFocused = remember { mutableStateOf(false) }
    val passwordValue = remember { mutableStateOf("") }
    val passwordConfirmValue = remember { mutableStateOf("") }

    val yes = remember { mutableStateOf(true) }
    val no = remember { mutableStateOf(false) }

    val invitationCodeTest = remember { mutableIntStateOf(0) }
    val invitationCodeValue = remember { mutableStateOf("") }
    val invitationCodeEnabled = remember { mutableStateOf(true) }
    val groupNameThroughCode = remember { mutableStateOf("") }
    val isInvitationCodeTFFocussed = remember { mutableStateOf(false) }

    val groupNameTest = remember { mutableIntStateOf(0) }
    val groupNameValue = remember { mutableStateOf("") }
    val groupNameEnabled = remember { mutableStateOf(true) }
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
            EnterInfoSingleColumn(
                essential = true,
                mean = stringResource(id = R.string.sign_up_name),
                tfValue = nameValue,
                keyboardOptions = textFieldKeyboard(ImeAction.Next, KeyboardType.Text),
                visualTransformation = VisualTransformation.None,
                modifier = Modifier.fillMaxWidth()
            ) {}
            if(!isEmailTFFocused.value && emailValue.value.trim().isNotEmpty()) {
                LaunchedEffect(this) {
                    emailTest.intValue = isEmailValid(emailValue.value)
                    if(emailTest.intValue <= 3) {
                        checkDuplicate("real/user/email", emailValue.value.replace("@", "_").replace(".", "_"), emailTest, 2, 1)
                    }
                }
            }
            WhatMean(mean = stringResource(id = R.string.sign_up_email), essential = true)
            TextField(
                value = emailValue.value,
                onValueChange = { emailValue.value = it},
                textStyle = TextStyle(
                    fontFamily = notoSansKr,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                    color = Color.Black
                ),
                placeholder = { TextFieldPlaceholderOrSupporting(true, "${stringResource(id = R.string.sign_up_email)} ${stringResource(id = R.string.sign_up_placeholder)}",true) },
                visualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .onFocusChanged { isEmailTFFocused.value = it.isFocused }
                    .fillMaxSize(),
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
                supportingText = { if(!isEmailTFFocused.value) {
                        when(emailTest.intValue) {
                            1 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_valid), correct = true) }
                            2, 3 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_valid_but_duplicate), correct = false)}
                            4 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_invalid), correct = false) }
                        }
                    }

                },
                singleLine = true,
                colors = textFieldColors(Color.Blue.copy(0.2f))
            )
            Spacer(modifier = Modifier.height(10.dp))
            EnterInfoSingleColumn(
                essential = true,
                mean = stringResource(id = R.string.sign_up_password),
                tfValue = passwordValue,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier.fillMaxWidth()
            ) { NewPasswordSupportingText(passwordValue.value)}
            EnterInfoSingleColumn(
                essential = true,
                mean = stringResource(id = R.string.sign_up_confirm_password),
                tfValue = passwordConfirmValue,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier.fillMaxWidth()
            ) { ConfirmPasswordSupportingText(passwordValue.value, passwordConfirmValue.value)}
            WhatMean(mean = stringResource(id = R.string.sign_up_method), essential = true)
            Type(yes, no) {
                invitationCodeValue.value = ""
                invitationCodeTest.intValue = 0
                groupNameValue.value = ""
                groupNameTest.intValue = 0
            }
            if(invitationCodeTest.intValue == 2) {
                invitationCodeEnabled.value = false
                groupNameTest.intValue = 0
                groupNameEnabled.value = false
                groupNameValue.value = ""
            }
            if(groupNameTest.intValue == 2) {
                invitationCodeValue.value = ""
                invitationCodeTest.intValue = 0
                invitationCodeEnabled.value = false
                groupNameEnabled.value = false
            }
            if(yes.value) {
                if(!isInvitationCodeTFFocussed.value && invitationCodeValue.value.isNotEmpty()) {
                    LaunchedEffect(this) {
//                        checkDuplicate("user/real_user_group_name", invitationCodeValue.value, invitationCodeTest, 2, 1)
                        checkInvitationCode(invitationCodeTest, invitationCodeValue.value, groupNameThroughCode)
                    }
                }
                TextField(
                    value = invitationCodeValue.value,
                    onValueChange = { invitationCodeValue.value = it},
                    textStyle = TextStyle(
                        fontFamily = notoSansKr,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        color = if(invitationCodeTest.intValue != 2) Color.Black else Color.DarkGray.copy(0.5f)
                    ),
                    enabled = invitationCodeEnabled.value,
                    placeholder = { TextFieldPlaceholderOrSupporting(true, "${stringResource(id = R.string.sign_up_invite_code)} ${stringResource(id = R.string.sign_up_placeholder)}",true) },
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .onFocusChanged { isInvitationCodeTFFocussed.value = it.isFocused }
                        .fillMaxSize(),
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    supportingText = {
                        if(!isInvitationCodeTFFocussed.value && invitationCodeValue.value.isNotEmpty()) {
                            when(invitationCodeTest.intValue) {
                                1 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_not_exist_invite_code), correct = false) }
                                2 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_exist_invite_code), correct = true) }
                            }
                        }
                    },
                    singleLine = true,
                    colors = textFieldColors(Color.Blue.copy(0.2f))
                )
            }
            else {
                if(!isGroupNameTFFocused.value && groupNameValue.value.isNotEmpty()) {
                    LaunchedEffect(this) {
                        checkDuplicate("real/group_name_and_invitation_code", groupNameValue.value, groupNameTest, 1, 2)
                    }
                }
                TextField(
                    value = groupNameValue.value,
                    onValueChange = { groupNameValue.value = it},
                    textStyle = TextStyle(
                        fontFamily = notoSansKr,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        color = if(groupNameTest.intValue != 2) Color.Black else Color.DarkGray.copy(0.5f)
                    ),
                    enabled = groupNameEnabled.value,
                    placeholder = { TextFieldPlaceholderOrSupporting(true, "${stringResource(id = R.string.sign_up_group_name)} ${stringResource(id = R.string.sign_up_placeholder)}",true) },
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .onFocusChanged { isGroupNameTFFocused.value = it.isFocused }
                        .fillMaxSize(),
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    supportingText = {
                        if(!isGroupNameTFFocused.value && groupNameValue.value.isNotEmpty()) {
                            when(groupNameTest.intValue) {
                                1 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_group_name_duplicate), correct = false) }
                                2 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_group_name_not_duplicate), correct = true) }
                            }
                        }
                    },
                    singleLine = true,
                    colors = textFieldColors(Color.Blue.copy(0.2f))
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            rolesValue.value.ifEmpty { rolesBoolean.value = !rolesBoolean.value }
            WhatMean(mean = stringResource(id = R.string.sign_up_roles), essential = true)
            rolesValue.value = selectRadioButton(stringArrayResource(id = R.array.roles).toList()) }
        val condition = nameValue.value.trim().isNotEmpty()
                && emailValue.value.trim().isNotEmpty()
                && emailTest.intValue == 1
                && passwordValue.value.trim().isNotEmpty()
                && passwordConfirmValue.value.trim().isNotEmpty()
                && (passwordValue.value == passwordConfirmValue.value)
                && (if(invitationCodeValue.value.isNotEmpty()) invitationCodeTest.intValue == 2 else groupNameTest.intValue == 2)
                && rolesValue.value.trim().isNotEmpty()

        CompleteButton(isEnable = condition, color = Color.Blue.copy(0.2f), text = stringResource(id = R.string.sign_up), modifier = Modifier.fillMaxWidth()) {
            signUp(context, groupNameTest, if(invitationCodeValue.value.isNotEmpty()) groupNameThroughCode else groupNameValue, nameValue.value, rolesValue.value, emailValue.value, passwordValue.value, signUpNavController)
        }
    }
}

@Composable
fun Type(first: MutableState<Boolean>, second: MutableState<Boolean>, click: () -> Unit) {
    Row(modifier = Modifier
        .padding(start = 10.dp, bottom = 10.dp)) {
        TextComposable(
            text = stringResource(id = R.string.sign_up_yes_invite_code),
            style = MaterialTheme.typography.bodySmall.copy(if(first.value) Color.Black else Color.LightGray),
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(end = 4.dp)
                .clickable {
                    click()
                    first.value = true
                    second.value = false
                }
        )
        TextComposable(
            text = stringResource(id = R.string.sign_up_no_invite_code),
            style = MaterialTheme.typography.bodySmall.copy(if(second.value)Color.Black else Color.LightGray),
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(start = 4.dp)
                .clickable {
                    click()
                    first.value = false
                    second.value = true
                }
        )
    }
}

@Composable
fun DoneSignUp(onDone: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextComposable(
                text = stringResource(id = R.string.sign_up_complete),
                style = MaterialTheme.typography.titleLarge.copy(color = Color.Black, textAlign = TextAlign.Center),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
        CompleteButton(
            isEnable = true,
            text = stringResource(id = R.string.go_to_login),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth()) {
            onDone()
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