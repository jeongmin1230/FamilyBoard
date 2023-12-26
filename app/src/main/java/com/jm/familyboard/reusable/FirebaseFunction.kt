package com.jm.familyboard.reusable

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.R
import com.jm.familyboard.User

fun checkDuplicate(path: String, text: String, test: MutableState<Int>, overlapValue: Int, nonOverlap: Int) {
    val reference = FirebaseDatabase.getInstance().reference.child(path)
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(childSnapshot in snapshot.children) {
                if(childSnapshot.key == text) {
                    test.value = overlapValue
                    break
                }
                else test.value = nonOverlap
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    reference.addValueEventListener(valueEventListener)
}

fun checkInvitationCode(invitationCodeTest: MutableState<Int>, invitationCodeValue: String, groupNameValue: MutableState<String>) {
    val invitationCodeRef = FirebaseDatabase.getInstance().reference.child("real/user/real_user_group_name")
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(childSnapshot in snapshot.children) {
                if(childSnapshot.value == invitationCodeValue) {
                    invitationCodeTest.value = 2
                    groupNameValue.value = childSnapshot.key.toString()
                    break
                }
                else invitationCodeTest.value = 1
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    invitationCodeRef.addValueEventListener(valueEventListener)
}

@Composable
fun ReturnValue(path: String, comparisonTarget: String, string: MutableState<String>) {
    val reference = FirebaseDatabase.getInstance().getReference(path)
    LaunchedEffect(reference) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children) {
//                    if(childSnapshot.key)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
    }
}
@Composable
fun LookUpRepresentativeUid(uid: MutableState<String>) {
    val context = LocalContext.current
    val representativeRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}")

    LaunchedEffect(representativeRef) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                uid.value = snapshot.child(context.getString(R.string.family_representative)).value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        representativeRef.addListenerForSingleValueEvent(valueEventListener)
    }
}

@Composable
fun InvitationCode(code: MutableState<String>) {
    val invitationCodeRef = FirebaseDatabase.getInstance().getReference("real/user/real_user_group_name")
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(childSnapshot in snapshot.children) {
                if(User.groupName == childSnapshot.key) {
                    code.value = childSnapshot.value.toString()
                    break
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    invitationCodeRef.addValueEventListener(valueEventListener)
}

fun generateDB(path: String, groupName: String, email: String, name: String, roles: String) {
    val ref = FirebaseDatabase.getInstance().getReference(path)
    ref.child("email").setValue(email)
    ref.child("group_name").setValue(groupName)
    ref.child("name").setValue(name)
    ref.child("roles").setValue(roles)
}

fun registerDatabase(groupNameTest: MutableState<Int>, groupNameValue: MutableState<String>, email: String, name: String, roles: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val emailRef = FirebaseDatabase.getInstance().getReference("real/user/email")
    val groupNameRef = FirebaseDatabase.getInstance().getReference("real/user/real_user_group_name")
    emailRef.child(email.replace("@", "_").replace(".", "_")).setValue("email")
//    checkGroupName(groupNameTest, groupNameValue.value)
    checkDuplicate("real/user/real_user_group_name", groupNameValue.value, groupNameTest, 1, 2)
    if(groupNameTest.value == 2) groupNameRef.child(groupNameValue.value).setValue(generateInvitationCode())
    generateDB("real/user/real_user/$uid", groupNameValue.value, email, name, roles)
    generateDB("real/service/${groupNameValue.value}/composition/$uid", groupNameValue.value, email, name, roles)
}

fun signUp(context: Context, groupNameTest: MutableState<Int>, groupNameValue: MutableState<String>, name: String, roles: String, email: String, password: String, signUpNavController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                registerDatabase(groupNameTest, groupNameValue, email, name, roles)
                signUpNavController.navigate(context.getString(R.string.sign_up_nav_route_2))
            } else {
                println(task.exception)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "onFailure ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

fun getUserData(activity: Activity, uid: String, password: String, navController: NavHostController, loading: MutableState<Boolean>) {
    User.uid = uid
    val userGroupNameComposition = FirebaseDatabase.getInstance().getReference("real/user/real_user/$uid")
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

fun loginUser(activity: Activity, navController: NavHostController, email: MutableState<String>, password: MutableState<String>, loading: MutableState<Boolean>) {
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
                    println("login fail : ${task.exception}")
                    Toast.makeText(activity, activity.getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                }
            }
        }
}