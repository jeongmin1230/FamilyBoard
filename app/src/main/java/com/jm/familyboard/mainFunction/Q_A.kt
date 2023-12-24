package com.jm.familyboard.mainFunction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.R
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun Q_AScreen(mainNavController: NavHostController) {
    val screenName = stringResource(id = R.string.q_a)
    val context = LocalContext.current
    val appBarImage = R.drawable.ic_q_a
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = context.getString(R.string.q_a_nav_route_1)) {
        composable(context.getString(R.string.q_a_nav_route_1)) {
            Column(modifier = Modifier
                .background(Color(0XFFF6EAC2))
                .fillMaxSize()) {
                AppBar(screenName, appBarImage, { currentNavController.navigate(context.getString(R.string.q_a_nav_route_2))}) { mainNavController.popBackStack() }
                Text(stringResource(id = R.string.q_a))
            }
        }
        composable(context.getString(R.string.q_a_nav_route_2)) {
            Column(modifier = Modifier
                .background(Color(0XFFF6EAC2))
                .fillMaxSize()) {
                AppBar(screenName, null, {}) { currentNavController.popBackStack() }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun Q_APreview() {
    FamilyBoardTheme {
        Q_AScreen(rememberNavController())
    }
}