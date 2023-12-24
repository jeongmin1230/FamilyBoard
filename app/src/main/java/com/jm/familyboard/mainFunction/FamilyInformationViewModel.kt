package com.jm.familyboard.mainFunction

import androidx.lifecycle.ViewModel

class FamilyInformationViewModel: ViewModel() {
    var memberUid = mutableListOf<String>() // uid
    var memberName = mutableListOf<String>() // 이름

    fun addMemberUid(uid: String) {
        memberUid.add(uid)
    }

    fun addMemberName(name: String) {
        memberName.add(name)
    }
}