package com.jm.familyboard.reusable

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseAllPath {
    val database = FirebaseDatabase.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    const val GROUP_NAME_AND_INVITATION_CODE = "real/group_name_and_invitation_code"
    const val SERVICE = "real/service/"
    const val USER_EMAIL = "real/user/email/"
    const val USER_INFO = "real/user/real_user/"
}