package com.jm.familyboard

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.TextFieldPlaceholderOrSupporting
import com.jm.familyboard.reusable.WhatMean
import com.jm.familyboard.reusable.textFieldColors
import com.jm.familyboard.ui.theme.FamilyBoardTheme

@Composable
fun SignUpScreen(loginNavController: NavHostController) {
    val context = LocalContext.current
    val screenName = stringResource(id = R.string.sign_up)
    val signUpNavController = rememberNavController()
    NavHost(signUpNavController, startDestination =  context.getString(R.string.sign_up_nav_route_1)) {
        composable(context.getString(R.string.sign_up_nav_route_1)) {
            Column {
                AppBar(screenName) { loginNavController.popBackStack() }
                EnterInfo(context, signUpNavController)
            }
        }
        composable(context.getString(R.string.sign_up_nav_route_2)) {
            Column {
                AppBar(screenName) { signUpNavController.popBackStack() }
                DoneSignUp(context, loginNavController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterInfo(context: Context, signUpNavController: NavHostController) {
    val nameValue = remember { mutableStateOf("") }
    val nameBoolean = remember { mutableStateOf(true) }
    val emailValue = remember { mutableStateOf("") }
    val emailSupporting = remember { mutableStateOf("") }
    val emailTest = remember { mutableIntStateOf(0) }
    val isEmailTFFocused = remember { mutableStateOf(false) }
    // 0 = 공백 -> 필수 항목임 ,2 -> 형식 맞지 않음, 3 -> 형식은 맞음, 4 -> 사용 가능
    val passwordValue = remember { mutableStateOf("") }
    val passwordSupporting = remember { mutableStateOf("") }
    val passwordBoolean = remember { mutableStateOf(false) }
    val passwordConfirmValue = remember { mutableStateOf("") }
    val passwordConfirmSupporting = remember { mutableStateOf("") }
    val passwordConfirmBoolean = remember { mutableStateOf(false) }
    val groupNameValue = remember { mutableStateOf("") }
    val groupNameSupporting = remember { mutableStateOf("") }
    val groupNameBoolean = remember { mutableStateOf(false) }
    val rolesValue = remember { mutableStateOf("") }
    val rolesBoolean = remember { mutableStateOf(false) }
    val routeValue = remember { mutableStateOf("") }
    Column {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 30.dp)
        ) {
            nameBoolean.value = nameValue.value.isNotEmpty()
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_name),
                tfValue = nameValue,
                supportingText = if(nameValue.value.trim().isEmpty()) stringResource(id = R.string.sign_up_essential_text) else "",
                isCorrect = nameBoolean,
                visualTransformation = VisualTransformation.None
            )
            if(!isEmailTFFocused.value && emailValue.value.trim().isNotEmpty()) {
                LaunchedEffect(this) {
                    emailTest.intValue = isEmailValid(emailValue.value)
                    if(emailTest.intValue >= 3) {
                        checkEmailDuplicate(emailValue.value, emailTest)
                    }
                }
            }
            WhatMean(mean = stringResource(id = R.string.sign_up_email), essential = true)
            TextField(
                value = emailValue.value,
                onValueChange = { emailValue.value = it},
                textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                placeholder = { TextFieldPlaceholderOrSupporting(true, "${stringResource(id = R.string.sign_up_email)} ${stringResource(id = R.string.sign_up_placeholder)}",true) },
                visualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .onFocusChanged { isEmailTFFocused.value = it.isFocused }
                    .fillMaxSize(),
                supportingText = {
                    if(emailValue.value.isEmpty()) TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_essential_text), correct = false)
                    if(!isEmailTFFocused.value && emailValue.value.isNotEmpty()) {
                        when(emailTest.intValue) {
                            0 -> { }
                            1 -> { }
                            2 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_invalid), correct = false) }
                            3 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_valid_but_duplicate), correct = false)}
                            4 -> { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = stringResource(id = R.string.sign_up_email_valid), correct = true) }
                        }
                    }

                },
                singleLine = true,
                colors = textFieldColors(Color.Blue.copy(0.2f))
            )
/*            emailBoolean.value = isEmailValid(emailValue.value)
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_email),
                tfValue = emailValue,
                supportingText = stringResource(id = R.string.sign_up_essential_text),
                isCorrect = emailBoolean,
                visualTransformation = VisualTransformation.None
            )*/
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_password),
                tfValue = passwordValue,
                supportingText = stringResource(id = R.string.sign_up_essential_text),
                isCorrect = passwordBoolean,
                visualTransformation = PasswordVisualTransformation('*'),
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_confirm_password),
                tfValue = passwordConfirmValue,
                supportingText = stringResource(id = R.string.sign_up_essential_text),
                isCorrect = passwordConfirmBoolean,
                visualTransformation = PasswordVisualTransformation('*'),
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_group_name),
                tfValue = groupNameValue,
                supportingText = stringResource(id = R.string.sign_up_essential_text),
                isCorrect = groupNameBoolean,
                visualTransformation = VisualTransformation.None
            )
            rolesValue.value.ifEmpty { rolesBoolean.value = !rolesBoolean.value }
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_roles),
                tfValue = rolesValue,
                supportingText = stringResource(id = R.string.sign_up_essential_text),
                isCorrect = rolesBoolean,
                visualTransformation = VisualTransformation.None
            )
            UnrestrictedTextField(mean = stringResource(id = R.string.sign_up_route), essential = false, tfValue = routeValue)
        }
        val condition = nameValue.value.trim().isNotEmpty()
                && emailValue.value.trim().isNotEmpty()
                && passwordValue.value.trim().isNotEmpty()
                && passwordConfirmValue.value.trim().isNotEmpty()
                && groupNameValue.value.trim().isNotEmpty()
                && rolesValue.value.trim().isNotEmpty()
                && (passwordValue.value == passwordConfirmValue.value)

        CompleteButton(isEnable = condition, text = stringResource(id = R.string.sign_up), modifier = Modifier.fillMaxWidth()) {
            signUp(context, groupNameValue.value, nameValue.value, rolesValue.value, emailValue.value, passwordValue.value, signUpNavController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterInfoColumn(mean: String, tfValue: MutableState<String>, supportingText: String, isCorrect: MutableState<Boolean>,visualTransformation: VisualTransformation) {
    Column(modifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxSize()) {
        WhatMean(mean = mean, essential = true)
        TextField(
            value = tfValue.value,
            onValueChange = { tfValue.value = it},
            textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            placeholder = { TextFieldPlaceholderOrSupporting(true, "$mean ${stringResource(id = R.string.sign_up_placeholder)}",true) },
            interactionSource = MutableInteractionSource(),
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxSize(),
            supportingText = { TextFieldPlaceholderOrSupporting(isPlaceholder = false, text = supportingText, correct = isCorrect.value)},
            singleLine = true,
            colors = textFieldColors(Color.Blue.copy(0.2f))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnrestrictedTextField(mean: String, essential: Boolean, tfValue: MutableState<String>) {
    Column(modifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxSize()) {
        WhatMean(mean = mean, essential = essential)
        TextField(
            value = tfValue.value,
            onValueChange = { tfValue.value = it},
            textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            placeholder = { TextFieldPlaceholderOrSupporting(true, "$mean ${stringResource(id = R.string.sign_up_placeholder)}",true) },
            interactionSource = MutableInteractionSource(),
            visualTransformation = VisualTransformation.None,
            modifier = Modifier.fillMaxSize(),
            colors = textFieldColors(Color.Blue.copy(0.2f))
        )
    }
}

@Composable
fun CompleteButton(isEnable: Boolean, text: String, modifier: Modifier, onClickButton: () -> Unit) {
    Button(
        enabled = isEnable,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(0.5f), disabledContainerColor = Color.LightGray),
        onClick = onClickButton,
        shape = RectangleShape,
        modifier = modifier.height(48.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun DoneSignUp(context: Context, loginNavController: NavHostController) {
    Column {
        CompleteButton(isEnable = true, text = stringResource(id = R.string.sign_up_complete), modifier = Modifier) {
            loginNavController.navigate(context.getString(R.string.first))
        }
    }
}

fun signUp(context: Context, groupName: String, name: String, roles: String, email: String, password: String, signUpNavController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                createDatabase(groupName, email, name, roles)
                signUpNavController.navigate(context.getString(R.string.sign_up_nav_route_2))
            } else {
                println(task.exception)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "onFailure ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun createDatabase(groupName: String, email: String, name: String, roles: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val emailRef = FirebaseDatabase.getInstance().getReference("user/email")
    val splitEmail = email.split("@")
    emailRef.child(splitEmail[0]).setValue(splitEmail[1])
    generateDB("user/$uid", email, groupName, name, roles)
    generateDB("$groupName/composition/$uid", email, groupName, name, roles)
}

private fun generateDB(path: String, groupName: String, email: String, name: String, roles: String) {
    val ref = FirebaseDatabase.getInstance().getReference(path)
    ref.child("email").setValue(email)
    ref.child("group_name").setValue(groupName)
    ref.child("name").setValue(name)
    ref.child("roles").setValue(roles)

}

@Preview(showSystemUi = true)
@Composable
fun SignUpPreview() {
    FamilyBoardTheme {
        SignUpScreen(rememberNavController())
    }
}