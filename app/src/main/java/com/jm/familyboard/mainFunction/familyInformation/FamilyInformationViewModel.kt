package com.jm.familyboard.mainFunction.familyInformation

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.datamodel.FamilyInformationResponse

class FamilyInformationViewModel: ViewModel() {
    val uid = mutableStateOf("")
    val existRepresentative = mutableIntStateOf(0)
    var familyComposition = mutableStateOf(listOf<FamilyInformationResponse>())
    private val database = FirebaseDatabase.getInstance()
    private val representativeReference = database.getReference("real/service/${User.groupName}")
    private val familyCompositionReference = database.getReference("real/service/${User.groupName}/composition")

    fun findRepresentativeUid(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val representativeName = snapshot.child(context.getString(R.string.family_representative))
                if(representativeName.exists()) {
                    uid.value = representativeName.value.toString()
                    existRepresentative.intValue = 1
                }
                else {
                    existRepresentative.intValue = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        representativeReference.addListenerForSingleValueEvent(valueEventListener)
    }

    fun loadComposition(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val familyInformationList = mutableListOf<FamilyInformationResponse>()

                for (childSnapshot in snapshot.children) {
                    val uid = childSnapshot.key ?: ""
                    val email = childSnapshot.child(context.getString(R.string.database_email)).getValue(String::class.java) ?: ""
                    val name = childSnapshot.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val roles = childSnapshot.child(context.getString(R.string.database_roles)).getValue(String::class.java) ?: ""
                    val familyInformation = FamilyInformationResponse(uid, email, name, roles)

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