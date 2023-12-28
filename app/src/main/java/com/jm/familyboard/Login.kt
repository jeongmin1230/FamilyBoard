package com.jm.familyboard

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jm.familyboard.reusable.Loading
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.getStoredUserEmail
import com.jm.familyboard.reusable.getStoredUserPassword
import com.jm.familyboard.reusable.loginUser
import com.jm.familyboard.ui.theme.FamilyBoardTheme

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyBoardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirstScreen()
                }
            }
        }
    }
}

@Composable
fun FirstScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val loading = remember { mutableStateOf(false) }

    val userEmail = remember { mutableStateOf(getStoredUserEmail(context))}
    val userPassword = remember { mutableStateOf(getStoredUserPassword(context))}

    NavHost(navController, startDestination = context.getString(R.string.first)) {
        composable(context.getString(R.string.first)) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(userEmail.value.isNotEmpty() && userPassword.value.isNotEmpty()) {
                    loading.value = true
                    Loading(loading)
                    LaunchedEffect(this) {
                        loginUser(context as Activity, navController, userEmail, userPassword, loading)
                    }
                    loading.value = false
                }
                else {
                    LoginScreen(navController, loading)
                }
            }

        }
        composable(context.getString(R.string.title_activity_sign_up)) {
            SignUpScreen(navController)
        }
        composable(context.getString(R.string.title_activity_main)) {
            MainScreen()
        }
        composable(context.getString(R.string.find_password)) {
            FindIdAndPasswordScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, loading: MutableState<Boolean>) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Spacer(modifier = Modifier.height(40.dp))
    Box {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(200.dp)) {
                LottieAnimation(
                    composition = composition,
                    progress = progress,
                )
            }
            TextField(
                value = email.value,
                singleLine = true,
                onValueChange = {email.value = it},
                textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                placeholder = {
                    Text(text = stringResource(id = R.string.enter_email),
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Gray))
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = password.value,
                singleLine = true,
                onValueChange = {password.value = it},
                textStyle = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                placeholder = {
                    TextComposable(
                        text = stringResource(id = R.string.enter_password),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        modifier = Modifier
                    )
                },
                visualTransformation = PasswordVisualTransformation('*'),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                enabled = email.value.isNotEmpty() && password.value.isNotEmpty(),
                onClick = {
                    loading.value = true
                    loginUser(context as Activity, navController, email, password, loading) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.LightGray,
                )) {
                Text(
                    text = stringResource(id = R.string.login),
                    style = MaterialTheme.typography.bodyMedium.copy(if(email.value.isNotEmpty() && password.value.isNotEmpty()) Color.White else Color.Black)
                    )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                TextComposable(
                    text = stringResource(id = R.string.find_password),
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center),
                    modifier = Modifier
                        .clickable(interactionSource = MutableInteractionSource(), indication = null) { navController.navigate(context.getString(R.string.find_password)) }
                        .weight(1f))
                TextComposable(
                    text = stringResource(id = R.string.sign_up),
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center),
                    modifier = Modifier
                        .clickable(interactionSource = MutableInteractionSource(), indication = null) { navController.navigate(context.getString(R.string.title_activity_sign_up)) }
                        .weight(1f))
            }
        }
        Box(Modifier.align(Alignment.Center)) {
            Loading(loading)
        }
    }
}
