package com.jm.familyboard.mainFunction

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.CompleteButton
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.EnterInfoMultiColumn
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.ui.theme.FamilyBoardTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AnnouncementScreen(mainNavController: NavHostController) {
    val screenName = stringResource(R.string.announcement)
    val context = LocalContext.current
    val appBarImage = R.drawable.ic_announcement
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = stringResource(R.string.announcement_nav_route_1)) {
        composable(context.getString(R.string.announcement_nav_route_1)) {
            Column(modifier = Modifier
                .background(Color(0xFFC6DBDA))
                .fillMaxSize()) {
                AppBar(screenName, appBarImage, { currentNavController.navigate("announcement_register")}) { mainNavController.popBackStack() }
                GetAnnouncement(context)
            }
        }
        composable("${context.getString(R.string.announcement_nav_route_2)}/{no}",
            arguments = listOf(navArgument("no") { type = NavType.IntType })) {
            val no = it.arguments?.getInt("no") ?: 0
            Column(modifier = Modifier
                .background(Color(0xFFC6DBDA))
                .fillMaxSize()) {
                AppBar(screenName, appBarImage, {}) { currentNavController.popBackStack() }
                Detail(context, no)
            }
        }

        composable(context.getString(R.string.announcement_nav_route_3)) {
            Column(modifier = Modifier
                .background(Color(0xFFC6DBDA))
                .fillMaxSize()) {
                AppBar(screenName, null, {}) { currentNavController.popBackStack() }
                RegisterNotice(context, currentNavController)
            }
        }
    }
}


@Composable
fun GetAnnouncement(context: Context) {
    var announcements by remember { mutableStateOf(emptyList<Announcement>()) }

    val database = FirebaseDatabase.getInstance()
    val announcementReference = database.getReference("service/${User.groupName}/announcement")

    DisposableEffect(announcementReference) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcementList = mutableListOf<Announcement>()

                for (childSnapshot in snapshot.children) {
                    val no = childSnapshot.child(context.getString(R.string.database_no)).getValue(Int::class.java) ?: 0
                    val title = childSnapshot.child(context.getString(R.string.database_title)).getValue(String::class.java) ?: ""
                    val content = childSnapshot.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val date = childSnapshot.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""

                    val announcement = Announcement(no, title, content, date)
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
    Column(Modifier.fillMaxSize()) {
        if(announcements.isEmpty()) {
            Text(text = "등록 된 공지 사항이 없습니다.")
        } else {
            Spacer(modifier = Modifier.height(20.dp))
            announcements.forEach { announcement ->
                AnnouncementList(announcement.title, announcement.content, announcement.date)
            }
        }
    }
}

@Composable
fun AnnouncementList(title: String, detail: String, date: String) {
    val expanded = remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)
        .clickable { expanded.value = !expanded.value }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(Color.Black),
            modifier = Modifier
                .padding(start = 6.dp, top = 6.dp)
                .align(Alignment.Start)
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
            modifier = Modifier
                .padding(bottom = 10.dp, end = 6.dp)
                .align(Alignment.End)
        )
        if(expanded.value) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue.copy(0.2f))) {
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                    modifier = Modifier.padding(all = 10.dp)
                )
            }
        }
        Divider()
    }
}

@Composable
fun Detail(context: Context, no: Int) {
    val detailTitle = remember { mutableStateOf("") }
    val detailContent = remember { mutableStateOf("") }
    val detailDate = remember { mutableStateOf("") }
    var announcements by remember { mutableStateOf(emptyList<Announcement>()) }

    val database = FirebaseDatabase.getInstance()
    val announcementReference = database.getReference("service/${User.groupName}/announcement")

    DisposableEffect(announcementReference) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcementList = mutableListOf<Announcement>()

                for (childSnapshot in snapshot.children) {
                    val currentNo = childSnapshot.child(context.getString(R.string.database_no)).getValue(Int::class.java) ?: 0

                    if (currentNo == no) {
                        val title = childSnapshot.child(context.getString(R.string.database_title)).getValue(String::class.java) ?: ""
                        val content = childSnapshot.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                        val date = childSnapshot.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""

                        detailTitle.value = title
                        detailContent.value = content
                        detailDate.value = date

                        val announcement = Announcement(currentNo, title, content, date)
                        announcementList.add(announcement)
                    }
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
    DetailAndWriteScreen(detailTitle , detailContent, detailDate.value)
}

@Composable
fun DetailAndWriteScreen(title: MutableState<String>, content: MutableState<String>, date: String) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        EnterInfoSingleColumn(
            essential = false,
            mean = stringResource(R.string.title),
            tfValue = title,
            keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
            visualTransformation = VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 14.dp)
        ) {}
        Spacer(modifier = Modifier.height(6.dp))
        EnterInfoMultiColumn(
            mean = stringResource(R.string.content),
            tfValue = content,
            keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
            visualTransformation = VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 10.dp)
        )
        Text(
            text = "${stringResource(R.string.date)} $date",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 10.dp)
        )
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RegisterNotice(context: Context, announcementNavController: NavHostController) {
    val writeTitle = remember { mutableStateOf("") }
    val writeContent = remember { mutableStateOf("") }
    val writeDate = SimpleDateFormat(stringResource(id = R.string.announcement_date_format)).format(Date(System.currentTimeMillis()))
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val announcementRef = FirebaseDatabase.getInstance().getReference("service/${User.groupName}/announcement")
    Column {
        Column(Modifier.weight(1f)) {
            DetailAndWriteScreen(writeTitle, writeContent, writeDate)
        }
        CompleteButton(
            isEnable = writeTitle.value.isNotEmpty() && writeContent.value.isNotEmpty(),
            text = stringResource(id = R.string.register_notice),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier.fillMaxWidth()) {
            announcementRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nextNo = snapshot.childrenCount + 1
                    val newRef = announcementRef.child("${context.getString(R.string.database_no)}$nextNo")
                    newRef.child(context.getString(R.string.database_content)).setValue(writeContent.value)
                    newRef.child(context.getString(R.string.database_date)).setValue(writeDate)
                    newRef.child(context.getString(R.string.database_no)).setValue(nextNo)
                    newRef.child(context.getString(R.string.database_registrant)).setValue(uid)
                    newRef.child(context.getString(R.string.database_title)).setValue(writeTitle.value)
                    announcementNavController.popBackStack()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}

data class Announcement(val no: Int, val title: String, val content: String, val date: String)

@Preview(showSystemUi = true)
@Composable
fun AnnouncementPreview() {
    FamilyBoardTheme {
        AnnouncementScreen(rememberNavController())
    }
}