package com.jm.familyboard

object User {
    var uid: String = ""
    var name: String = ""
    var email: String = ""
    var groupName: String = ""
    var roles: String = ""

    fun deleteInfo() {
        uid = ""
        name = ""
        email = ""
        groupName = ""
        roles = ""
    }
}