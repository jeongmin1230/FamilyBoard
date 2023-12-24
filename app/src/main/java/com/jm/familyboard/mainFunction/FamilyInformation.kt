package com.jm.familyboard.mainFunction

import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.CompleteButton
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.ConfirmDialog
import com.jm.familyboard.reusable.EachLayout
import com.jm.familyboard.reusable.LookUpRepresentativeUid
import com.jm.familyboard.reusable.signUp
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun FamilyInformationScreen(mainNavController: NavHostController) {
    val screenName = stringResource(id = R.string.family_information)
    val uid = remember { mutableStateOf("") }
    LookUpRepresentativeUid(uid)
    val context = LocalContext.current
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = context.getString(R.string.family_nav_route_1)) {
        composable(context.getString(R.string.family_nav_route_1)) {
            Column(modifier = Modifier
                .background(Color(0XFFFEE1E8))
                .fillMaxSize()) {
                AppBar(screenName, R.drawable.ic_family_information, { currentNavController.navigate(context.getString(R.string.family_nav_route_2)) }) { mainNavController.popBackStack() }
                GetFamilyInfo(context)
            }
        }
        composable(context.getString(R.string.family_nav_route_2)) {
            Column(modifier = Modifier
                .background(Color(0XFFFEE1E8))
                .fillMaxSize()) {
                AppBar(screenName, null, {}) { currentNavController.popBackStack() }
                RepresentativeSelection(currentNavController, context)
            }
        }
    }
}

@Composable
fun GetFamilyInfo(context: Context) {
    var familyInfoList by remember { mutableStateOf(emptyList<FamilyInfo>()) }

    val database = FirebaseDatabase.getInstance()
    val compositionRef = database.getReference("service/${User.groupName}/composition")

    DisposableEffect(compositionRef) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val infoList = mutableListOf<FamilyInfo>()
                for (childSnapshot in snapshot.children) {
                    val email = childSnapshot.child(context.getString(R.string.database_email)).getValue(String::class.java) ?: ""
                    val roles = childSnapshot.child(context.getString(R.string.database_roles)).getValue(String::class.java) ?: ""
                    val uid = childSnapshot.key ?: ""
                    val name = childSnapshot.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val familyInfo = FamilyInfo(uid, email, name, roles)
                    infoList.add(familyInfo)
                }
                familyInfoList = infoList
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        compositionRef.addValueEventListener(valueEventListener)

        onDispose {
            compositionRef.removeEventListener(valueEventListener)
        }
    }

    familyInfoList.forEach { info ->
        EachLayout(Color(0xFFD59DAB), info.name, if(info.email == User.email) stringResource(id = R.string.me) else info.roles) {}
    }
}

@Composable
fun RepresentativeSelection(currentNavController: NavHostController, context: Context) {
    val uid = remember { mutableStateOf("") }
    val enabled = remember { mutableStateOf(false) }
    LookUpRepresentativeUid(uid)
    var familyInfoList by remember { mutableStateOf(emptyList<FamilyInfo>()) }

    val database = FirebaseDatabase.getInstance()
    val compositionRef = database.getReference("service/${User.groupName}/composition")

    DisposableEffect(compositionRef) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val infoList = mutableListOf<FamilyInfo>()
                for (childSnapshot in snapshot.children) {
                    println(childSnapshot.key)
                    uid.value = childSnapshot.key ?: ""
                    val email = childSnapshot.child(context.getString(R.string.database_email)).getValue(String::class.java) ?: ""
                    val roles = childSnapshot.child(context.getString(R.string.database_roles)).getValue(String::class.java) ?: ""
                    val name = childSnapshot.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val familyInfo = FamilyInfo(uid.value, email, name, roles)
                    infoList.add(familyInfo)
                }
                familyInfoList = infoList
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        compositionRef.addValueEventListener(valueEventListener)

        onDispose {
            compositionRef.removeEventListener(valueEventListener)
        }
    }
    Column {
        Column(Modifier.weight(1f)) {
            val selectedOption = remember { mutableStateOf(uid) }
            val isSelect = remember { mutableStateOf(false) }

            enabled.value = selectedOption.value.value.isNotEmpty()

            Spacer(modifier = Modifier.height(20.dp))
            familyInfoList.forEachIndexed { index, info ->
                isSelect.value = info.uid.trim() == selectedOption.value.value.trim()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(if (index % 2 == 0) Color(0xFFD59DAB) else Color.White)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 20.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { selectedOption.value.value = info.uid }
                ) {
                    RadioButton(
                        selected = isSelect.value,
                        onClick = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = info.name,
                        style = MaterialTheme.typography.bodyMedium.copy(if(index % 2 == 0) Color.White else Color.Black),
                    )
                }
            }
        }
        CompleteButton(
            isEnable = true,
            color = Color(0xFFD59DAB),
            text = stringResource(id = R.string.selection),
            modifier = Modifier.fillMaxWidth()) {
            val uidReference = FirebaseDatabase.getInstance().getReference("service/${User.groupName}")
            uidReference.child("representative").setValue(uid.value)
            currentNavController.popBackStack()
        }
    }
}

data class FamilyInfo(val uid: String, val email: String, val name: String, val roles: String)

@Preview(showSystemUi = true)
@Composable
fun FamilyInformationPreview() {
    FamilyBoardTheme {
        FamilyInformationScreen(rememberNavController())
    }
}