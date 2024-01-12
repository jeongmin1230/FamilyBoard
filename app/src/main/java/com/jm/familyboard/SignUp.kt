package com.jm.familyboard

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.CompleteButton
import com.jm.familyboard.reusable.FirebaseAllPath
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.TextFieldPlaceholderOrSupporting
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.checkDuplicate
import com.jm.familyboard.reusable.isEmailValid
import com.jm.familyboard.reusable.textFieldColors
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.reusable.textSetting
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
                EnterInfo(/*context, signUpNavController*/)
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EnterInfo(context: Context, signUpNavController: NavHostController) {
//    val nameValue = remember { mutableStateOf("") }
//
//    val emailValue = remember { mutableStateOf("") }
//    val emailTest = remember { mutableIntStateOf(5) }
//    val emailDuplicate = remember { mutableStateOf(false) }
//    val isEmailEnabled = remember { mutableStateOf(true) }
//
//    val passwordValue = remember { mutableStateOf("") }
//    val passwordConfirmValue = remember { mutableStateOf("") }
//
//    val yes = remember { mutableStateOf(true) }
//    val no = remember { mutableStateOf(false) }
//
//    val invitationCodeTest = remember { mutableIntStateOf(0) }
//    val invitationCodeValue = remember { mutableStateOf("") }
//    val invitationCodeEnabled = remember { mutableStateOf(true) }
//    val groupNameThroughCode = remember { mutableStateOf("") }
//    val invitationCheck = remember { mutableStateOf(false) }
//
//    val groupNameTest = remember { mutableIntStateOf(0) }
//    val groupNameValue = remember { mutableStateOf("") }
//    val groupNameEnabled = remember { mutableStateOf(true) }
//    val isGroupNameEnabled = remember { mutableStateOf(true) }
//    val groupCheck = remember { mutableStateOf(false) }
//    val groupNameCheck = remember { mutableStateOf(false) }
//
//    val rolesValue = remember { mutableStateOf("") }
//    val rolesBoolean = remember { mutableStateOf(false) }
//    Column {
//        Column(
//            modifier = Modifier
//                .verticalScroll(rememberScrollState())
//                .weight(1f)
//                .padding(horizontal = 10.dp, vertical = 30.dp)
//        ) {
//            EnterInfoSingleColumn(
//                essential = true,
//                mean = stringResource(id = R.string.sign_up_name),
//                tfValue = nameValue,
//                keyboardOptions = textFieldKeyboard(ImeAction.Next, KeyboardType.Text),
//                visualTransformation = VisualTransformation.None,
//                modifier = Modifier.fillMaxWidth()
//            ) {}
//
//            LaunchedEffect(emailDuplicate.value) {
//                checkDuplicate(FirebaseAllPath.USER_EMAIL, emailValue.value.replace("@", "_").replace(".", "_"), emailTest, 2, 1)
//            }
//            WhatMean(mean = stringResource(id = R.string.sign_up_email), essential = true)
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                TextField(
//                    value = emailValue.value,
//                    onValueChange = { emailValue.value = it },
//                    textStyle = textSetting(!isEmailEnabled.value),
//                    visualTransformation = VisualTransformation.None,
//                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
//                    placeholder = {
//                        TextFieldPlaceholderOrSupporting(
//                            isPlaceholder = true,
//                            text = "${stringResource(id = R.string.sign_up_email)} ${stringResource(id = R.string.sign_up_placeholder)} ",
//                            correct = true
//                        )},
//                    modifier = Modifier
//                        .padding(end = 4.dp)
//                        .weight(1f),
//                    singleLine = true,
//                    enabled = isEmailEnabled.value,
//                    colors = textFieldColors(Color.Blue.copy(0.2f))
//                )
//                TextComposable(
//                    text = stringResource(id = R.string.check_duplicate),
//                    style = MaterialTheme.typography.labelMedium.copy(Color.Black),
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .clickable { emailDuplicate.value = true })
//            }
//            if(emailDuplicate.value) {
//                AlertDialog(
//                    onDismissRequest = { emailDuplicate.value = false },
//                    containerColor = Color.White,
//                    text = {
//                        val text = when (emailTest.intValue) {
//                            1 -> stringResource(id = R.string.sign_up_email_valid)
//                            2, 3 -> stringResource(id = R.string.sign_up_email_valid_but_duplicate)
//                            4 -> stringResource(id = R.string.sign_up_email_invalid)
//                            else -> ""
//                        }
//                        TextComposable(
//                            text = text,
//                            style = MaterialTheme.typography.labelMedium.copy(Color.Black),
//                            fontWeight = FontWeight.Normal,
//                            modifier = Modifier
//                        )
//                    },
//                    confirmButton = {
//                        if(emailTest.intValue == 1) {
//                            TextComposable(
//                                text = stringResource(id = R.string.check),
//                                style = MaterialTheme.typography.labelMedium.copy(Color.Black),
//                                fontWeight = FontWeight.Normal,
//                                modifier = Modifier.clickable {
//                                    emailDuplicate.value = false
//                                    isEmailEnabled.value = false
//                                }
//                            )
//                        }
//                    },
//                    dismissButton = {
//                        TextComposable(
//                            text = stringResource(id = R.string.cancel),
//                            style = MaterialTheme.typography.labelMedium.copy(Color.Red),
//                            fontWeight = FontWeight.Normal,
//                            modifier = Modifier.clickable {
//                                emailDuplicate.value = false
//                                emailValue.value = ""
//                            }
//                        )
//                    }
//                )
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//
//            EnterInfoSingleColumn(
//                essential = true,
//                mean = stringResource(id = R.string.sign_up_password),
//                tfValue = passwordValue,
//                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
//                visualTransformation = PasswordVisualTransformation('*'),
//                modifier = Modifier.fillMaxWidth()
//            ) { NewPasswordSupportingText(passwordValue.value)}
//
//            EnterInfoSingleColumn(
//                essential = true,
//                mean = stringResource(id = R.string.sign_up_confirm_password),
//                tfValue = passwordConfirmValue,
//                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
//                visualTransformation = PasswordVisualTransformation('*'),
//                modifier = Modifier.fillMaxWidth()
//            ) { ConfirmPasswordSupportingText(passwordValue.value, passwordConfirmValue.value)}
//
//            WhatMean(mean = stringResource(id = R.string.sign_up_method), essential = true)
//            Type(yes, no) {
//                invitationCodeValue.value = ""
//                invitationCodeTest.intValue = 0
//                groupNameValue.value = ""
//                groupNameTest.intValue = 0
//            }
//            if(invitationCodeTest.intValue == 2) {
//                invitationCodeEnabled.value = false
//                groupNameTest.intValue = 0
//                isGroupNameEnabled.value = false
//                groupNameValue.value = ""
//            }
//            if(groupNameTest.intValue == 2) {
//                invitationCodeValue.value = ""
//                invitationCodeTest.intValue = 0
//                invitationCodeEnabled.value = false
//                isGroupNameEnabled.value = false
//            }
//            if(yes.value) {
//                LaunchedEffect(invitationCheck.value) {
//                    checkInvitationCode(invitationCodeTest, invitationCodeValue.value, groupNameThroughCode)
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    TextField(
//                        value = invitationCodeValue.value,
//                        onValueChange = { invitationCodeValue.value = it },
//                        textStyle = textSetting(invitationCodeEnabled.value),
//                        visualTransformation = VisualTransformation.None,
//                        keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
//                        placeholder = {
//                            TextFieldPlaceholderOrSupporting(
//                                isPlaceholder = true,
//                                text = "${stringResource(id = R.string.sign_up_invite_code)} ${stringResource(id = R.string.sign_up_placeholder)} ",
//                                correct = true
//                            )},
//                        modifier = Modifier
//                            .padding(end = 4.dp)
//                            .weight(1f),
//                        singleLine = true,
//                        enabled = isEmailEnabled.value,
//                        colors = textFieldColors(Color.Blue.copy(0.2f))
//                    )
//                    TextComposable(
//                        text = stringResource(id = R.string.check_code),
//                        style = MaterialTheme.typography.labelMedium.copy(Color.Black),
//                        fontWeight = FontWeight.Normal,
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .clickable { invitationCheck.value = true })
//                }
//                if(invitationCheck.value) {
//                    AlertDialog(
//                        onDismissRequest = { invitationCheck.value = false },
//                        containerColor = Color.White,
//                        text = {
//                            val text = when(invitationCodeTest.intValue) {
//                                1 -> stringResource(id = R.string.sign_up_not_exist_invite_code)
//                                2 -> stringResource(id = R.string.sign_up_exist_invite_code)
//                                else -> ""
//                            }
//                            TextComposable(
//                                text = text,
//                                style = MaterialTheme.typography.labelMedium.copy(Color.Black),
//                                fontWeight = FontWeight.Normal,
//                                modifier = Modifier
//                            )
//                        },
//                        confirmButton = {
//                            if(invitationCodeTest.intValue == 2) {
//                                TextComposable(
//                                    text = stringResource(id = R.string.check),
//                                    style = MaterialTheme.typography.labelMedium.copy(Color.Black),
//                                    fontWeight = FontWeight.Normal,
//                                    modifier = Modifier.clickable {
//                                        invitationCheck.value = false
////                                        invitationCodeEnabled.value = false
//                                    }
//                                )
//                            }
//                        },
//                        dismissButton = {
//                            TextComposable(
//                                text = stringResource(id = R.string.cancel),
//                                style = MaterialTheme.typography.labelMedium.copy(Color.Red),
//                                fontWeight = FontWeight.Normal,
//                                modifier = Modifier.clickable {
//                                    invitationCheck.value = false
////                                    invitationCodeEnabled.value = true
////                                    invitationCodeValue.value = ""
//                                }
//                            )
//                        }
//                    )
//                }
//            }
//            else {
//                LaunchedEffect(groupNameCheck.value) {
//                    checkDuplicate(FirebaseAllPath.GROUP_NAME_AND_INVITATION_CODE, groupNameValue.value, groupNameTest, 1, 2)
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    TextField(
//                        value = groupNameValue.value,
//                        onValueChange = { groupNameValue.value = it },
//                        textStyle = textSetting(groupCheck.value),
//                        visualTransformation = VisualTransformation.None,
//                        keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
//                        placeholder = {
//                            TextFieldPlaceholderOrSupporting(
//                                isPlaceholder = true,
//                                text = "${stringResource(id = R.string.sign_up_group_name)} ${stringResource(id = R.string.sign_up_placeholder)} ",
//                                correct = true
//                            )},
//                        modifier = Modifier
//                            .padding(end = 4.dp)
//                            .weight(1f),
//                        singleLine = true,
////                        enabled = isGroupNameEnabled.value,
//                        colors = textFieldColors(Color.Blue.copy(0.2f))
//                    )
//                    TextComposable(
//                        text = stringResource(id = R.string.check_duplicate),
//                        style = MaterialTheme.typography.labelMedium.copy(Color.Black),
//                        fontWeight = FontWeight.Normal,
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .clickable { groupNameCheck.value = true })
//                }
//            }
//            Spacer(modifier = Modifier.height(14.dp))
//            rolesValue.value.ifEmpty { rolesBoolean.value = !rolesBoolean.value }
//            WhatMean(mean = stringResource(id = R.string.sign_up_roles), essential = true)
//            rolesValue.value = selectRadioButton(stringArrayResource(id = R.array.roles).toList()) }
//        val condition = nameValue.value.trim().isNotEmpty()
//                && emailValue.value.trim().isNotEmpty()
//                && emailTest.intValue == 1
//                && passwordValue.value.trim().isNotEmpty()
//                && passwordConfirmValue.value.trim().isNotEmpty()
//                && (passwordValue.value == passwordConfirmValue.value)
//                && (if(invitationCodeValue.value.isNotEmpty()) invitationCodeTest.intValue == 2 else groupNameTest.intValue == 2)
//                && rolesValue.value.trim().isNotEmpty()
//
//        CompleteButton(isEnable = condition, color = Color.Blue.copy(0.2f), text = stringResource(id = R.string.sign_up), modifier = Modifier.fillMaxWidth()) {
//            signUp(context, groupNameTest, if(invitationCodeValue.value.isNotEmpty()) groupNameThroughCode else groupNameValue, nameValue.value, rolesValue.value, emailValue.value, passwordValue.value, signUpNavController)
//        }
//    }
//}

@Composable
fun EnterInfo() {
    val name = remember { mutableStateOf("") }

    val emailDialog = remember { mutableStateOf(false) }
    val email = remember { mutableStateOf("") }
    val enableEmail = remember { mutableStateOf(true) }
    val checkEmail = remember { mutableStateOf(false) }
    val emailTest = remember { mutableIntStateOf(5) }

    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val invitationCode = remember { mutableStateOf("") }
    val groupName = remember { mutableStateOf("") }
    val role = remember { mutableStateOf("") }
    Column {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .weight(1f)
            .padding(horizontal = 10.dp, vertical = 20.dp)) {
            NonCheckColumn(
                mean = stringResource(id = R.string.name),
                tf = name,
                keyBordOption = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = VisualTransformation.None,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            LaunchedEffect(emailDialog.value) {
                emailTest.intValue = isEmailValid(email.value)
                if(emailTest.intValue <= 3) {
                    checkDuplicate(FirebaseAllPath.USER_EMAIL, email.value.replace("@", "_").replace(".", "_"), emailTest, 2, 1)
                }
            }
            CheckColumn(
                mean = stringResource(id = R.string.email),
                tf = email,
                isEnabled = enableEmail.value,
                visualTransformation = VisualTransformation.None,
                keyBordOption = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .weight(1f),
                buttonText = stringResource(id = R.string.check_duplicate)
            ) { if(email.value.isNotEmpty()) emailDialog.value = true }

            val text = when (emailTest.intValue) {
                1 -> stringResource(id = R.string.sign_up_email_valid)
                2, 3 -> stringResource(id = R.string.sign_up_email_valid_but_duplicate)
                4 -> stringResource(id = R.string.sign_up_email_invalid)
                else -> ""
            }
            if(emailDialog.value) {
                CheckDialog(
                    content = text,
                    dismissRequest = { checkEmail.value = false },
                    onDismiss = {
                        TextComposable(
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.labelMedium.copy(Color.Red),
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.clickable {
                                email.value = ""
                                emailDialog.value = false
                                checkEmail.value = false
                            }
                        )},
                    onConfirm = {
                        if(emailTest.intValue == 1) {
                            TextComposable(
                                text = stringResource(id = R.string.check),
                                style = MaterialTheme.typography.labelMedium.copy(Color.Black),
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.clickable {
                                    checkEmail.value = false
                                    emailDialog.value = false
                                    enableEmail.value = false
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonCheckColumn(mean: String, tf: MutableState<String>, keyBordOption: KeyboardOptions, visualTransformation: VisualTransformation, modifier: Modifier) {
    Column {
        WhatMean(mean = mean, essential = true)
        TextField(
            value = tf.value,
            onValueChange = { tf.value = it },
            placeholder = { TextFieldPlaceholderOrSupporting(
                isPlaceholder = true,
                text = "$mean ${stringResource(id = R.string.sign_up_placeholder)}",
                correct = true
            )},
            textStyle = textSetting(true),
            keyboardOptions = keyBordOption,
            visualTransformation = visualTransformation,
            colors = textFieldColors(color = Color.Blue.copy(0.2f)),
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckColumn(mean: String, tf: MutableState<String>, isEnabled: Boolean, visualTransformation: VisualTransformation, keyBordOption: KeyboardOptions, modifier: Modifier, buttonText: String, onClick: () -> Unit) {
    Column {
        WhatMean(mean = mean, essential = true)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = tf.value,
                onValueChange = { tf.value = it },
                textStyle = textSetting(isEnabled),
                visualTransformation =  visualTransformation,
                keyboardOptions = keyBordOption,
                placeholder = {
                    TextFieldPlaceholderOrSupporting(
                        isPlaceholder = true,
                        text = "$mean ${stringResource(id = R.string.sign_up_placeholder)}",
                        correct = true
                    )
                },
                modifier = modifier,
                enabled = isEnabled,
                colors = textFieldColors(color = Color.Blue.copy(0.2f))
            )
            Button(onClick = { onClick() }) {
                TextComposable(
                    text = buttonText,
                    style = MaterialTheme.typography.labelMedium.copy(Color.Black),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun CheckDialog(content: String, dismissRequest: () -> Unit, onDismiss: @Composable () -> Unit, onConfirm: @Composable () -> Unit) {
    AlertDialog(
        onDismissRequest = { dismissRequest() },
        text = {
            TextComposable(
                text = content,
                style = MaterialTheme.typography.labelMedium.copy(Color.Black),
                fontWeight = FontWeight.Normal,
                modifier = Modifier
            )
        },
        containerColor = Color.White,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        confirmButton = { onConfirm() },
        dismissButton = { onDismiss() },
    )
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