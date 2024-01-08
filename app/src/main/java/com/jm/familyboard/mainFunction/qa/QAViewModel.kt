package com.jm.familyboard.mainFunction.qa

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
import com.jm.familyboard.datamodel.AnswerContentResponse
import com.jm.familyboard.datamodel.QAResponse

class QAViewModel: ViewModel() {
    var qas = mutableStateOf(listOf<QAResponse>())
    var comments = mutableStateOf(listOf<AnswerContentResponse>())

    var vmQuestionNo = mutableIntStateOf(0)

    var vmQuestionTitle = mutableStateOf("")
    var vmQuestionContent = mutableStateOf("")
    var vmQuestionDate = mutableStateOf("")

    var vmAnswerContent = mutableStateOf("")
    var vmAnswerDate = mutableStateOf("")

    var vmFlag = mutableStateOf(false)

    private val qaReference = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a")
    private val writeAnswerReference = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a")


    fun init() {
        vmQuestionNo.intValue = 0
        vmQuestionTitle.value = ""
        vmQuestionContent.value = ""
        vmFlag.value = false
        vmAnswerContent.value = ""
    }

    fun loadData(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val qaList = mutableListOf<QAResponse>()
                for (childSnapshot in snapshot.children.reversed()) {
                    val answerContent = childSnapshot.child(context.getString(R.string.database_answer_content))
                    val contentInAC = answerContent.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val dateInAC = answerContent.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                    val writerInAC = answerContent.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val writerUidInAC = answerContent.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""

                    val flag = childSnapshot.child(context.getString(R.string.database_flag)).getValue(Boolean::class.java) ?: false

                    val no = childSnapshot.child(context.getString(R.string.database_no)).getValue(Int::class.java) ?: 0

                    val questionContent = childSnapshot.child(context.getString(R.string.database_question_content))
                    val contentInQC = questionContent.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val dateInQC = questionContent.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""

                    val questionTitle = childSnapshot.child(context.getString(R.string.database_question_title)).getValue(String::class.java) ?: ""

                    val writer = childSnapshot.child(context.getString(R.string.database_writer))
                    val nameInWriter = writer.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val uidInWriter = writer.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""

                    vmQuestionTitle.value = questionTitle
                    vmQuestionContent.value = contentInQC
                    vmQuestionDate.value = dateInQC
                    vmAnswerContent.value = contentInAC
                    vmAnswerDate.value = dateInAC
                    vmFlag.value = flag
                    val qa = QAResponse(
                            QAResponse.No(
                                QAResponse.No.AnswerContent(answerContent.childrenCount, contentInAC, vmAnswerDate.value),
                                no,
                                QAResponse.No.QuestionContent(contentInQC, dateInQC),
                                questionTitle,
                                QAResponse.No.Writer(nameInWriter, uidInWriter)
                            )
                    )
                    qaList.add(qa)
                }
                qas.value = qaList
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        qaReference.addValueEventListener(valueEventListener)
    }

    fun writeQuestion(context: Context, currentNavController: NavHostController) {
        qaReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nextNo = snapshot.childrenCount + 1
                val ref =  qaReference.child("no$nextNo")
                ref.child(context.getString(R.string.database_no)).setValue(nextNo)
                val qcRef = ref.child(context.getString(R.string.database_question_content))
                qcRef.child(context.getString(R.string.database_content)).setValue(vmQuestionContent.value)
                qcRef.child(context.getString(R.string.database_date)).setValue(vmQuestionDate.value)
                ref.child(context.getString(R.string.database_question_title)).setValue(vmQuestionTitle.value)
                val writerRef = ref.child(context.getString(R.string.database_writer))
                writerRef.child(context.getString(R.string.database_name)).setValue(User.name)
                writerRef.child(context.getString(R.string.database_uid)).setValue(User.uid)

                currentNavController.popBackStack()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun loadComment(context: Context) {
        val answerRef = writeAnswerReference.child("no${vmQuestionNo.intValue}")
        println("answerRef : $answerRef")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val answerContentList = mutableListOf<AnswerContentResponse>()
                for(childSnapshot in snapshot.children) {
                    val answerContentSnapshot = childSnapshot.child("answerContent")

                    answerContentSnapshot.children.forEach { answerSnapshot ->
                        val commentContent  = answerSnapshot.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                        val commentDate = answerSnapshot.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                        val commentName = answerSnapshot.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                        val commentUid = answerSnapshot.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""
                        val answerContent = AnswerContentResponse(commentContent, commentDate, commentName, commentUid)
                        answerContentList.add(answerContent)
                    }
                    println("answerContentList : $answerContentList")
                    comments.value = answerContentList
                    println("comments.value ${comments.value}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        qaReference.addListenerForSingleValueEvent(valueEventListener)
    }

    fun writeComment(context: Context) {
        val answerRef = writeAnswerReference.child("no${vmQuestionNo.intValue}")
        answerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val answerContentsRef = answerRef.child(context.getString(R.string.database_answer_content))
                val answerContentRef = answerContentsRef.child(vmAnswerDate.value)
                answerContentRef.child(context.getString(R.string.database_content)).setValue(vmAnswerContent.value)
                answerContentRef.child(context.getString(R.string.database_uid)).setValue(User.uid)
                answerContentRef.child(context.getString(R.string.database_name)).setValue(User.name)
                answerContentRef.child(context.getString(R.string.database_date)).setValue(vmAnswerDate.value)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}