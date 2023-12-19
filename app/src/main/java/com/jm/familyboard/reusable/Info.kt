package com.jm.familyboard.reusable

import android.content.Context

fun getStoredUserEmail(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    return sharedPreferences.getString("email", "") ?: ""
}

fun getStoredUserPassword(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    return sharedPreferences.getString("password", "") ?: ""
}

fun removeUserCredentials(context: Context) {
    val sharedPreferences = context.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("email")
    editor.remove("password")
    editor.remove("name")
    editor.remove("groupName")
    editor.remove("roles")
    editor.apply()
}
