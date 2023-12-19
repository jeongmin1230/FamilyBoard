package com.jm.familyboard

object User {
    var name: String = ""
    var email: String = ""
    var groupName: String = ""
    var roles: String = ""

    fun deleteInfo() {
        name = ""
        email = ""
        groupName = ""
        roles = ""
    }
}