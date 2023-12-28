package com.jm.familyboard.mainFunction.qa

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jm.familyboard.CompleteButton
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.EnterInfoMultiColumn
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.HowToUseColumn
import com.jm.familyboard.reusable.LoadList
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.ui.theme.FamilyBoardTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun Q_AScreen(mainNavController: NavHostController) {
    val context = LocalContext.current
    val no = remember { mutableIntStateOf(0) }
    val qaArray = stringArrayResource(id = R.array.q_a_nav)
    val questionContent = remember { mutableStateOf("") }
    val currentNavController = rememberNavController()
    NavHost(currentNavController, startDestination = qaArray[1]) {
        composable(qaArray[1]) {
            Column(modifier = Modifier
                .background(Color(0XFFF6EAC2))
                .fillMaxSize()) {
                AppBar(true, qaArray[0], R.drawable.ic_q_a, { currentNavController.navigate(qaArray[3])}) { mainNavController.popBackStack() }
                Q_A_List(no, questionContent, context, currentNavController)
            }
        }
        composable(qaArray[3]) {
            Column(modifier = Modifier
                .background(Color(0XFFF6EAC2))
                .fillMaxSize()) {
                AppBar(false, qaArray[2], null, {}) { currentNavController.popBackStack() }
                RegisterQuestionScreen(context, currentNavController)
            }
        }

        composable(qaArray[5]) {
            Column(modifier = Modifier
                .background(Color(0XFFF6EAC2))
                .fillMaxSize()) {
                AppBar(false, qaArray[4], null, {}) { currentNavController.popBackStack() }
                ReplyScreen(context, no.intValue, questionContent, currentNavController)
            }
        }
    }
}

@Composable
fun Q_A_List(no: MutableState<Int>, questionContent: MutableState<String>, context: Context, currentNavController: NavHostController) {
    var qas by remember { mutableStateOf(emptyList<QA>()) }
    val registerTitle = remember { mutableStateOf("") }
    val registerContent = remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance()
    val qaRef = database.getReference("real/service/${User.groupName}/q_a")

    DisposableEffect(qaRef) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val qaList = mutableListOf<QA>()
                for (childSnapshot in snapshot.children) {
                    no.value = childSnapshot.key.toString().split(context.getString(R.string.database_no)).last().toInt()
                    val flag = childSnapshot.child(context.getString(R.string.database_flag)).getValue(Boolean::class.java) ?: false
                    val title = childSnapshot.child(context.getString(R.string.database_question_title)).getValue(String::class.java) ?: ""
                    val registrant = childSnapshot.child(context.getString(R.string.database_registrant)).child(context.getString(R.string.database_name)).getValue(String::class.java) ?: ""
                    val answer = childSnapshot.child(context.getString(R.string.database_answer_content)).child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    val content = childSnapshot.child(context.getString(R.string.database_question_content)).child(context.getString(R.string.database_content)).getValue(String::class.java) ?: ""
                    registerTitle.value = title
                    questionContent.value = content
                    val qa = QA(flag, no.value, title, content, answer, registrant)
                    qaList.add(qa)
                }
                qas = qaList
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        qaRef.addValueEventListener(valueEventListener)

        onDispose {
            qaRef.removeEventListener(valueEventListener)
        }
    }
    Column {
        HowToUseColumn(text = stringResource(id = R.string.q_a_information))
        if(qas.isEmpty()) {
            TextComposable(
                text = "등록 된 질문이 없습니다.",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                modifier = Modifier
            )
        } else {
            val flag = remember { mutableStateOf(false) }
            Spacer(modifier = Modifier.height(20.dp))
            qas.forEach { qa ->
                LoadList(
                    screenType = 1,
                    flag = flag,
                    editTitle = registerTitle,
                    editContent = registerContent,
                    title = qa.title,
                    content = qa.content,
                    answer = qa.answer,
                    writer = qa.writer
                ) {
                    no.value = qa.no
//                    currentNavController.navigate(qaArray[5])
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RegisterQuestionScreen(context: Context, currentNavController: NavHostController) {
    val questionTitle = remember { mutableStateOf("") }
    val questionContent = remember { mutableStateOf("") }
    val createDate = SimpleDateFormat(stringResource(id = R.string.announcement_date_format)).format(Date(System.currentTimeMillis()))
    val qaRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a")
    Column {
        Column(Modifier.weight(1f)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EnterInfoSingleColumn(
                    essential = false,
                    mean = stringResource(id = R.string.title),
                    tfValue = questionTitle,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {}
                Spacer(modifier = Modifier.height(6.dp))
                EnterInfoMultiColumn(
                    mean = stringResource(R.string.q_a_content),
                    enabled = true,
                    tfValue = questionContent,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 10.dp)
                )
                TextComposable(
                    text = "${stringResource(R.string.date)} $createDate",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 10.dp)
                )
            }
        }
        CompleteButton(
            isEnable = questionTitle.value.isNotEmpty() && questionContent.value.isNotEmpty(),
            text = stringResource(id = R.string.register_notice),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier.fillMaxWidth()) {
            qaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nextNo = snapshot.childrenCount + 1
                    val ref = qaRef.child("${context.getString(R.string.database_no)}$nextNo")
                    ref.child(context.getString(R.string.database_question_title)).setValue(questionTitle.value)
                    val contentRef = ref.child(context.getString(R.string.database_question_content))
                    contentRef.child(context.getString(R.string.database_date)).setValue(createDate)
                    contentRef.child(context.getString(R.string.database_content)).setValue(questionContent.value)
                    ref.child(context.getString(R.string.database_flag)).setValue(false)
                    val registrantRef = ref.child(context.getString(R.string.database_registrant))
                    registrantRef.child(context.getString(R.string.database_name)).setValue(User.name)
                    registrantRef.child(context.getString(R.string.database_uid)).setValue(User.uid)
                    currentNavController.popBackStack()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun ReplyScreen(context: Context, no: Int, questionContent: MutableState<String>, currentNavController: NavHostController) {
    val noRef = FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}/q_a/${context.getString(R.string.database_no)}$no")
    val date = SimpleDateFormat(stringResource(id = R.string.announcement_date_format)).format(Date(System.currentTimeMillis()))
    val answerContent = remember { mutableStateOf("") }
    Column {
        Column(Modifier.weight(1f)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EnterInfoMultiColumn(
                    mean = stringResource(R.string.q_a_content),
                    enabled = false,
                    tfValue = questionContent,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                EnterInfoMultiColumn(
                    mean = stringResource(R.string.q_a_answer_content),
                    enabled = true,
                    tfValue = answerContent,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 10.dp)
                )
            }
        }
        CompleteButton(
            isEnable = questionContent.value.isNotEmpty() && answerContent.value.isNotEmpty(),
            text = stringResource(id = R.string.register_notice),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier.fillMaxWidth()) {
            noRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    noRef.child(context.getString(R.string.database_flag)).setValue(true)
                    val answerContentRef = noRef.child(context.getString(R.string.database_answer_content))
                    answerContentRef.child(context.getString(R.string.database_content)).setValue(answerContent.value)
                    answerContentRef.child(context.getString(R.string.database_date)).setValue(date)
                    currentNavController.popBackStack()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}

data class QA(val flag: Boolean, val no: Int, val title: String, val content: String, val answer: String, val writer: String)

@Preview(showSystemUi = true)
@Composable
fun Q_APreview() {
    FamilyBoardTheme {
        Q_AScreen(rememberNavController())
    }
}