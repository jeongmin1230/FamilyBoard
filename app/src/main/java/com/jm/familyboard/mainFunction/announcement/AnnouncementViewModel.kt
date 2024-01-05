package com.jm.familyboard.mainFunction.announcement

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.datamodel.AnnouncementResponse

class AnnouncementViewModel: ViewModel() {
    var announcements = mutableStateOf(listOf<AnnouncementResponse>())
    private val announcementReference = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/announcement")

    var vmTitle = mutableStateOf("")
    var vmContent = mutableStateOf("")
    var vmWriteDate = mutableStateOf("")

    var vmModify = mutableStateOf(false)

    var vmWritingNo = mutableIntStateOf(0)

    fun init() {
        vmModify.value = false
        vmTitle.value = ""
        vmContent.value = ""
    }

    fun loadData(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcementList = mutableListOf<AnnouncementResponse>()

                for (childSnapshot in snapshot.children) {
                    val content = childSnapshot.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val date = childSnapshot.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                    val no = childSnapshot.child(context.getString(R.string.database_no)).getValue(Int::class.java) ?: 0
                    val title = childSnapshot.child(context.getString(R.string.database_title)).getValue(String::class.java) ?: ""
                    val writer = childSnapshot.child(context.getString(R.string.database_writer)).getValue(String::class.java) ?: ""
                    val writerUid = childSnapshot.child(context.getString(R.string.database_writer_uid)).getValue(String::class.java) ?: ""

                    if(User.uid == writerUid) println("User.uid == writerUid ${childSnapshot.key}")

                    val announcement = AnnouncementResponse(content, date, no, title, writer, writerUid)
                    vmTitle.value = title
                    vmContent.value = content
                    announcementList.add(announcement)
                    announcements.value = announcementList
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
                val nextNo = snapshot.childrenCount + 1
                val ref =  announcementReference.child("${context.getString(R.string.database_no)}${if(isModify)writeNo else nextNo}")
                ref.child(context.getString(R.string.database_content)).setValue(vmContent.value)
                ref.child(context.getString(R.string.database_title)).setValue(vmTitle.value)
                ref.child(context.getString(R.string.database_date)).setValue(vmWriteDate.value)
                ref.child(context.getString(R.string.database_no)).setValue(if(isModify)writeNo else nextNo)
                ref.child(context.getString(R.string.database_writer_uid)).setValue(User.uid)
                ref.child(context.getString(R.string.database_writer)).setValue(User.name)
                currentNavController.popBackStack()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}