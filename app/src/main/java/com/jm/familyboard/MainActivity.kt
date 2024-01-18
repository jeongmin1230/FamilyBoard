package com.jm.familyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.mainFunction.announcement.AnnouncementScreen
import com.jm.familyboard.mainFunction.familyInformation.FamilyInformationScreen
import com.jm.familyboard.mainFunction.myInformation.MyInformationScreen
import com.jm.familyboard.reusable.EachMainMenuLayout
import com.jm.familyboard.ui.theme.FamilyBoardTheme
import com.jm.familyboard.mainFunction.qa.Q_AScreen
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.remoteConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyBoardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val mainStringArray = stringArrayResource(id = R.array.main_nav)
    val navController = rememberNavController()
    val newVersion = remember { mutableStateOf("") }

    remoteConfig(newVersion)

    NavHost(navController, startDestination = mainStringArray[0]) {
        composable(mainStringArray[0]) {
            Column {
                if(newVersion.value.isNotEmpty()) {
                    if (newVersion.value.split(".")[0] != BuildConfig.VERSION_NAME.split(".")[0]) {
                        AlertDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                TextComposable(
                                    text = stringResource(id = R.string.update_now),
                                    style = MaterialTheme.typography.labelMedium.copy(Color.Blue),
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.clickable { openPlayStore(context) }
                                )
                            },
                            dismissButton = {
                                TextComposable(
                                    text = stringResource(id = R.string.update_later),
                                    style = MaterialTheme.typography.labelMedium.copy(Color.DarkGray),
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .clickable { (context as Activity).finishAffinity() }
                                )
                            },
                            text = {
                                TextComposable(
                                    text = stringResource(id = R.string.update_available_please_use_after트_update),
                                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                )
                            },
                            containerColor = Color.White,
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        EachMainMenuLayout(
                            text = stringResource(R.string.announcement),
                            animation = R.raw.announcement,
                            bgColor = Color(0xFFC6DBDA),
                            route = mainStringArray[1],
                            navController = navController)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        EachMainMenuLayout(
                            text = stringResource(R.string.family_information),
                            animation = R.raw.family_information,
                            bgColor = Color(0XFFFEE1E8),
                            route = mainStringArray[2],
                            navController = navController)
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        EachMainMenuLayout(
                            text = stringResource(R.string.my_information),
                            animation = R.raw.my_information,
                            bgColor = Color(0XFFECD5E3),
                            route =  mainStringArray[3],
                            navController = navController)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        EachMainMenuLayout(
                            text = stringResource(R.string.q_a),
                            animation = R.raw.q_a,
                            bgColor = Color(0XFFF6EAC2),
                            route =  mainStringArray[4],
                            navController = navController)
                    }
                }
            }
        }
        composable(mainStringArray[1]) {
            AnnouncementScreen(navController)
        }
        composable(mainStringArray[2]) {
            FamilyInformationScreen(navController)
        }
        composable(mainStringArray[3]) {
            MyInformationScreen(navController)
        }
        composable(mainStringArray[4]) {
            Q_AScreen(navController)
        }
    }
}

fun openPlayStore(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
        startActivity(context, intent, null)
    } catch (e: android.content.ActivityNotFoundException) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
        startActivity(context, intent, null)
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    FamilyBoardTheme {
        MainScreen()
    }
}