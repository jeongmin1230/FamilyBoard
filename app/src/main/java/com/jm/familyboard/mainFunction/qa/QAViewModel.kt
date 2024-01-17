package com.jm.familyboard.mainFunction.qa

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.datamodel.AnswerResponse
import com.jm.familyboard.datamodel.QAResponse
import com.jm.familyboard.reusable.FirebaseAllPath

class QAViewModel: ViewModel() {
    private val qa = FirebaseAllPath.database.getReference(FirebaseAllPath.SERVICE + User.groupName + "/q_a")
    var qas = mutableStateOf(listOf<QAResponse>())
    var comments = mutableStateOf(listOf<AnswerResponse>())

    var vmModify = mutableStateOf(false)

    var vmQuestionTitle = mutableStateOf("")
    var vmQuestionContent = mutableStateOf("")
    var vmQuestionDate = mutableStateOf("")

    var vmAnswerContent = mutableStateOf("")
    var vmAnswerDate = mutableStateOf("")

    fun init() {
        vmModify.value = false
        vmQuestionTitle.value = ""
        vmQuestionContent.value = ""
    }

    fun loadData(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val qaList = mutableListOf<QAResponse>()
                for (childSnapshot in snapshot.children.reversed()) {
                    val answerCount = childSnapshot.child(context.getString(R.string.database_answer_content)).childrenCount
                    val questionContent = childSnapshot.child(context.getString(R.string.database_question_content))
                    val content = questionContent.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val date = questionContent.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                    val questionTitle = childSnapshot.child(context.getString(R.string.database_question_title)).getValue(String::class.java) ?: ""
                    val writer = childSnapshot.child(context.getString(R.string.database_writer))
                    val name = writer.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val uid = writer.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""
                    val qa = QAResponse(
                        answerCount,
                        QAResponse.QuestionContent(content, date),
                        questionTitle,
                        QAResponse.Writer(name, uid)
                    )
                    qaList.add(qa)
                }
                qas.value = qaList
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        qa.addValueEventListener(valueEventListener)
    }

    fun writeQuestion(context: Context, currentNavController: NavHostController) {
        qa.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ref = snapshot.child(vmQuestionDate.value).ref
                val questionContent = ref.child(context.getString(R.string.database_question_content)).ref
                questionContent.child(context.getString(R.string.database_content)).setValue(vmQuestionContent.value)
                questionContent.child(context.getString(R.string.database_date)).setValue(vmQuestionDate.value)
                ref.child(context.getString(R.string.database_question_title)).setValue(vmQuestionTitle.value)
                val writer = ref.child(context.getString(R.string.database_writer)).ref
                writer.child(context.getString(R.string.database_name)).setValue(User.name)
                writer.child(context.getString(R.string.database_uid)).setValue(User.uid)

                currentNavController.popBackStack()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun loadComment(context: Context) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val answerContentList = mutableListOf<AnswerResponse>()
                for(childSnapshot in snapshot.children) {
                    if(childSnapshot.key == vmQuestionDate.value) {
                        childSnapshot.child(context.getString(R.string.database_answer_content)).children.forEach {
                            val answerTime = it.key ?: ""
                            val content = it.child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                            val date = it.child(context.getString(R.string.database_date)).getValue(String::class.java) ?: ""
                            val name = it.child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                            val uid = it.child(context.getString(R.string.database_uid)).getValue(String::class.java) ?: ""

                            val answerContent = AnswerResponse(answerTime, content, date, name, uid)
                            answerContentList.add(answerContent)
                        }
                    }
                    comments.value = answerContentList
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        qa.addListenerForSingleValueEvent(valueEventListener)
    }

    fun writeComment(context: Context) {
        val answerRef = qa.child(vmQuestionDate.value)
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