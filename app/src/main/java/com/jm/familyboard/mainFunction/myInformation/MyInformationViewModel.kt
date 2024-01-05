package com.jm.familyboard.mainFunction.myInformation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.Login
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.removeUserCredentials

class MyInformationViewModel: ViewModel() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val deleteMember = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/composition").child(User.uid)
    private val userUidRef = FirebaseDatabase.getInstance().getReference("real/user/real_user/${User.uid}")
    private val announcementRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/announcement")
    private val qaRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a")

    var invitationCode = mutableStateOf("")
    var editName = mutableStateOf(User.name)
    var editRoles = mutableStateOf(User.roles)
    var editNewPassword = mutableStateOf("")
    var editConfirmNewPassword = mutableStateOf("")

    fun findInvitationCode() {
        val invitationCodeRef = FirebaseDatabase.getInstance().getReference("real/group_name_and_invitation_code")
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
        invitationCodeRef.addValueEventListener(valueEventListener)
    }

    fun updateInfo(context: Context, currentNavController: NavHostController) {
        val groupCompositionRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/composition/${User.uid}")
        groupCompositionRef.child(context.getString(R.string.database_name)).setValue(editName.value)
        groupCompositionRef.child(context.getString(R.string.database_roles)).setValue(editRoles.value)
        val userRef = FirebaseDatabase.getInstance().getReference("real/user/real_user/${User.uid}")
        userRef.child(context.getString(R.string.database_name)).setValue(editName.value)
        userRef.child(context.getString(R.string.database_roles)).setValue(editRoles.value)
        if(editNewPassword.value.isNotEmpty() && editConfirmNewPassword.value.isNotEmpty() && editNewPassword.value.trim() == editConfirmNewPassword.value.trim()) {
            FirebaseAuth.getInstance().currentUser?.updatePassword(editNewPassword.value)
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        currentNavController.popBackStack()
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
        deleteMember.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("snapshot childrenCount : ${snapshot.childrenCount}")
                for(childSnapshot in snapshot.children) {
                    val registrant = childSnapshot.child(context.getString(R.string.database_registrant)).getValue(String::class.java)
                    if(registrant == User.uid) childSnapshot.ref.removeValue()
                }
                if(snapshot.childrenCount < 1) {
                    FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}").removeValue()
                    FirebaseDatabase.getInstance().getReference("real/group_name_and_invitation_code").child(User.groupName).removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        announcementRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children) {
                    val writerUid = childSnapshot.child(context.getString(R.string.database_writer_uid)).getValue(String::class.java) ?: ""
                    if(User.uid == writerUid) {
                        FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/announcement/${childSnapshot.key}").removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        qaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(childSnapshot in snapshot.children) {
                    val writerRef = childSnapshot.child(context.getString(R.string.database_writer))
                    val writer = writerRef.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""
                    val answerContentRef = childSnapshot.child(context.getString(R.string.database_answer_content)).child(context.getString(R.string.database_writer))
                    val answerUid = answerContentRef.getValue(String::class.java) ?: ""
                    if(answerUid == User.uid) {
                        qaRef.child(context.getString(R.string.database_flag)).setValue(false)
                        FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a/$answerContentRef").removeValue()
                    } else if(writer == User.uid){
                        FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a/${childSnapshot.key}").removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}").child(context.getString(
            R.string.family_representative)).removeValue()
        FirebaseDatabase.getInstance().getReference("real/user/email").child(User.email.replace("@", "_").replace(".","_")).removeValue()
        deleteMember.removeValue()
        userUidRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.done_withdrawal), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                println(exception.message)
            }
        currentUser?.delete()?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val intent = Intent(context, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                User.deleteInfo()
                context.startActivity(intent)
            }
        }
    }
}