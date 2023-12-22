package com.jm.familyboard.reusable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
fun AppBar(screenName: String, onClickBack: () -> Unit) {
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
                .padding(horizontal = 10.dp)
        )
        Text(screenName)
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
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Black, fontWeight = FontWeight.W400),
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
fun EachLayout(bigString: String, smallString: String, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 10.dp)
        .clickable { onClick() }
        .border(BorderStroke(1.dp, Color.LightGray))) {
        Text(
            text = bigString,
            style = MaterialTheme.typography.bodyLarge.copy(Color.Black),
            modifier = Modifier
                .padding(start = 6.dp, top = 6.dp)
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = smallString,
            style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
            modifier = Modifier
                .padding(bottom = 6.dp, end = 6.dp)
                .align(Alignment.End)
        )
    }
}

@Composable
fun rolesRadioButton(): String {
    val rolesString = listOf(
        stringResource(id = R.string.sign_up_father),
        stringResource(id = R.string.sign_up_mother),
        stringResource(id = R.string.sign_up_children),
        stringResource(id = R.string.sign_up_etc)
    )
    var selectedOption by remember { mutableStateOf(rolesString[0]) }

    Row(
        modifier = Modifier.padding(end = 4.dp),
    ) {
        rolesString.forEach { role ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(interactionSource = MutableInteractionSource(), indication = null) { selectedOption = role }
            ) {
                RadioButton(
                    selected = (role == selectedOption),
                    onClick = null
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
    return selectedOption
}

@Composable
fun ConfirmDialog(onDismiss: () -> Unit, content: String, confirmAction: () -> Unit, dismissAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = content)
            }
        },
        containerColor = Color.White,
        confirmButton = {
            Text(text = stringResource(id = R.string.yes),
                style = MaterialTheme.typography.bodyMedium.copy(Color.Red),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { confirmAction() })
        },
        dismissButton = {
            Text(text = stringResource(id = R.string.not),
                style = MaterialTheme.typography.bodyMedium.copy(Color.DarkGray),
                modifier = Modifier.clickable { dismissAction() })
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

@Composable
fun WhatMean(mean: String, essential: Boolean) {
    Row {
        Text(
            text = mean,
            style = MaterialTheme.typography.bodySmall.copy(Color.DarkGray),
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
        )
        if(essential) {
            Text(
                text = stringResource(id = R.string.sign_up_essential),
                style = MaterialTheme.typography.bodySmall.copy(Color.Red)
            )
        }
    }
}

@Composable
fun TextFieldPlaceholderOrSupporting(isPlaceholder: Boolean, text: String, correct: Boolean) {
    if (isPlaceholder) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(Color.DarkGray)
        )
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (correct) Color.Blue else Color.Red,
                fontWeight = FontWeight.W400
            ),
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

@Preview
@Composable
fun ConfirmDialogPreview() {
    ConfirmDialog({}, "content", {}, {})
}