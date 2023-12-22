package com.jm.familyboard

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.reusable.Loading
import com.jm.familyboard.reusable.getStoredUserEmail
import com.jm.familyboard.reusable.getStoredUserPassword
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(userEmail.value.isNotEmpty() && userPassword.value.isNotEmpty()) {
                    loading.value = true
                    Loading(loading)
                    loginUser(context as Activity, navController, userEmail, userPassword, loading)
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
                placeholder = {
                    Text(text = stringResource(id = R.string.enter_email),
                        style = MaterialTheme.typography.bodyMedium)
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
                placeholder = {
                    Text(text = stringResource(id = R.string.enter_password),
                        style = MaterialTheme.typography.bodyMedium)
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
                Text(text = stringResource(id = R.string.login))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(id = R.string.sign_up),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { navController.navigate(context.getString(R.string.title_activity_sign_up)) },
                    style = MaterialTheme.typography.bodyMedium.copy(Color.Black, textAlign = TextAlign.Center))
            }
        }
        Box(Modifier.align(Alignment.Center)) {
            Loading(loading)
        }
    }
}

private fun loginUser(activity: Activity, navController: NavHostController, email: MutableState<String>, password: MutableState<String>, loading: MutableState<Boolean>) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email.value.trim(), password.value.trim())
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                getUserData(activity, uid, password.value.trim(), navController, loading)
            } else {
                loading.value = false
                email.value = ""
                password.value = ""
                if(!task.isSuccessful) {
                    Toast.makeText(activity, activity.getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                }
            }
        }
}

private fun getUserData(activity: Activity, uid: String, password: String, navController: NavHostController, loading: MutableState<Boolean>) {
    val userGroupNameComposition = FirebaseDatabase.getInstance().getReference("user/$uid")
    val userEmailRef = userGroupNameComposition.child("email")
    val userNameRef = userGroupNameComposition.child("name")
    val userGroupNameRef = userGroupNameComposition.child("group_name")
    val userRolesRef = userGroupNameComposition.child("roles")

    userEmailRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val email = snapshot.getValue(String::class.java)
            if (email != null) {
                User.email = email
                userNameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.getValue(String::class.java)
                        if (name != null) {
                            User.name = name
                            userGroupNameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val groupName = snapshot.getValue(String::class.java)
                                    if(groupName != null) {
                                        User.groupName = groupName
                                        userRolesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val roles = snapshot.getValue(String::class.java)
                                                if(roles != null) {
                                                    User.roles = roles
                                                    storeUserCredentials(activity, name, email, password, groupName, roles)
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                            }
                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })
                            if (User.email.isNotEmpty() && User.name.isNotEmpty()) {
                                loading.value = false
                                navController.navigate((activity as Context).getString(R.string.title_activity_main))
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    })
}

private fun storeUserCredentials(activity: Activity, name: String, email: String, password: String, groupName: String, roles: String) {
    val sharedPreferences = activity.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("name", name)
    editor.putString("email", email)
    editor.putString("password", password)
    editor.putString("groupName", groupName)
    editor.putString("roles", roles)
    editor.apply()
}