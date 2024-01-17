package com.jm.familyboard

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.jm.familyboard.mainFunction.announcement.AnnouncementScreen
import com.jm.familyboard.mainFunction.familyInformation.FamilyInformationScreen
import com.jm.familyboard.mainFunction.myInformation.MyInformationScreen
import com.jm.familyboard.reusable.EachMainMenuLayout
import com.jm.familyboard.ui.theme.FamilyBoardTheme
import com.jm.familyboard.mainFunction.qa.Q_AScreen

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
    val newVersion = remember { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(true) }

    AppUpdateCheck(navController = navController, root = mainStringArray[0], newVersion = newVersion, context = context)
    NavHost(navController, startDestination = mainStringArray[0]) {
        composable(mainStringArray[0]) {
            if(newVersion.value) {
                // alert dialog 나오게 수정
                // 현재 버전 체크 반대로 코드 넣어놓음..
            } else  {
                Column {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EachMainMenuLayout(
                                text = stringResource(R.string.announcement),
                                animation = R.raw.announcement,
                                bgColor = Color(0xFFC6DBDA),
                                route = stringResource(R.string.announcement),
                                navController = navController)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            EachMainMenuLayout(
                                text = stringResource(R.string.family_information),
                                animation = R.raw.family_information,
                                bgColor = Color(0XFFFEE1E8),
                                route = stringResource(R.string.family_information),
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
                                route = stringResource(R.string.my_information),
                                navController = navController)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            EachMainMenuLayout(
                                text = stringResource(R.string.q_a),
                                animation = R.raw.q_a,
                                bgColor = Color(0XFFF6EAC2),
                                route = stringResource(R.string.q_a),
                                navController = navController)
                        }
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

@Composable
fun AppUpdateCheck(navController: NavHostController, root: String, newVersion: MutableState<Boolean>, context: Context) {
    val appUpdateManager = AppUpdateManagerFactory.create(context)
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            println(appUpdateInfo.updateAvailability())
            println(UpdateAvailability.UPDATE_AVAILABLE)
            println(appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
            println("appUpdate 있음 : ${appUpdateInfo.updateAvailability()}")
            println("현재 버전 : ${BuildConfig.VERSION_NAME}")
            newVersion.value = true
        } else {
            println(appUpdateInfo.updateAvailability())
            println(UpdateAvailability.UPDATE_AVAILABLE)
            println(appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
            println("------------------")
            println(appUpdateInfo.updateAvailability())
            println(appUpdateInfo.packageName())
            println(appUpdateInfo.clientVersionStalenessDays)
            println(appUpdateInfo.clientVersionStalenessDays())
            println("최신 버전임")
            newVersion.value = false
            navController.navigate(root)

        }
    }
        .addOnFailureListener { fail ->
            println("실패.. ${fail.message}")
        }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    FamilyBoardTheme {
        MainScreen()
    }
}