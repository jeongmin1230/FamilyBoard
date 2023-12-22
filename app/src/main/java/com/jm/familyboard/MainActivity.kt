package com.jm.familyboard

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.mainFunction.AnnouncementScreen
import com.jm.familyboard.mainFunction.FamilyInformationScreen
import com.jm.familyboard.mainFunction.MyInformationScreen
import com.jm.familyboard.mainFunction.Q_AScreen
import com.jm.familyboard.reusable.EachMainMenuLayout
import com.jm.familyboard.ui.theme.FamilyBoardTheme

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
    val navController = rememberNavController()
    NavHost(navController, startDestination = stringResource(R.string.main)) {
        composable(context.getString(R.string.main)) {
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
        composable(context.getString(R.string.announcement)) {
            AnnouncementScreen(navController)
        }
        composable(context.getString(R.string.family_information)) {
            FamilyInformationScreen(navController)
        }
        composable(context.getString(R.string.my_information)) {
            MyInformationScreen(navController)
        }
        composable(context.getString(R.string.q_a)) {
            Q_AScreen(navController)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    FamilyBoardTheme {
        MainScreen()
    }
}