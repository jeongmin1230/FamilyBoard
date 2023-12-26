package com.jm.familyboard.reusable

import android.app.Activity
import android.content.Context

fun getStoredUserEmail(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    return sharedPreferences.getString("email", "") ?: ""
}

fun getStoredUserPassword(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    return sharedPreferences.getString("password", "") ?: ""
}

fun storeUserCredentials(activity: Activity, name: String, email: String, password: String, groupName: String, roles: String) {
    val sharedPreferences = activity.getSharedPreferences("UserCredentials", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("name", name)
    editor.putString("email", email)
    editor.putString("password", password)
    editor.putString("groupName", groupName)
    editor.putString("roles", roles)
    editor.apply()
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
