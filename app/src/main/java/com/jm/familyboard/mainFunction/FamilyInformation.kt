package com.jm.familyboard.mainFunction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.R
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun FamilyInformationScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize()) {
        AppBar(screenName = stringResource(id = R.string.family_information)) {
            navController.popBackStack()
        }
        Text(stringResource(id = R.string.family_information))
    }
}

@Preview(showSystemUi = true)
@Composable
fun FamilyInformationPreview() {
    FamilyBoardTheme {
        FamilyInformationScreen(rememberNavController())
    }
}