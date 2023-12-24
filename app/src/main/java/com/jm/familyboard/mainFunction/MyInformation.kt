package com.jm.familyboard.mainFunction

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jm.familyboard.Login
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.ConfirmDialog
import com.jm.familyboard.reusable.InvitationCode
import com.jm.familyboard.reusable.LookUpRepresentativeUid
import com.jm.familyboard.reusable.removeUserCredentials
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun MyInformationScreen(mainNavController: NavHostController) {
    val screenName = stringResource(id = R.string.my_information)
    val context = LocalContext.current
    val appBarImage = R.drawable.ic_my_information
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = context.getString(R.string.my_information_nav_route_1)) {
        composable(context.getString(R.string.my_information_nav_route_1)) {
            Column(modifier = Modifier
                .background(Color(0XFFECD5E3))
                .fillMaxSize()) {
                AppBar(screenName,appBarImage, { currentNavController.navigate(context.getString(R.string.my_information_nav_route_2))}) { mainNavController.popBackStack() }
                Spacer(modifier = Modifier.height(30.dp))
                InfoLayout()
            }
        }
        composable(context.getString(R.string.my_information_nav_route_2)) {
            Column(modifier = Modifier
                .background(Color(0XFFECD5E3))
                .fillMaxSize()) {
                AppBar(screenName, null, {}) { currentNavController.popBackStack() }
            }
        }
    }
}

@Composable
fun InfoLayout() {
    val representativeUid = remember { mutableStateOf("") }
    LookUpRepresentativeUid(representativeUid)
    val context = LocalContext.current
    val roles = if(User.roles == "mother") "어머니" else if(User.roles == "father") "아버지" else if(User.roles == "children") "자식" else if(User.roles == "pat") "반려 동물" else "기타"
    val invitationCode = remember { mutableStateOf("") }
    InvitationCode(invitationCode)
    val confirmLogout = remember { mutableStateOf(false) }
    val confirmWithdrawal = remember { mutableStateOf(false) }
    invitationCode.value = invitationCode.value.ifEmpty { stringResource(id = R.string.please_set_representative) }

    println(FirebaseAuth.getInstance().uid)
    Column(Modifier.verticalScroll(rememberScrollState())) {
        RowLayout(click = false, mean = stringResource(id = R.string.my_name), info = User.name)
        RowLayout(click = false, mean = stringResource(id = R.string.my_email), info = User.email)
        RowLayout(click = false, mean = stringResource(id = R.string.my_group_name), info = User.groupName)
        RowLayout(click = false, mean = stringResource(id = R.string.my_roles), info = roles)
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
fun Logout(context: Context, confirmLogout: MutableState<Boolean>) {
    ConfirmDialog(
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
        onDismiss = { confirmWithdrawal.value = false },
        content = stringResource(id = R.string.do_withdrawal),
        confirmAction = {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = FirebaseAuth.getInstance().uid
            val deleteMember = FirebaseDatabase.getInstance().getReference("service/${User.groupName}/composition").child("$uid")
            val userUid = FirebaseDatabase.getInstance().getReference("user/real_user/$uid")
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
        Text(
            text = mean,
            style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
            modifier = Modifier.padding(end = 20.dp)
        )
        Text(
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