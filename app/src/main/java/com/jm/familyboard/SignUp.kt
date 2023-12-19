package com.jm.familyboard

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

@Composable
fun EnterInfo(context: Context, signUpNavController: NavHostController) {
    val nameValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    val passwordConfirmValue = remember { mutableStateOf("") }
    val groupNameValue = remember { mutableStateOf("") }
    val rolesValue = remember { mutableStateOf("") }
    val routeValue = remember { mutableStateOf("") }
    Column {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 30.dp)
        ) {
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_name),
                essential = true,
                tfValue = nameValue,
                visualTransformation = VisualTransformation.None
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_email),
                essential = true,
                tfValue = emailValue,
                visualTransformation = VisualTransformation.None
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_password),
                essential = true,
                tfValue = passwordValue,
                visualTransformation = PasswordVisualTransformation('*'),
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_confirm_password),
                essential = true,
                tfValue = passwordConfirmValue,
                visualTransformation = PasswordVisualTransformation('*'),
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_group_name),
                essential = true,
                tfValue = groupNameValue,
                visualTransformation = VisualTransformation.None
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_roles),
                essential = true,
                tfValue = rolesValue,
                visualTransformation = VisualTransformation.None
            )
            EnterInfoColumn(
                mean = stringResource(id = R.string.sign_up_route),
                essential = false,
                tfValue = routeValue,
                visualTransformation = VisualTransformation.None
            )
        }
        val condition = nameValue.value.trim().isNotEmpty()
                && emailValue.value.trim().isNotEmpty()
                && passwordValue.value.trim().isNotEmpty()
                && passwordConfirmValue.value.trim().isNotEmpty()
                && groupNameValue.value.trim().isNotEmpty()
                && rolesValue.value.trim().isNotEmpty()
                && (passwordValue.value == passwordConfirmValue.value)

        CompleteButton(condition, Modifier.fillMaxWidth()) {
            signUp(context, groupNameValue.value, nameValue.value, rolesValue.value, emailValue.value, passwordValue.value, signUpNavController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterInfoColumn(mean: String, essential: Boolean, tfValue: MutableState<String>, visualTransformation: VisualTransformation) {
    Column(modifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxSize()) {
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
        TextField(
            value = tfValue.value,
            onValueChange = { tfValue.value = it},
            textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
            placeholder = {
                Text(
                    text = "$mean ${stringResource(id = R.string.sign_up_placeholder)}",
                    style = MaterialTheme.typography.bodySmall.copy(Color.DarkGray)
            )},
            interactionSource = MutableInteractionSource(),
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxSize(),
            supportingText = {},
            colors = textFieldColors(Color.Blue.copy(0.2f))
        )
    }
}

@Composable
fun CompleteButton(isEnable: Boolean, modifier: Modifier, onClickButton: () -> Unit) {
    Button(
        enabled = isEnable,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(0.5f), disabledContainerColor = Color.LightGray),
        onClick = onClickButton,
        shape = RectangleShape,
        modifier = modifier.height(48.dp),
    ) {
        Text(
            text = stringResource(id = R.string.sign_up),
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun DoneSignUp(context: Context, loginNavController: NavHostController) {
    Column {
        CompleteButton(isEnable = true, modifier = Modifier) {
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