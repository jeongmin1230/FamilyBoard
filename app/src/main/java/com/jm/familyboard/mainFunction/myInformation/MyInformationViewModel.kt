package com.jm.familyboard.mainFunction.myInformation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.Login
import com.jm.familyboard.MainActivity
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.FirebaseAllPath
import com.jm.familyboard.reusable.removeUserCredentials

class MyInformationViewModel: ViewModel() {
    private val infoInGroup = FirebaseAllPath.database.getReference(FirebaseAllPath.SERVICE + User.groupName)
    private val userInfo = FirebaseAllPath.database.getReference(FirebaseAllPath.USER_INFO + User.uid)

    var invitationCode = mutableStateOf("")
    var editName = mutableStateOf(User.name)
    var editRoles = mutableStateOf(User.roles)
    var editNewPassword = mutableStateOf("")
    var editConfirmNewPassword = mutableStateOf("")

    fun findInvitationCode() {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children) {
                    if(User.groupName == childSnapshot.key) {
                        invitationCode.value = childSnapshot.value.toString()
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        FirebaseDatabase.getInstance().getReference(FirebaseAllPath.GROUP_NAME_AND_INVITATION_CODE).addValueEventListener(valueEventListener)
    }

    fun updateInfo(context: Context) {
        val userInfoAddressInGroup = infoInGroup.child("/composition/${User.uid}")
        userInfoAddressInGroup.child(context.getString(R.string.database_name)).setValue(editName.value)
        userInfoAddressInGroup.child(context.getString(R.string.database_roles)).setValue(editRoles.value)
        userInfo.child(context.getString(R.string.database_name)).setValue(editName.value)
        userInfo.child(context.getString(R.string.database_roles)).setValue(editRoles.value)
        if(editNewPassword.value.isNotEmpty() && editConfirmNewPassword.value.isNotEmpty() && editNewPassword.value.trim() == editConfirmNewPassword.value.trim()) {
            FirebaseAllPath.currentUser?.updatePassword(editNewPassword.value)
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(context, context.getString(R.string.change_password_please_login_again), Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "${it.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, context.getString(R.string.my_information_edit_success), Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
        User.name = editName.value
        User.roles = editRoles.value
    }

    fun logout(context: Context) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        removeUserCredentials(context)
        User.deleteInfo()
        Toast.makeText(context, context.getString(R.string.done_logout), Toast.LENGTH_SHORT).show()
        context.startActivity(intent)
    }

    fun withdrawal(context: Context) {
        infoInGroup.child("/composition").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child(User.uid).ref.removeValue()
                if (snapshot.childrenCount < 1) {
                    FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}").ref.removeValue()
                    FirebaseDatabase.getInstance().getReference("real/group_name_and_invitation_code").child(User.groupName).ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        infoInGroup.child("/announcement").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children) {
                    val writerUid = childSnapshot.child(context.getString(R.string.database_writer_uid)).getValue(String::class.java) ?: ""
                    if(User.uid == writerUid) {
                        infoInGroup.child("${User.groupName}/announcement/${childSnapshot.key}").removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        infoInGroup.child("/q_a").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children) {
                    val writerRef = childSnapshot.child(context.getString(R.string.database_writer))
                    val writerUid = writerRef.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""
                    val answerContentUid = childSnapshot.child(context.getString(R.string.database_answer_content)).child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""
                    if(answerContentUid == User.uid) {
                        infoInGroup.child("/q_a").child("${childSnapshot.key}").child(context.getString(R.string.database_flag)).setValue(false)
                        childSnapshot.child(context.getString(R.string.database_answer_content)).ref.removeValue()
                    }
                    if(writerUid == User.uid) {
                        infoInGroup.child("/q_a").child("${childSnapshot.key}").removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        if(User.uid == User.representativeUid) {
            FirebaseDatabase.getInstance().getReference(FirebaseAllPath.SERVICE + User.groupName).child(context.getString(R.string.family_representative)).removeValue()
        }
        FirebaseDatabase.getInstance().getReference(FirebaseAllPath.USER_EMAIL).child(User.email.replace("@", "_").replace(".","_")).removeValue()
        userInfo.removeValue()
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
            }
        }
    }
}