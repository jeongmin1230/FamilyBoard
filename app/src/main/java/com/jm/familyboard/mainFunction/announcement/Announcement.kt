package com.jm.familyboard.mainFunction.announcement

import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.R
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.CompleteButton
import com.jm.familyboard.reusable.EnterInfoMultiColumn
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.HowToUseColumn
import com.jm.familyboard.reusable.ItemLayout
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.date
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.reusable.today

@SuppressLint("UnrememberedMutableState")
@Composable
fun AnnouncementScreen(mainNavController: NavHostController) {
    val context = LocalContext.current
    val announcementViewModel = AnnouncementViewModel()
    val currentNavController = rememberNavController()
    val announcementList = remember { announcementViewModel.announcements }
    val announcementArray = stringArrayResource(id = R.array.announcement_nav)

    NavHost(currentNavController, startDestination = announcementArray[1]) {
        composable(announcementArray[1]) {
            Column(modifier = Modifier
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()) {
                AppBar(true, announcementArray[0], R.drawable.ic_announcement, {
                    announcementViewModel.vmWriteDate.value = today(context)
                    announcementViewModel.init()
                    currentNavController.navigate(announcementArray[3])}) { mainNavController.popBackStack()
                }
                LaunchedEffect(announcementList) { announcementViewModel.loadData(context) }

                Spacer(modifier = Modifier.height(20.dp))
                HowToUseColumn(text = stringResource(id = R.string.announcement_information))

                if(announcementList.value.isEmpty()) {
                    TextComposable(
                        text = stringResource(id = R.string.empty_screen),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                } else {
                    announcementList.value.forEach { announcement ->
                        ItemLayout(
                            screenType = 0,
                            date = announcement.date,
                            title = announcement.title,
                            content = announcement.content,
                            commentNum = 0L,
                            writer = announcement.writer,
                            onShortClick = {},
                            onLongClick = {
                                announcementViewModel.vmModify.value = true
                                announcementViewModel.vmTitle.value = announcement.title
                                announcementViewModel.vmContent.value = announcement.content
                                announcementViewModel.vmWriteDate.value = announcement.date
                                currentNavController.navigate(announcementArray[3])}
                        )
                    }
                }
            }
        }

        composable(announcementArray[3]) {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                AppBar(false, announcementArray[2], null, {}) { currentNavController.popBackStack() }
                RegisterNotice(announcementViewModel.vmModify.value, announcementViewModel.vmTitle, announcementViewModel.vmContent, date(announcementViewModel.vmModify.value, today(context), announcementViewModel.vmWriteDate.value)) {
                    announcementViewModel.writeDB(context, currentNavController)
                }
            }
        }
    }
}

@Composable
fun RegisterNotice(modify: Boolean, vmTitle: MutableState<String>, vmContent: MutableState<String>, vmWriteDate: List<String>, writeDB: () -> Unit) {
    val buttonText = stringResource(id = R.string.announcement) + if(modify) { stringResource(id = R.string.edit) } else { stringResource(id = R.string.register) }
    Column {
        Column(Modifier.weight(1f)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EnterInfoSingleColumn(
                    essential = false,
                    mean = stringResource(R.string.title),
                    tfValue = vmTitle ,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 14.dp)
                ) {}
                Spacer(modifier = Modifier.height(6.dp))
                EnterInfoMultiColumn(
                    mean = stringResource(R.string.content),
                    tfValue = vmContent,
                    enabled = true,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Default, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 10.dp)
                )
                TextComposable(
                    text = vmWriteDate[1],
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 10.dp)
                )
            }
        }
        CompleteButton(
            isEnable = vmTitle.value.isNotEmpty() && vmContent.value.isNotEmpty(),
            text = buttonText,
            color = Color.Blue.copy(0.2f),
            modifier = Modifier.fillMaxWidth()) { writeDB() }
    }
}