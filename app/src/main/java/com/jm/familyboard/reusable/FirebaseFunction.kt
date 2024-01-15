package com.jm.familyboard.reusable

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.Login
import com.jm.familyboard.MainActivity
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.findRepresentativeUid

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
    val invitationCodeRef = FirebaseDatabase.getInstance().reference.child("real/group_name_and_invitation_code")
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
    val groupNameRef = FirebaseDatabase.getInstance().getReference("real/group_name_and_invitation_code")
    emailRef.child(email.replace("@", "_").replace(".", "_")).setValue("email")
    checkDuplicate("real/group_name_and_invitation_code", groupNameValue.value, groupNameTest, 1, 2)
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
                if(!FirebaseAllPath.database.getReference(FirebaseAllPath.USER_EMAIL).child(email.replace("@", "_").replace(".","_")).key.isNullOrBlank()) {
                    signUpNavController.navigate(context.getString(R.string.sign_up_nav_route_2))
                }
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
                                                    findRepresentativeUid(activity)
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
                                val intent = Intent((activity as Context), MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                (activity as Context).startActivity(intent)
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
    if(email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()) {
        auth.signInWithEmailAndPassword(email.value.trim(), password.value.trim())
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    getUserData(activity, uid, password.value.trim(), navController, loading)
                } else {
                    loading.value = false
                    email.value = ""
                    password.value = ""
                    if(email.value.isNotEmpty() && password.value.isNotEmpty()) {
                        if(!task.isSuccessful) {
                            println("login fail : ${task.exception}")
                            Toast.makeText(activity, activity.getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
    }
}

fun sendResetPasswordEmail(context: Context, email: String) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(context, context.getString(R.string.send_email), Toast.LENGTH_SHORT).show()
            }
            else {
                println("it.exception ${it.exception}")
            }
        }
}

fun deleteInfo(context: Context) {
    val uid = User.uid
    val infoInGroup = FirebaseAllPath.database.getReference(FirebaseAllPath.SERVICE + User.groupName)
    infoInGroup.child("/announcement").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(childSnapshot in snapshot.children) {
                val writerUid = childSnapshot.child(context.getString(R.string.database_writer_uid)).getValue(String::class.java) ?: ""
                if(User.uid == writerUid) {
                    FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/announcement/${childSnapshot.key}").removeValue()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        }
    })

    infoInGroup.child("/composition").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.child(uid).ref.removeValue()
            if(snapshot.childrenCount < 1) {
                infoInGroup.removeValue()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        }
    })

    infoInGroup.child("/q_a").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(childSnapshot in snapshot.children) {
                val writerUid = childSnapshot.child("${context.getString(R.string.database_writer)}/${context.getString(R.string.database_uid)}").getValue(String::class.java) ?: ""
                if(uid == writerUid) {
                    childSnapshot.ref.removeValue()
                }
                for(answer in childSnapshot.child(context.getString(R.string.database_answer_content)).children) {
                    val answerUid = answer.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""
                    if(uid == answerUid) {
                        answer.ref.removeValue()
                    }
                }

            }
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        }
    })
    if(uid == User.representativeUid) {
        FirebaseDatabase.getInstance().getReference(FirebaseAllPath.SERVICE + User.groupName).child(context.getString(R.string.family_representative)).removeValue()
    }
}

fun logout(context: Context) {
    FirebaseAuth.getInstance().signOut()
    val intent = Intent(context, Login::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    removeUserCredentials(context)
    User.deleteInfo()
    Toast.makeText(context, context.getString(R.string.done_logout), Toast.LENGTH_SHORT).show()
}

fun withdrawal(context: Context) {
    deleteInfo(context)
    FirebaseAllPath.database.getReference(FirebaseAllPath.GROUP_NAME_AND_INVITATION_CODE)
    FirebaseAllPath.database.getReference(FirebaseAllPath.USER_EMAIL).child(User.email.replace("@", "_").replace(".","_")).removeValue()
    FirebaseAllPath.database.getReference(FirebaseAllPath.USER_INFO + User.uid).removeValue()
    FirebaseAllPath.database.getReference(FirebaseAllPath.USER_INFO).removeValue()
        .addOnSuccessListener {
            Toast.makeText(context, context.getString(R.string.done_withdrawal), Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { exception ->
            println(exception.message)
        }
    FirebaseAllPath.currentUser?.delete()?.addOnCompleteListener { task ->
        if(task.isSuccessful) {
            val intent = Intent(context, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            User.deleteInfo()
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "${task.exception}", Toast.LENGTH_SHORT).show()
        }
    }
}