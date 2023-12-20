package com.jm.familyboard

import androidx.compose.runtime.MutableState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun isEmailValid(email: String): Int {
    val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}")
    return if(email.matches(emailPattern)) 3 else if(!email.matches(emailPattern)) 2 else 0
}

fun checkEmailDuplicate(email: String, emailTest: MutableState<Int>) {
    val userReference = FirebaseDatabase.getInstance().reference.child("user/email")
    userReference.orderByKey().equalTo(email.split("@")[0])
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 중복된 이메일이 존재하는지 여부 확인
                val isDuplicate = dataSnapshot.exists()
                if(isDuplicate) {
                    emailTest.value = 3
                }
                else emailTest.value = 4
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
}