package com.jm.familyboard.reusable

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jm.familyboard.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jm.familyboard.User

@Composable
fun Loading(loading: MutableState<Boolean>) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    if(loading.value) {
        Box(modifier = Modifier
            .size(400.dp)
            .background(Color.Transparent)
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
            )
        }
    }
}

@Composable
fun TextComposable(text: String, style: androidx.compose.ui.text.TextStyle, modifier: Modifier) {
    Text(
        text = text,
        style = style,
        modifier = modifier
    )
}

@Composable
fun AppBar(enabled: Boolean, screenName: String, imageButtonSource: Int?, imageFunction: () -> Unit, onClickBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(48.dp)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.back),
            modifier = Modifier
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) { onClickBack() }
                .padding(start = 10.dp)
        )
        if(screenName != stringResource(id = R.string.sign_up)) {
            TextComposable(
                text = screenName,
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
            imageButtonSource?.let { ImageVector.vectorResource(it) }?.let {
                Image(
                    imageVector = it,
                    contentDescription = screenName,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable(enabled = enabled) { imageFunction() }
                )
            }
        }
    }
}

@Composable
fun EachMainMenuLayout(text: String, animation: Int, bgColor: Color, route: String, navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable { navController.navigate(route) }
            .background(bgColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LottieAnimation(
                composition = composition,
                progress = progress)
        }
        TextComposable(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Black, fontWeight = FontWeight.W400),
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
fun HowToUseColumn(text: String) {
    Column(modifier = Modifier
        .background(Color.White)
        .padding(all = 10.dp)
        .fillMaxWidth()) {
        TextComposable(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
            modifier = Modifier
        )
    }
}

/**
 * screenType = 0 -> announcement
 * screenType = 1 -> Q_A
 * */

// screenType 0 에서 flag 는 사용 x
// screenType 1 에서 flag 는 답변 유무에 사용
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllList(screenType: Int, flag: Boolean, modify: MutableState<Boolean>, title: String, content: String, date: String, writer: String, answer: String, writerUid: String, clickAction: () -> Unit) {
    val context = LocalContext.current
    val showDetail = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = { showDetail.value = !showDetail.value },
                onLongClick = {
                    when(screenType) {
                        0 -> {
                            showDialog = if(User.uid == writerUid) { true }
                            else {
                                Toast.makeText(context, context.getString(R.string.announcement_edit_warning), Toast.LENGTH_SHORT).show()
                                false
                            }
                        }
                        1 -> { // q a 화면 이면 답변 있으면 더 추가 못 한다고 토스트
                        }
                    }
                }
            )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)) {
            TextComposable(
                text = date,
                style = MaterialTheme.typography.labelSmall.copy(Color.DarkGray),
                modifier = Modifier.align(Alignment.Start)
            )
            TextComposable(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                modifier = Modifier.align(Alignment.Start)
            )
            TextComposable(
                text = writer,
                style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
    if(showDetail.value) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(if(screenType == 0) Color(0xFFC6DBDA) else Color(0XFFF6EAC2))
            .padding(all = 10.dp)) {
            TextComposable(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                modifier = Modifier
            )
            if(flag) {
                TextComposable(text = answer,
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                    modifier = Modifier)
            }
        }
    }
    Divider(Modifier.border(BorderStroke(1.dp, Color.DarkGray)))

        if(showDialog) {
            when(screenType) {
                0 -> { // announcement
                    ConfirmDialog(screenType = 0, onDismiss = { showDialog = false }, content = title ) {
                        modify.value = true
                        clickAction()
                    }
                }
                1 -> { // q&a
                    when(flag) {
                        true -> {
                            Toast.makeText(context, context.getString(R.string.q_a_answer_Warning), Toast.LENGTH_SHORT).show()
                        }
                        false -> {
                            ConfirmDialog(screenType = 1, onDismiss = { showDialog = false }, content = title) { clickAction() }
                        }
                    }
                }

            }
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadList(screenType: Int, flag: MutableState<Boolean>, editTitle: MutableState<String>, editContent: MutableState<String>, title: String, content: String, writer: String, answer: String, clickAction: () -> Unit) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)
        .combinedClickable(
            onClick = { expanded.value = !expanded.value },
            onLongClick = {
                // 작성자가 현재 로그인 한 사용자 와 일치 할 경우 에만 수정 가능
                if (writer == User.name && screenType == 0) {
                    flag.value = true
                    editTitle.value = title
                    editContent.value = content
                    showDialog = true
                } else if (writer != User.name && screenType == 0) {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.announcement_edit_warning),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    editTitle.value = title
                    editContent.value = content
                    showDialog = true
                }
            }
        )
    ) {
        val text = if(screenType != 0) {
            when(flag.value) {
                true -> {
                    stringResource(id = R.string.complete_answer)
                }
                false -> {
                    stringResource(id = R.string.waiting_for_answer)
                }
            }
        } else ""

        TextComposable(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(if(flag.value) Color.Blue else Color.DarkGray),
            modifier = Modifier.padding(start = 6.dp, end = 6.dp)
        )
        TextComposable(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            modifier = Modifier
                .padding(start = 6.dp)
                .align(Alignment.Start)
        )
        TextComposable(
            text = writer,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
            modifier = Modifier
                .padding(bottom = 10.dp, end = 6.dp)
                .align(Alignment.End)
        )
        if(expanded.value) {
            Column(modifier = Modifier
                .fillMaxWidth()) {
                TextComposable(
                    text = if(screenType == 0) "[${stringResource(id = R.string.announcement)}]" else "[${stringResource(id = R.string.database_question_content_mean)}]",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Black),
                    modifier = Modifier.padding(all = 10.dp)
                )
                TextComposable(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    modifier = Modifier.padding(all = 10.dp)
                )
            }
            if(flag.value) {
                Column(modifier = Modifier
                    .fillMaxWidth()) {
                    TextComposable(
                        text = "[${stringResource(id = R.string.database_answer_mean)}]",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Black),
                        modifier = Modifier.padding(all = 10.dp)
                    )
                    TextComposable(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                        modifier = Modifier.padding(all = 10.dp)
                    )
                }
            }
        }
        if(showDialog) {
            when(flag.value) {
                true -> {
                    Toast.makeText(context, context.getString(R.string.q_a_answer_Warning), Toast.LENGTH_SHORT).show()
                }
                false -> {
                    ConfirmDialog(screenType = screenType, onDismiss = { showDialog = false }, content = title) { clickAction() }
                }
            }
        }
        Divider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterInfoSingleColumn(essential: Boolean, mean: String, tfValue: MutableState<String>, keyboardOptions: KeyboardOptions, visualTransformation: VisualTransformation, modifier: Modifier, supportingText: @Composable () -> Unit) {
    Column(modifier = modifier) {
        WhatMean(mean = mean, essential = essential)
        TextField(
            value = tfValue.value,
            onValueChange = { tfValue.value = it},
            textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            placeholder = { TextFieldPlaceholderOrSupporting(isPlaceholder = true, text = "$mean ${stringResource(id = R.string.sign_up_placeholder)}", correct = true)},
            interactionSource = MutableInteractionSource(),
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            supportingText = { supportingText() },
            singleLine = true,
            colors = textFieldColors(Color.Blue.copy(0.2f))
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterInfoMultiColumn(mean: String, enabled: Boolean, tfValue: MutableState<String>, keyboardOptions: KeyboardOptions, visualTransformation: VisualTransformation, modifier: Modifier) {
    Column(modifier = Modifier
        .padding(bottom = 14.dp)
        .fillMaxSize()) {
        WhatMean(mean = mean, essential = false)
        TextField(
            value = tfValue.value,
            onValueChange = { tfValue.value = it},
            textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            placeholder = { TextFieldPlaceholderOrSupporting(isPlaceholder = true, text = "$mean ${stringResource(id = R.string.sign_up_placeholder)}", correct = true)},
            interactionSource = MutableInteractionSource(),
            visualTransformation = visualTransformation,
            modifier = modifier,
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            colors = textFieldColors(Color.Blue.copy(0.2f))
        )
    }
}

@Composable
fun selectRadioButton(string: List<String>): String {
    var selectedOption by remember { mutableStateOf(string[0]) }

    Row{
        string.forEach { role ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(interactionSource = MutableInteractionSource(), indication = null) { selectedOption = role }
            ) {
                RadioButton(
                    selected = (role == selectedOption), // role 있으면 거기에 없으면 처음 거에
                    onClick = null
                )
                TextComposable(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                )
            }
        }
    }
    return selectedOption
}


/**
 * announcement 화면 이고 수정 = 0
 * announcement 화면 아니고 수정 = 1
 * * 그 외 = 2
 * */

@Composable
fun ConfirmDialog(screenType: Int, onDismiss: () -> Unit, content: String, confirmAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextComposable(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                    modifier = Modifier
                )
            }
        },
        containerColor = Color.White,
        confirmButton = {
            when(screenType){
                0 -> {
                    TextComposable(
                        text = stringResource(id = R.string.edit),
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .fillMaxWidth()
                            .clickable { confirmAction() })
                }
                1 -> {
                    TextComposable(
                        text = stringResource(id = R.string.answer),
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .fillMaxWidth()
                            .clickable { confirmAction() })
                }
                2 -> {
                    TextComposable(text = stringResource(id = R.string.yes),
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable { confirmAction() })
                }
            }
        },
        dismissButton = {
            if(screenType == 2) {
                TextComposable(text = stringResource(id = R.string.not),
                    style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
                    modifier = Modifier.clickable { onDismiss() })
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

@Composable
fun WhatMean(mean: String, essential: Boolean) {
    Row {
        TextComposable(
            text = mean,
            style = MaterialTheme.typography.bodySmall.copy(Color.DarkGray),
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
        )
        if(essential) {
            TextComposable(
                text = stringResource(id = R.string.sign_up_essential),
                style = MaterialTheme.typography.bodySmall.copy(Color.Red),
                modifier = Modifier
            )
        }
    }
}

@Composable
fun TextFieldPlaceholderOrSupporting(isPlaceholder: Boolean, text: String, correct: Boolean) {
    if (isPlaceholder) {
        TextComposable(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray),
            modifier = Modifier
        )
    } else {
        TextComposable(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = if (correct) Color.Blue else Color.Red, fontWeight = FontWeight.W400),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldColors(color: Color): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        containerColor = color,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
}

@Composable
fun textFieldKeyboard(imeAction: ImeAction, keyboardType: KeyboardType): KeyboardOptions {
    return KeyboardOptions(
        imeAction = imeAction,
        keyboardType = keyboardType
    )
}

@Preview
@Composable
fun EachLayoutPreview() {
    EachMainMenuLayout("메세지", R.raw.announcement, Color(0XFFC6DBDA), "", rememberNavController())
}