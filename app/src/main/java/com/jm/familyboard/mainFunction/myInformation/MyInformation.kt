package com.jm.familyboard.mainFunction.myInformation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.BuildConfig
import com.jm.familyboard.Login
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.CompleteButton
import com.jm.familyboard.reusable.ConfirmDialog
import com.jm.familyboard.reusable.ConfirmPasswordSupportingText
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.HowToUseColumn
import com.jm.familyboard.reusable.NewPasswordSupportingText
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.TextFieldPlaceholderOrSupporting
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.checkInvitationCode
import com.jm.familyboard.reusable.deleteInfo
import com.jm.familyboard.reusable.generateDB
import com.jm.familyboard.reusable.getStoredUserPassword
import com.jm.familyboard.reusable.logout
import com.jm.familyboard.reusable.notoSansKr
import com.jm.familyboard.reusable.selectRadioButton
import com.jm.familyboard.reusable.textFieldColors
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.reusable.withdrawal

@Composable
fun MyInformationScreen(mainNavController: NavHostController) {
    val myInformationViewModel = MyInformationViewModel()
    if(User.uid == User.representativeUid) LaunchedEffect(true) { myInformationViewModel.findInvitationCode() }
    val context = LocalContext.current
    val invitationCode = remember { myInformationViewModel.invitationCode }

    val myInformationArray = stringArrayResource(id = R.array.my_information_nav)
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = myInformationArray[1]) {
        composable(myInformationArray[1]) {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                AppBar(true, myInformationArray[0], R.drawable.ic_my_information, { currentNavController.navigate(myInformationArray[3])}) { mainNavController.popBackStack() }
                MyInformation(context, invitationCode)
            }
        }
        composable(myInformationArray[3]) {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                AppBar(false, myInformationArray[2], null, {}) { currentNavController.popBackStack() }
                CheckPassword(context) { currentNavController.navigate((myInformationArray[5])) }
            }
        }
        composable(myInformationArray[5]) {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                AppBar(false, myInformationArray[4], null, {}) { currentNavController.popBackStack() }
                EditInformation(context, myInformationViewModel.editName, myInformationViewModel.editRoles, myInformationViewModel.editNewPassword, myInformationViewModel.editConfirmNewPassword)
                { myInformationViewModel.updateInfo(context) }
            }
        }
    }
}

@Composable
fun MyInformation(context: Context, invitationCode: MutableState<String>) {
    val confirmLogout = remember { mutableStateOf(false) }
    val confirmWithdrawal = remember { mutableStateOf(false) }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(10.dp))
        HowToUseColumn(text = stringResource(id = R.string.my_information_information))

        WhatMean(mean = stringResource(id = R.string.app_information), essential = false)
        RowLayout(mean = stringResource(id = R.string.app_version_name), info = BuildConfig.VERSION_NAME) {}

        Divider(Modifier.background(Color.Gray))
        Spacer(modifier = Modifier.height(10.dp))

        WhatMean(mean = stringResource(id = R.string.my_information), essential = false)
        RowLayout(mean = stringResource(id = R.string.my_name), info = User.name){}
        RowLayout(mean = stringResource(id = R.string.my_email), info = User.email){}
        RowLayout(mean = stringResource(id = R.string.my_group_name), info = User.groupName){}
        RowLayout(mean = stringResource(id = R.string.my_roles), info = User.roles){}
        if(User.uid == User.representativeUid) {
            var copiedToClipboard by remember { mutableStateOf(false) }
            val clipboardManager = getSystemService(LocalContext.current, ClipboardManager::class.java)
            RowLayout(mean = stringResource(id = R.string.my_invitation_code), info = invitationCode.value) {
                copiedToClipboard = try {
                    clipboardManager?.setPrimaryClip(ClipData.newPlainText("Label", invitationCode.value))
                    true
                } catch (e: Exception) {
                    false
                }
            }
            WhatMean(mean = stringResource(id = R.string.my_information_for_representative), essential = false)
        }

        Divider(Modifier.background(Color.Gray))
        Spacer(modifier = Modifier.height(10.dp))

        WhatMean(mean = stringResource(id = R.string.account_information), essential = false)
        RowLayout(mean = "", info = stringResource(id = R.string.logout)) { confirmLogout.value = true }
        RowLayout(mean = "", info = stringResource(id = R.string.withdrawal)) { confirmWithdrawal.value = true }
    }
    if(confirmLogout.value) {
        Logout(context, confirmLogout)
    }
    if(confirmWithdrawal.value){
        Withdrawal(context, confirmWithdrawal)
    }
}

@Composable
fun CheckPassword(context: Context, complete: () -> Unit) {
    val editCurrentPassword = remember { mutableStateOf("") }
    Column {
        Column(modifier = Modifier
            .padding(horizontal = 10.dp)
            .weight(1f)) {
            EnterInfoSingleColumn(
                essential = true,
                mean = stringResource(id = R.string.current_password),
                tfValue = editCurrentPassword,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier.fillMaxWidth()
            ) {}
        }
        CompleteButton(
            isEnable = editCurrentPassword.value.isNotEmpty(),
            text = stringResource(id = R.string.my_information_confirm_password),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()) {
            if(editCurrentPassword.value.trim() == getStoredUserPassword(context)) {
                complete()
            }
            else {
                Toast.makeText(context, context.getString(R.string.not_correct_current_password), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInformation(context: Context, editName: MutableState<String>, editRoles: MutableState<String>, editNewPassword: MutableState<String>, editConfirmPassword: MutableState<String>, updateInfo: () -> Unit) {
    val invitationCodeTest = remember { mutableIntStateOf(0) }
    val invitationCodeValue = remember { mutableStateOf("") }
    val invitationCodeEnabled = remember { mutableStateOf(true) }
    val groupNameThroughCode = remember { mutableStateOf("") }
    val check = remember { mutableStateOf(false) }

    Column {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp)
            .weight(1f)) {
            EnterInfoSingleColumn(
                essential = true,
                mean = stringResource(id = R.string.my_name),
                tfValue = editName,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = VisualTransformation.None,
                modifier = Modifier.fillMaxWidth()
            ) {}
            Spacer(modifier = Modifier.height(10.dp))
            EnterInfoSingleColumn(
                essential = true,
                mean = stringResource(id = R.string.sign_up_password),
                tfValue = editNewPassword,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier.fillMaxWidth()
            ) { NewPasswordSupportingText(editNewPassword.value) }
            Spacer(modifier = Modifier.height(10.dp))
            EnterInfoSingleColumn(
                essential = true,
                mean = stringResource(id = R.string.sign_up_confirm_password),
                tfValue = editConfirmPassword,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier.fillMaxWidth()
            ) { ConfirmPasswordSupportingText(editNewPassword.value, editConfirmPassword.value) }
            Spacer(modifier = Modifier.height(10.dp))
            WhatMean(mean = stringResource(id = R.string.my_roles), essential = true)
            editRoles.value = selectRadioButton(stringArrayResource(id = R.array.roles).toList())
            LaunchedEffect(check.value) {
//                        checkDuplicate("user/real_user_group_name", invitationCodeValue.value, invitationCodeTest, 2, 1)
                checkInvitationCode(invitationCodeTest, invitationCodeValue.value, groupNameThroughCode)
            }
            Spacer(modifier = Modifier.height(10.dp))
            WhatMean(mean = stringResource(id = R.string.move_group), essential = false)
            Row {
                TextField(
                    value = invitationCodeValue.value,
                    onValueChange = { invitationCodeValue.value = it},
                    textStyle = TextStyle(
                        fontFamily = notoSansKr,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        color = if(invitationCodeEnabled.value) Color.Black else Color.DarkGray.copy(0.5f)
                    ),
                    enabled = invitationCodeEnabled.value,
                    placeholder = { TextFieldPlaceholderOrSupporting(true, "${stringResource(id = R.string.sign_up_invite_code)} ${stringResource(id = R.string.sign_up_placeholder)}",true) },
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                        .fillMaxSize(),
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    singleLine = true,
                    colors = textFieldColors(Color.Blue.copy(0.2f))
                )
                Button(onClick = { check.value = true }) {
                    TextComposable(
                        text = stringResource(id = R.string.check),
                        style = MaterialTheme.typography.labelMedium.copy(Color.Black),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier)
                }
                if(check.value) {
                    AlertDialog(
                        onDismissRequest = { check.value = false },
                        text = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                when(invitationCodeTest.intValue) {
                                    1 -> {
                                        TextComposable(
                                            text = stringResource(id = R.string.sign_up_not_exist_invite_code),
                                            style = MaterialTheme.typography.labelMedium.copy(Color.Black),
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier
                                        )
                                    }
                                    2 -> {
                                        TextComposable(
                                            text = "[${groupNameThroughCode.value}] ${stringResource(id = R.string.move_group_info)}",
                                            style = MaterialTheme.typography.labelMedium.copy(Color.Black),
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
                        },
                        containerColor = Color.White,
                        confirmButton = {
                            if(invitationCodeTest.intValue == 2) {
                                TextComposable(
                                    text = stringResource(id = R.string.check),
                                    style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.clickable {
                                        deleteInfo(context)
                                        generateDB("real/service/${groupNameThroughCode.value}/composition/${User.uid}", groupNameThroughCode.value, User.email, User.name, User.roles)
                                        generateDB("real/user/real_user/${User.uid}", groupNameThroughCode.value, User.email, User.name, User.roles)
                                        val intent = Intent(context, Login::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)
                                        check.value = false
                                    }
                                )
                            }
                        },
                        dismissButton = {
                            TextComposable(
                                text = stringResource(id = R.string.not),
                                style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.clickable {
                                    invitationCodeEnabled.value = true
                                    check.value = false
                                }
                            )
                        },
                        properties = DialogProperties(usePlatformDefaultWidth = false),
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
        }
        CompleteButton(
            isEnable = if(editNewPassword.value.trim().isNotEmpty() || editConfirmPassword.value.trim().isNotEmpty()) editNewPassword.value == editConfirmPassword.value else true,
            text = stringResource(id = R.string.my_information_edit_complete),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()) { updateInfo() }
    }
}

@Composable
fun Logout(context: Context, confirmLogout: MutableState<Boolean>) {
    ConfirmDialog(
        screenType = 2,
        path = "",
        onDismiss = { confirmLogout.value = false },
        content = stringResource(id = R.string.do_logout),
        confirmAction = { logout(context) }
    )
}

@Composable
fun Withdrawal(context: Context, confirmWithdrawal: MutableState<Boolean>) {
    ConfirmDialog(
        screenType = 2,
        path = "",
        onDismiss = { confirmWithdrawal.value = false },
        content = stringResource(id =  R.string.do_withdrawal),
        confirmAction = { withdrawal(context) }
    )
}

@Composable
fun RowLayout(mean: String, info: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .padding(all = 10.dp)
        .fillMaxSize()) {
        TextComposable(
            text = mean,
            style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
            fontWeight = FontWeight.Normal,
            modifier = if(mean.isNotEmpty()) Modifier.padding(end = 20.dp) else Modifier.padding(0.dp)
        )
        TextComposable(
            text = info,
            style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { onClick() }
        )
    }
}