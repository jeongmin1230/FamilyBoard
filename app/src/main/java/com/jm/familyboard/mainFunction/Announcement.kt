package com.jm.familyboard.mainFunction

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun AnnouncementScreen(mainNavController: NavHostController) {
    val screenName = stringResource(R.string.announcement)
    val context = LocalContext.current
    val announcementNavController = rememberNavController()
    NavHost(announcementNavController, startDestination = stringResource(R.string.announcement_nav_route_1)) {
        composable(context.getString(R.string.announcement_nav_route_1)) {
            Column {
                AppBar(screenName) { mainNavController.popBackStack() }
                GetAnnouncement(context, announcementNavController)
            }
        }
        composable("${context.getString(R.string.announcement_nav_route_2)}/{no}",
            arguments = listOf(navArgument("no") { type = NavType.IntType })) {
            val no = it.arguments?.getInt("no") ?: 0
            Column {
                AppBar(screenName) { announcementNavController.popBackStack() }
                Detail(context, no)
            }
        }
    }
}


@Composable
fun GetAnnouncement(context: Context, announcementNavController: NavHostController) {
    var announcements by remember { mutableStateOf(emptyList<Announcement>()) }

    val database = FirebaseDatabase.getInstance()
    val announcementReference = database.getReference("${User.groupName}/announcement")

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

    announcements.forEach { announcement ->
        EachLayout(announcement.title, announcement.date) {
            announcementNavController.navigate("${context.getString(R.string.announcement_nav_route_2)}/${announcement.no}")
        }
    }
}

@Composable
fun EachLayout(title: String, date: String, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 4.dp)
        .clickable { onClick() }
        .border(BorderStroke(1.dp, Color.LightGray))) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(Color.Black),
            modifier = Modifier
                .padding(start = 4.dp)
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
            modifier = Modifier
                .padding(end = 4.dp)
                .align(Alignment.End)
        )
    }
}

@Composable
fun Detail(context: Context, no: Int) {
    var detailTitle by remember { mutableStateOf("") }
    var detailContent by remember { mutableStateOf("") }
    var detailDate by remember { mutableStateOf("") }
    var announcements by remember { mutableStateOf(emptyList<Announcement>()) }

    val database = FirebaseDatabase.getInstance()
    val announcementReference = database.getReference("${User.groupName}/announcement")

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

                        detailTitle = title
                        detailContent = content
                        detailDate = date

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

    Column {
        Text(
            text = "${stringResource(R.string.no)} $no",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 10.dp)
            )
        Text(
            text = stringResource(R.string.title),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .background(Color.Blue.copy(0.2f))
        ) {
            Text(
                text = detailTitle,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(all = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.content),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Blue.copy(0.2f))
        ) {
            Text(
                text = detailContent,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(all = 10.dp)
            )
        }
        Text(
            text = "${stringResource(R.string.date)} $detailDate",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 10.dp)
        )
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