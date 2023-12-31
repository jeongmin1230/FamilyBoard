package com.jm.familyboard

object User {
    var representativeUid: String = ""

    var uid: String = ""
    var name: String = ""
    var email: String = ""
    var groupName: String = ""
    var roles: String = ""

    fun deleteInfo() {
        representativeUid = ""

        uid = ""
        name = ""
        email = ""
        groupName = ""
        roles = ""
    }
}