package com.jm.familyboard

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jm.familyboard.mainFunction.AnnouncementScreen
import com.jm.familyboard.mainFunction.FamilyInformationScreen
import com.jm.familyboard.mainFunction.MyInformationScreen
import com.jm.familyboard.mainFunction.Q_AScreen

@Composable
fun Navigation(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(navController, startDestination = stringResource(id = R.string.main)) {
        composable(context.getString(R.string.main)) {
            MainScreen()
        }
        composable(context.getString(R.string.announcement)) {
            AnnouncementScreen()
        }
        composable(context.getString(R.string.family_information)) {
            FamilyInformationScreen()
        }
        composable(context.getString(R.string.my_information)) {
            MyInformationScreen()
        }
        composable(context.getString(R.string.q_a)) {
            Q_AScreen()
        }
    }
}