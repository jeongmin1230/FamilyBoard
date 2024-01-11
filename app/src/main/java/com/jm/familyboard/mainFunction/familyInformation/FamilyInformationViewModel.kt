package com.jm.familyboard.mainFunction.familyInformation

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.datamodel.FamilyInformationResponse
import com.jm.familyboard.reusable.FirebaseAllPath

class FamilyInformationViewModel: ViewModel() {
    private val familyCompositionReference = FirebaseAllPath.database.getReference(FirebaseAllPath.SERVICE + "${User.groupName}/composition")
    var familyComposition = mutableStateOf(listOf<FamilyInformationResponse>())

    fun loadComposition(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val familyInformationList = mutableListOf<FamilyInformationResponse>()

                for (childSnapshot in snapshot.children) {
                    val uid = childSnapshot.key ?: ""
                    val name = childSnapshot.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val roles = childSnapshot.child(context.getString(R.string.database_roles)).getValue(String::class.java) ?: ""

                    val familyInformation = FamilyInformationResponse(uid, name, roles)
                    familyInformationList.add(familyInformation)
                    familyComposition.value = familyInformationList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        familyCompositionReference.addListenerForSingleValueEvent(valueEventListener)
    }
}