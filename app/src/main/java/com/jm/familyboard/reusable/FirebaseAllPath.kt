package com.jm.familyboard.reusable

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseAllPath {
    val database = FirebaseDatabase.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    const val GROUP_NAME_AND_INVITATION_CODE = "real/group_name_and_invitation_code" // 하위에 그룹 이름 : 랜덤 초대 코드
    const val SERVICE = "real/service/" // 하위에 그룹 이름 / announcement || composition || q_a 등등
    const val USER_EMAIL = "real/user/email/" // 하위에 이메일_도메인1_도메인2 : email
    const val USER_INFO = "real/user/real_user/" // 하위에 uid 하위에 그 uid 정보
}