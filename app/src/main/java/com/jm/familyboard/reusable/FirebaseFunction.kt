package com.jm.familyboard.reusable

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.R

fun checkEmailDuplicate(email: String, emailTest: MutableState<Int>) {
    val userReference = FirebaseDatabase.getInstance().reference.child("user/email")
    userReference.orderByKey().equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    emailTest.value = 3
                }
                else emailTest.value = 4
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
}

fun checkInvitationCode(invitationCodeTest: MutableState<Int>, invitationCodeValue: String, groupNameValue: MutableState<String>) {
    val invitationCodeRef = FirebaseDatabase.getInstance().reference.child("user/real_user_group_name")
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(childSnapshot in snapshot.children) {
                if(childSnapshot.value == invitationCodeValue) {
                    invitationCodeTest.value = 2
                    groupNameValue.value = childSnapshot.key.toString()
                    println("groupName : ${groupNameValue.value}")
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

fun checkGroupName(groupNameTest: MutableState<Int>, groupName: String){
    val groupNameRef = FirebaseDatabase.getInstance().reference.child("user/real_user_group_name")
    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for(childSnapshot in snapshot.children) {
                if(groupName == childSnapshot.key) {
                    groupNameTest.value = 1
                    break
                }
                else groupNameTest.value = 2
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    groupNameRef.addValueEventListener(valueEventListener)
}

fun generateDB(path: String, groupName: String, email: String, name: String, roles: String) {
    val ref = FirebaseDatabase.getInstance().getReference(path)
    ref.child("email").setValue(email)
    ref.child("group_name").setValue(groupName)
    ref.child("name").setValue(name)
    ref.child("roles").setValue(roles)

}

private fun createDatabase(groupNameValue: MutableState<String>, email: String, name: String, roles: String) {
    println(groupNameValue.value)
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val emailRef = FirebaseDatabase.getInstance().getReference("user/email")
    val groupNameRef = FirebaseDatabase.getInstance().getReference("user/real_user_group_name")
    emailRef.child(email.replace("@", "_").replace(".", "_")).setValue("email")
    groupNameRef.child(groupNameValue.value).child("code").setValue(generateInvitationCode())//.setValue(generateInvitationCode())
    generateDB("user/real_user/$uid", groupNameValue.value, email, name, roles)
    generateDB("service/${groupNameValue.value}/composition/$uid", groupNameValue.value, email, name, roles)
}

fun signUp(context: Context, groupNameValue: MutableState<String>, name: String, roles: String, email: String, password: String, signUpNavController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                createDatabase(groupNameValue, email, name, roles)
                signUpNavController.navigate(context.getString(R.string.sign_up_nav_route_2))
            } else {
                println(task.exception)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "onFailure ${e.message}", Toast.LENGTH_SHORT).show()
        }
}