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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jm.familyboard.User

val notoSansKr = FontFamily(
    Font(R.font.notosanskr_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.notosanskr_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.notosanskr_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.notosanskr_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.notosanskr_thin, FontWeight.Thin, FontStyle.Normal)
)

@Composable
fun Loading(loading: MutableState<Boolean>) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    if(loading.value) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
            )
        }
    }
}

@Composable
fun TextComposable(text: String, style: TextStyle, fontWeight: FontWeight, modifier: Modifier) {
    Text(
        text = text,
        style = style.merge(
            TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        ),
        fontWeight = fontWeight,
        fontFamily = notoSansKr,
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
        TextComposable(
            text = screenName,
            style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        )
        if(screenName != stringResource(id = R.string.sign_up)) {
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
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
            fontWeight = FontWeight.Normal,
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
            fontWeight = FontWeight.Normal,
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllList(screenType: Int, flag: Boolean, modify: MutableState<Boolean>, no: Int, title: String, content: String, date: String, writer: String, answer: String, writerUid: String, clickAction: () -> Unit) {
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
                        1 -> {
                            showDialog = true/*if(flag) {
                                Toast.makeText(context, context.getString(R.string.q_a_answer_Warning), Toast.LENGTH_SHORT).show()
                                false
                            }
                            else { true }*/
                        }
                    }
                }
            )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)) {
            val text = when(flag) {
                true -> {
                    stringResource(id = R.string.complete_answer)
                }
                false -> {
                    stringResource(id = R.string.waiting_for_answer)
                }
            }
            TextComposable(
                text = if(screenType == 0) date else text,
                style = MaterialTheme.typography.labelSmall.copy(if(flag && screenType == 1) Color.Blue else if(!flag && screenType == 1) Color.Red else Color.DarkGray),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )
            TextComposable(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )
            TextComposable(
                text = writer,
                style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
    if(showDetail.value) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(if (screenType == 0) Color(0xFFC6DBDA) else Color(0XFFF6EAC2))
            .padding(all = 10.dp)) {
            TextComposable(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                fontWeight = FontWeight.Normal,
                modifier = Modifier
            )
            if(flag) {
                TextComposable(
                    text = "[${stringResource(id = R.string.database_answer_mean)}]",
                    style = MaterialTheme.typography.labelSmall.copy(Color.DarkGray),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 10.dp)
                )
                TextComposable(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
    Divider(Modifier.border(BorderStroke(1.dp, Color.DarkGray)))

        if(showDialog) {
            when(screenType) {
                0 -> {
                    ConfirmDialog(screenType = 0, onDismiss = { showDialog = false }, content = title ) {
                        modify.value = true
                        clickAction()
                    }
                }
                1 -> {
                    ConfirmDialog(screenType = 1, onDismiss = { showDialog = false }, content = title) {
                        clickAction()
                    }
                }
            }
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
            textStyle = TextStyle(
                fontFamily = notoSansKr,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
                color = Color.Black
            )/*MaterialTheme.typography.bodyMedium.copy(fontFamily = notoSansKr, color = Color.Black)*/,
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
            textStyle = TextStyle(
                fontFamily = notoSansKr,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
                color = Color.Black
            ),
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
    var selectedOption by remember { mutableStateOf(User.roles.ifEmpty { string[0] }) }
    Column {
        string.forEach { role ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { selectedOption = role }
            ) {
                RadioButton(
                    selected = (role == selectedOption), // role 있으면 거기에 없으면 처음 거에
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Blue.copy(0.2f),
                        unselectedColor = Color.LightGray,
                        disabledSelectedColor = Color.LightGray,
                        disabledUnselectedColor = Color.LightGray
                    )
                )
                TextComposable(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
        }
    }
    return selectedOption
}

@Composable
fun CompleteButton(isEnable: Boolean, color: Color, text: String, modifier: Modifier, onClickButton: () -> Unit) {
    Button(
        enabled = isEnable,
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = Color.LightGray),
        onClick = onClickButton,
        shape = RectangleShape,
        modifier = modifier.height(48.dp),
    ) {
        TextComposable(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black, textAlign = TextAlign.Center),
            fontWeight = FontWeight.Normal,
            modifier = Modifier
        )
    }
}

@Composable
fun ConfirmDialog(screenType: Int, onDismiss: () -> Unit, content: String, confirmAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextComposable(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                    fontWeight = FontWeight.Normal,
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
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .fillMaxWidth()
                            .clickable { confirmAction() })
                }
                1 -> {
                    TextComposable(
                        text = stringResource(id = R.string.answer),
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .fillMaxWidth()
                            .clickable { confirmAction() })
                }
                2 -> {
                    TextComposable(text = stringResource(id = R.string.yes),
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable { confirmAction() })
                }
            }
        },
        dismissButton = {
            if(screenType == 2) {
                TextComposable(
                    text = stringResource(id = R.string.not),
                    style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable { onDismiss() }
                )
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
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
        )
        if(essential) {
            TextComposable(
                text = stringResource(id = R.string.sign_up_essential),
                style = MaterialTheme.typography.bodySmall.copy(Color.Red),
                fontWeight = FontWeight.Normal,
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
            fontWeight = FontWeight.Normal,
            modifier = Modifier
        )
    } else {
        TextComposable(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = if (correct) Color.Blue else Color.Red, fontWeight = FontWeight.W400),
            fontWeight = FontWeight.Normal,
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