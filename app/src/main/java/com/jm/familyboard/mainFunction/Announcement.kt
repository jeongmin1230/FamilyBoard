package com.jm.familyboard.mainFunction

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jm.familyboard.R
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun AnnouncementScreen() {
    Column {
        Text(stringResource(id = R.string.announcement))
    }
}

@Preview(showSystemUi = true)
@Composable
fun AnnouncementPreview() {
    FamilyBoardTheme {
        AnnouncementScreen()
    }
}