package com.jm.familyboard.mainFunction.myInformation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.Login
import com.jm.familyboard.MainActivity
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.FirebaseAllPath

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
}