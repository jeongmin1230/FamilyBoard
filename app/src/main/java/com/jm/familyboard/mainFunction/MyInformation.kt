package com.jm.familyboard.mainFunction

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jm.familyboard.Login
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.ConfirmDialog
import com.jm.familyboard.reusable.removeUserCredentials
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun MyInformationScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize()) {
        AppBar(screenName = stringResource(id = R.string.my_information)) {
            navController.popBackStack()
        }
        Spacer(modifier = Modifier.height(30.dp))
        InfoLayout()
    }
}

@Composable
fun InfoLayout() {
    val context = LocalContext.current
    val roles = if(User.roles == "mother") "어머니" else if(User.roles == "father") "아버지" else if(User.roles == "children") "자식" else if(User.roles == "pat") "반려 동물" else "기타"
    val confirmLogout = remember { mutableStateOf(false) }
    val confirmWithdrawal = remember { mutableStateOf(false) }

    println(FirebaseAuth.getInstance().uid)
    Column(Modifier.verticalScroll(rememberScrollState())) {
        RowLayout(mean = stringResource(id = R.string.my_name), info = User.name)
        RowLayout(mean = stringResource(id = R.string.my_email), info = User.email)
        RowLayout(mean = stringResource(id = R.string.my_group_name), info = User.groupName)
        RowLayout(mean = stringResource(id = R.string.my_roles), info = roles)
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
    ) { confirmLogout.value = false }
}

@Composable
fun Withdrawal(context: Context, confirmWithdrawal: MutableState<Boolean>) {
    ConfirmDialog(
        onDismiss = { confirmWithdrawal.value = false },
        content = stringResource(id = R.string.do_withdrawal),
        confirmAction = {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = FirebaseAuth.getInstance().uid
            val userUid = FirebaseDatabase.getInstance().getReference("user/$uid")
            val deleteMember = FirebaseDatabase.getInstance().getReference("${User.groupName}/composition").child("$uid")
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
    ) { confirmWithdrawal.value = false }
}

@Composable
fun RowLayout(mean: String, info: String) {
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
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ConfirmText(highLight: Boolean, confirmText: String, onClick: () -> Unit) {
    Text(
        text = confirmText,
        style = MaterialTheme.typography.bodySmall.copy(if(highLight) Color.Red else Color.DarkGray),
        modifier = Modifier
            .clickable(interactionSource = MutableInteractionSource(), indication = null) { onClick() }
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