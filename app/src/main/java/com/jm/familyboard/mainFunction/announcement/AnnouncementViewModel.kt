package com.jm.familyboard.mainFunction.announcement

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.datamodel.AnnouncementResponse
import com.jm.familyboard.reusable.FirebaseAllPath

class AnnouncementViewModel: ViewModel() {
    private val announcementReference = FirebaseAllPath.database.getReference(FirebaseAllPath.SERVICE + "${User.groupName}/announcement")
    var announcements = mutableStateOf(listOf<AnnouncementResponse>())

    var vmTitle = mutableStateOf("")
    var vmContent = mutableStateOf("")
    var vmWriteDate = mutableStateOf("")
    var vmModify = mutableStateOf(false)
    var vmWritingNo = mutableIntStateOf(0)

    fun init() {
        vmModify.value = false
        vmTitle.value = ""
        vmContent.value = ""
        vmWriteDate.value = ""
        vmWritingNo.intValue = 0
    }

    fun loadData(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcementList = mutableListOf<AnnouncementResponse>()

                for (childSnapshot in snapshot.children.reversed()) {
                    val dateKey = childSnapshot.key
                    val content = childSnapshot.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val date = childSnapshot.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                    val title = childSnapshot.child(context.getString(R.string.database_title)).getValue(String::class.java) ?: ""
                    val writer = childSnapshot.child(context.getString(R.string.database_writer)).getValue(String::class.java) ?: ""
                    val writerUid = childSnapshot.child(context.getString(R.string.database_writer_uid)).getValue(String::class.java) ?: ""
                    println("?? ${childSnapshot.child(context.getString(R.string.database_content))}")

                    val announcement = AnnouncementResponse(content, date, title, writer, writerUid)
                    announcementList.add(announcement)
                    announcements.value = announcementList
/*                    val content = childSnapshot.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val date = childSnapshot.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                    val no = childSnapshot.child(context.getString(R.string.database_no)).getValue(Int::class.java) ?: 0
                    val title = childSnapshot.child(context.getString(R.string.database_title)).getValue(String::class.java) ?: ""
                    val writer = childSnapshot.child(context.getString(R.string.database_writer)).getValue(String::class.java) ?: ""
                    val writerUid = childSnapshot.child(context.getString(R.string.database_writer_uid)).getValue(String::class.java) ?: ""

                    vmTitle.value = title
                    vmContent.value = content

                    val announcement = AnnouncementResponse(content, date, no, title, writer, writerUid)
                    announcementList.add(announcement)
                    announcements.value = announcementList*/
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        announcementReference.addValueEventListener(valueEventListener)
    }

    fun writeDB(context: Context, isModify: Boolean, writeNo: Int, currentNavController: NavHostController){
        announcementReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ref =  announcementReference.child(vmWriteDate.value)
                ref.child(context.getString(R.string.database_content)).setValue(vmContent.value)
                ref.child(context.getString(R.string.database_title)).setValue(vmTitle.value)
                ref.child(context.getString(R.string.database_date)).setValue(vmWriteDate.value)
                ref.child(context.getString(R.string.database_writer_uid)).setValue(User.uid)
                ref.child(context.getString(R.string.database_writer)).setValue(User.name)
                currentNavController.popBackStack()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}