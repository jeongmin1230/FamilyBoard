package com.jm.familyboard.mainFunction

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.EachLayout
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun FamilyInformationScreen(navController: NavHostController) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize()) {
        AppBar(screenName = stringResource(id = R.string.family_information)) {
            navController.popBackStack()
        }
        GetFamilyInfo(context)
    }
}

@Composable
fun GetFamilyInfo(context: Context) {
    var familyInfoList by remember { mutableStateOf(emptyList<FamilyInfo>()) }

    val database = FirebaseDatabase.getInstance()
    val compositionRef = database.getReference("${User.groupName}/composition")

    DisposableEffect(compositionRef) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcementList = mutableListOf<FamilyInfo>()

                for (childSnapshot in snapshot.children) {
                    val name = childSnapshot.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val roles = childSnapshot.child(context.getString(R.string.database_roles)).getValue(String::class.java) ?: ""

                    val familyInfo = FamilyInfo(name, roles)
                    announcementList.add(familyInfo)
                }

                familyInfoList = announcementList
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
        EachLayout(bigString = info.name, smallString = info.roles) {}
    }
}

data class FamilyInfo(val name: String, val roles: String)

@Preview(showSystemUi = true)
@Composable
fun FamilyInformationPreview() {
    FamilyBoardTheme {
        FamilyInformationScreen(rememberNavController())
    }
}