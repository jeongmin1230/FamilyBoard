package com.jm.familyboard

import android.app.Activity
import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.reusable.CompleteButton
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.Loading
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.getStoredUserEmail
import com.jm.familyboard.reusable.getStoredUserPassword
import com.jm.familyboard.reusable.loginUser
import com.jm.familyboard.reusable.textFieldKeyboard
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
            ResetPassword(navController)
        }
    }
}

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
            EnterInfoSingleColumn(
                essential = false,
                mean = stringResource(id = R.string.enter_email),
                tfValue = email,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
                visualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {}
            EnterInfoSingleColumn(
                essential = false,
                mean = stringResource(id = R.string.enter_password),
                tfValue = password,
                keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                visualTransformation = PasswordVisualTransformation('*'),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {}
            Spacer(modifier = Modifier.height(10.dp))
            CompleteButton(
                isEnable = email.value.isNotEmpty() && password.value.isNotEmpty(),
                text = stringResource(id = R.string.login),
                color = Color.Blue.copy(0.2f),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()) {
                loading.value = true
                loginUser(context as Activity, navController, email, password, loading)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                TextComposable(
                    text = stringResource(id = R.string.find_password),
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { navController.navigate(context.getString(R.string.find_password)) }
                        .weight(1f))
                TextComposable(
                    text = stringResource(id = R.string.sign_up),
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { navController.navigate(context.getString(R.string.title_activity_sign_up)) }
                        .weight(1f))
            }
        }
        Box(Modifier.align(Alignment.Center)) {
            Loading(loading)
        }
    }
}

fun findRepresentativeUid(context: Context) {
    val representativeReference = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}")
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val representative = snapshot.child(context.getString(R.string.family_representative))
            if(representative.exists()) {
                User.representativeUid = representative.value.toString()
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    representativeReference.addListenerForSingleValueEvent(valueEventListener)
}
