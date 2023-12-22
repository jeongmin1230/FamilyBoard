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
    val userReference = FirebaseDatabase.getInstance().reference.child("email")
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

fun checkGroupName(groupName: String, isDuplicate: MutableState<Int>) {
    val groupNameReference = FirebaseDatabase.getInstance().reference.child("group_name")
    groupNameReference.orderByKey().equalTo(groupName)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupNameDuplicate = snapshot.exists()
                if(groupName.isNotEmpty()) isDuplicate.value = -1
                if(groupNameDuplicate) isDuplicate.value = 1
                else isDuplicate.value = 2
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
}

fun generateDB(path: String, groupName: String, email: String, name: String, roles: String) {
    val ref = FirebaseDatabase.getInstance().getReference(path)
    ref.child("email").setValue(email)
    ref.child("group_name").setValue(groupName)
    ref.child("name").setValue(name)
    ref.child("roles").setValue(roles)

}

private fun createDatabase(groupName: String, email: String, name: String, roles: String) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val emailRef = FirebaseDatabase.getInstance().getReference("email")
    val groupNameRef = FirebaseDatabase.getInstance().getReference("group_name")
    emailRef.child(email.replace("@", "_").replace(".", "_")).setValue("email")
    groupNameRef.child(groupName).setValue(groupName)
    generateDB("user/$uid", groupName, email, name, roles)
    generateDB("$groupName/composition/$uid", groupName, email, name, roles)
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