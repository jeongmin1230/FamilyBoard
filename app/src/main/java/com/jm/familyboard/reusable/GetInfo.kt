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
