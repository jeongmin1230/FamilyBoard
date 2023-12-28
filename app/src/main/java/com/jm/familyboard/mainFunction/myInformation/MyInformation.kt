package com.jm.familyboard.mainFunction.myInformation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.CompleteButton
import com.jm.familyboard.Login
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.ConfirmDialog
import com.jm.familyboard.reusable.ConfirmPasswordSupportingText
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.HowToUseColumn
import com.jm.familyboard.reusable.InvitationCode
import com.jm.familyboard.reusable.LookUpRepresentativeUid
import com.jm.familyboard.reusable.NewPasswordSupportingText
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.getStoredUserPassword
import com.jm.familyboard.reusable.removeUserCredentials
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun MyInformationScreen(mainNavController: NavHostController) {
    val representativeUid = remember { mutableStateOf("") }
    LookUpRepresentativeUid(representativeUid)
    val context = LocalContext.current
    val myInformationArray = stringArrayResource(id = R.array.my_information_nav)
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = myInformationArray[1]) {
        composable(myInformationArray[1]) {
            Column(modifier = Modifier
                .background(Color(0XFFECD5E3))
                .fillMaxSize()) {
                AppBar(true, myInformationArray[0], R.drawable.ic_my_information, { currentNavController.navigate(myInformationArray[3])}) { mainNavController.popBackStack() }
                InfoLayout(representativeUid)
            }
        }
        composable(myInformationArray[3]) {
            Column(modifier = Modifier
                .background(Color(0XFFECD5E3))
                .fillMaxSize()) {
                AppBar(false, myInformationArray[2], null, {}) { currentNavController.popBackStack() }
                CheckPassword(context) {
                    currentNavController.navigate((myInformationArray[5]))
                }
            }
        }
        composable(myInformationArray[5]) {
            Column(modifier = Modifier
                .background(Color(0XFFECD5E3))
                .fillMaxSize()) {
                AppBar(false, myInformationArray[4], null, {}) { currentNavController.popBackStack() }
                EditInformation(context, currentNavController)
            }
        }
    }
}

@Composable
fun InfoLayout(representativeUid: MutableState<String>) {
    val context = LocalContext.current
    val invitationCode = remember { mutableStateOf("") }
    InvitationCode(invitationCode)
    val confirmLogout = remember { mutableStateOf(false) }
    val confirmWithdrawal = remember { mutableStateOf(false) }
    invitationCode.value = invitationCode.value.ifEmpty { stringResource(id = R.string.please_set_representative) }
    HowToUseColumn(text = stringResource(id = R.string.my_information_information))
    Column(Modifier.verticalScroll(rememberScrollState())) {
        RowLayout(click = false, mean = stringResource(id = R.string.my_name), info = User.name)
        RowLayout(click = false, mean = stringResource(id = R.string.my_email), info = User.email)
        RowLayout(click = false, mean = stringResource(id = R.string.my_group_name), info = User.groupName)
        RowLayout(click = false, mean = stringResource(id = R.string.my_roles), info = User.roles)
        if(User.uid == representativeUid.value) {
            RowLayout(click = invitationCode.value.isNotEmpty(), mean = stringResource(id = R.string.my_invitation_code), info = invitationCode.value)
        }
        Spacer(modifier = Modifier.height(30.dp))
        ConfirmText(false, confirmText = stringResource(id = R.string.logout)) { confirmLogout.value = true }
        ConfirmText(true, confirmText = stringResource(id = R.string.withdrawal)) { confirmWithdrawal.value = true }
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

@Composable
fun EditInformation(context: Context, currentNavController: NavHostController) {
    val editName = remember { mutableStateOf(User.name) }
    val editRoles = remember { mutableStateOf(User.roles) }
    val isSelect = remember { mutableStateOf(false) }
    val rolesList = mutableListOf(stringResource(id = R.string.sign_up_father), stringResource(id = R.string.sign_up_mother), stringResource(id = R.string.sign_up_children), stringResource(id = R.string.sign_up_etc))
    val editNewPassword = remember { mutableStateOf("") }
    val editConfirmNewPassword = remember { mutableStateOf("") }
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
                tfValue = editConfirmNewPassword,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier.fillMaxWidth()
            ) { ConfirmPasswordSupportingText(editNewPassword.value, editConfirmNewPassword.value) }
            Spacer(modifier = Modifier.height(10.dp))
            WhatMean(mean = stringResource(id = R.string.my_roles), essential = true)
            rolesList.forEach { role ->
                isSelect.value = role == editRoles.value
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 10.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { editRoles.value = role }
                ) {
                    RadioButton(
                        selected = isSelect.value,
                        onClick = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    TextComposable(
                        text = role,
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }
        CompleteButton(
            isEnable = if(editName.value.isNotEmpty() || (editNewPassword.value.isNotEmpty() == editConfirmNewPassword.value.isNotEmpty())) editNewPassword.value == editConfirmNewPassword.value else false,
            text = stringResource(id = R.string.my_information_edit_complete),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()) {
            val groupCompositionRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/composition/${User.uid}")
            groupCompositionRef.child(context.getString(R.string.database_name)).setValue(editName.value)
            groupCompositionRef.child(context.getString(R.string.database_roles)).setValue(editRoles.value)
            val userRef = FirebaseDatabase.getInstance().getReference("real/user/real_user/${User.uid}")
            userRef.child(context.getString(R.string.database_name)).setValue(editName.value)
            userRef.child(context.getString(R.string.database_roles)).setValue(editRoles.value)
            if(editNewPassword.value.isNotEmpty() && editConfirmNewPassword.value.isNotEmpty()) {
                FirebaseAuth.getInstance().currentUser?.updatePassword(editNewPassword.value)
                    ?.addOnCompleteListener {
                        if(it.isSuccessful) {
                            currentNavController.popBackStack()
                            Toast.makeText(context, context.getString(R.string.change_password_please_login_again), Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, Login::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "${it.exception}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}

@Composable
fun Logout(context: Context, confirmLogout: MutableState<Boolean>) {
    ConfirmDialog(
        screenType = 2,
        onDismiss = { confirmLogout.value = false },
        content = stringResource(id = R.string.do_logout),
        confirmAction = {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            removeUserCredentials(context)
            User.deleteInfo()
            Toast.makeText(context, context.getString(R.string.done_logout), Toast.LENGTH_SHORT).show()
            context.startActivity(intent)
        }
    )
}

@Composable
fun Withdrawal(context: Context, confirmWithdrawal: MutableState<Boolean>) {
    ConfirmDialog(
        screenType = 2,
        onDismiss = { confirmWithdrawal.value = false },
        content = stringResource(id = R.string.do_withdrawal),
        confirmAction = {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val deleteMember = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/composition").child(User.uid)
            val userUid = FirebaseDatabase.getInstance().getReference("real/user/real_user/${User.uid}")
            val announcementRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/announcement")
            val qaRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a")
            announcementRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(childSnapshot in snapshot.children) {
                        val registrant = childSnapshot.child(context.getString(R.string.database_registrant)).getValue(String::class.java)
                        if(registrant == User.uid) childSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
            qaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(childSnapshot in snapshot.children) {
                        val registrant = childSnapshot.child(context.getString(R.string.database_registrant)).child(context.getString(R.string.database_uid)).getValue(String::class.java)
                        if(registrant == User.uid) childSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
            FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}").child(context.getString(R.string.family_representative)).removeValue()
            FirebaseDatabase.getInstance().getReference("real/user/email").child(User.email.replace("@", "_".replace(".","_"))).removeValue()
            deleteMember.removeValue()
            userUid.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, context.getString(R.string.done_withdrawal), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    println(exception.message)
                }
            currentUser?.delete()?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val intent = Intent(context, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    User.deleteInfo()
                    context.startActivity(intent)
                }
            }
        }
    )
}

@Composable
fun RowLayout(click: Boolean, mean: String, info: String) {
    var copiedToClipboard by remember { mutableStateOf(false) }
    val clipboardManager = getSystemService(LocalContext.current, ClipboardManager::class.java)

    Row(modifier = Modifier
        .padding(all = 10.dp)
        .fillMaxSize()) {
        TextComposable(
            text = mean,
            style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
            modifier = Modifier.padding(end = 20.dp)
        )
        TextComposable(
            text = info,
            style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            modifier = Modifier
                .clickable {
                    if(click) {
                    copiedToClipboard = try {
                        clipboardManager?.setPrimaryClip(ClipData.newPlainText("Label", info))
                        true
                    } catch (e: Exception) {
                        false
                    }
                    }
                }
        )
    }
}

@Composable
fun ConfirmText(highLight: Boolean, confirmText: String, onClick: () -> Unit) {
    Text(
        text = confirmText,
        style = MaterialTheme.typography.bodySmall.copy(if(highLight) Color.Red else Color.DarkGray),
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick() }
            .padding(start = 10.dp, bottom = 20.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun MyInformationPreview() {
    FamilyBoardTheme {
        MyInformationScreen(rememberNavController())
    }
}