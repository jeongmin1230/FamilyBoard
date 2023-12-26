package com.jm.familyboard.mainFunction

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.CompleteButton
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.LoadList
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.EnterInfoMultiColumn
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.HowToUseColumn
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.ui.theme.FamilyBoardTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AnnouncementScreen(mainNavController: NavHostController) {
    val screenName = stringResource(R.string.announcement)
    val context = LocalContext.current
    val registerTitle = remember { mutableStateOf("") }
    val registerContent = remember { mutableStateOf("") }
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = stringResource(R.string.announcement_nav_route_1)) {
        composable(context.getString(R.string.announcement_nav_route_1)) {
            Column(modifier = Modifier
                .background(Color(0xFFC6DBDA))
                .fillMaxSize()) {
                AppBar(true, screenName, R.drawable.ic_announcement, {
                    registerTitle.value = ""
                    registerContent.value = ""
                    currentNavController.navigate("${context.getString(R.string.announcement_nav_route_2)}/false/0")}) { mainNavController.popBackStack() }
                GetAnnouncement(context, registerTitle, registerContent, currentNavController)
            }
        }

        composable("${context.getString(R.string.announcement_nav_route_2)}/{edit}/{registerNo}",
            arguments = listOf(navArgument("edit") { type = NavType.BoolType }, navArgument("registerNo") { type = NavType.IntType })
        ) {
            val edit = it.arguments?.getBoolean("edit") ?: false
            val registerNo = it.arguments?.getInt("registerNo") ?: 0
            Column(modifier = Modifier
                .background(Color(0xFFC6DBDA))
                .fillMaxSize()) {
                AppBar(false, screenName, null, {}) { currentNavController.popBackStack() }
                RegisterNotice(edit, registerNo, context, registerTitle, registerContent, currentNavController)
            }
        }
    }
}


@Composable
fun GetAnnouncement(context: Context, editTitle: MutableState<String>, editContent: MutableState<String>, currentNavController: NavHostController) {
    var announcements by remember { mutableStateOf(emptyList<Announcement>()) }
    val database = FirebaseDatabase.getInstance()
    val announcementReference = database.getReference("real/service/${User.groupName}/announcement")

    DisposableEffect(announcementReference) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcementList = mutableListOf<Announcement>()

                for (childSnapshot in snapshot.children) {
                    val no = childSnapshot.child(context.getString(R.string.database_no)).getValue(Int::class.java) ?: 0
                    val title = childSnapshot.child(context.getString(R.string.database_title)).getValue(String::class.java) ?: ""
                    val content = childSnapshot.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val date = childSnapshot.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                    val writer = childSnapshot.child(context.getString(R.string.database_writer)).getValue(String::class.java) ?: ""

                    val announcement = Announcement(no, title, content, date, writer)
                    editTitle.value = title
                    editContent.value = content
                    announcementList.add(announcement)
                }

                announcements = announcementList
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        announcementReference.addValueEventListener(valueEventListener)

        onDispose {
            announcementReference.removeEventListener(valueEventListener)
        }
    }
    HowToUseColumn(stringResource(id = R.string.announcement_information))
    if(announcements.isEmpty()) {
        TextComposable(
            text = "등록 된 공지 사항이 없습니다.",
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
            modifier = Modifier
            )
    } else {
        Spacer(modifier = Modifier.height(20.dp))
        announcements.forEach { announcement ->
            LoadList(type = 0, false, editTitle, editContent, announcement.title, announcement.content, "", "") {
                currentNavController.navigate("${context.getString(R.string.announcement_nav_route_2)}/true/${announcement.no}")
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RegisterNotice(edit: Boolean, no: Int, context: Context, editTitle: MutableState<String>, editContent: MutableState<String>, currentNavController: NavHostController) {
    val newTitle = remember { mutableStateOf("") }
    val newContent = remember { mutableStateOf("") }
    val writeDate = SimpleDateFormat(stringResource(id = R.string.announcement_date_format)).format(Date(System.currentTimeMillis()))
    val announcementRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/announcement")
    Column {
        Column(Modifier.weight(1f)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EnterInfoSingleColumn(
                    essential = false,
                    mean = stringResource(R.string.title),
                    tfValue = if(editTitle.value.isNotEmpty()) editTitle else newTitle ,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 14.dp)
                ) {}
                Spacer(modifier = Modifier.height(6.dp))
                EnterInfoMultiColumn(
                    mean = stringResource(R.string.content),
                    tfValue = if(editContent.value.isNotEmpty()) editContent else newContent,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 10.dp)
                )
                TextComposable(
                    text = "${stringResource(R.string.date)} $writeDate",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 10.dp)
                )
            }
        }
        CompleteButton(
            isEnable = if(!edit) newTitle.value.isNotEmpty() && newContent.value.isNotEmpty() else editTitle.value.isNotEmpty() && editContent.value.isNotEmpty(),
            text = stringResource(id = R.string.register_notice),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier.fillMaxWidth()) {
            announcementRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nextNo = snapshot.childrenCount + 1
                    val ref = if(edit) announcementRef.child("${context.getString(R.string.database_no)}$no") else announcementRef.child("${context.getString(R.string.database_no)}$nextNo")
                    ref.child(context.getString(R.string.database_content)).setValue(if(edit) editContent.value else newTitle.value)
                    ref.child(context.getString(R.string.database_date)).setValue(writeDate)
                    ref.child(context.getString(R.string.database_no)).setValue(if(edit) no else nextNo)
                    ref.child(context.getString(R.string.database_registrant)).setValue(User.uid)
                    ref.child(context.getString(R.string.database_writer)).setValue(User.name)
                    ref.child(context.getString(R.string.database_title)).setValue(if(edit) editTitle.value else newContent.value)
                    currentNavController.popBackStack()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}

data class Announcement(val no: Int, val title: String, val content: String, val date: String, val writer: String)

@Preview(showSystemUi = true)
@Composable
fun AnnouncementPreview() {
    FamilyBoardTheme {
        AnnouncementScreen(rememberNavController())
    }
}