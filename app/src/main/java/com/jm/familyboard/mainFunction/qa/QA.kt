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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jm.familyboard.R
import com.jm.familyboard.reusable.AllList
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.CompleteButton
import com.jm.familyboard.reusable.EnterInfoMultiColumn
import com.jm.familyboard.reusable.EnterInfoSingleColumn
import com.jm.familyboard.reusable.TextComposable
import com.jm.familyboard.reusable.textFieldKeyboard
import com.jm.familyboard.ui.theme.FamilyBoardTheme
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
@Composable
fun Q_AScreen(mainNavController: NavHostController) {
    val context = LocalContext.current
    val qaViewModel = QAViewModel()
    val currentNavController = rememberNavController()
    val qaList = remember { qaViewModel.qas }
    val qaArray = stringArrayResource(id = R.array.q_a_nav)
    val isModify = remember { mutableStateOf(false) }
    NavHost(currentNavController, startDestination = qaArray[1]) {
        composable(qaArray[1]) {
            Column(modifier = Modifier
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()) {
                AppBar(true, qaArray[0], R.drawable.ic_q_a, {
                    qaViewModel.init()
                    currentNavController.navigate(qaArray[3])}) {
                    qaViewModel.init()
                    mainNavController.popBackStack()
                }
                LaunchedEffect(qaList) { qaViewModel.loadData(context) }

                Spacer(modifier = Modifier.height(20.dp))

                if(qaList.value.isEmpty()) {
                    TextComposable(
                        text = stringResource(id = R.string.empty_screen),
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                    )
                } else {
                    qaList.value.forEach { qa ->
                        AllList(
                            screenType = 1,
                            flag = qa.no.flag,
                            modify = isModify,
                            no = qa.no.no,
                            title = qa.no.questionTitle,
                            content = qa.no.questionContent.content,
                            date = qa.no.questionContent.date,
                            writer = qa.no.writer.name,
                            answer = qa.no.answerContent.content,
                            writerUid = qa.no.writer.uid
                        ) {
                            qaViewModel.vmQuestionContent.value = qa.no.questionContent.content
                            qaViewModel.vmQuestionTitle.value = qa.no.questionTitle
                            qaViewModel.vmAnswerContent.value = qa.no.answerContent.content
                            qaViewModel.vmFlag.value = qa.no.flag
                            qaViewModel.vmQuestionNo.intValue = qa.no.no
                            currentNavController.navigate(qaArray[5])
                        }
                    }
                }
            }
        }
        composable(qaArray[3]) {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                qaViewModel.vmQuestionDate.value = SimpleDateFormat(stringResource(id = R.string.announcement_date_format)).format(Date(System.currentTimeMillis()))
                AppBar(false, qaArray[2], null, {}) {
                    qaViewModel.init()
                    currentNavController.popBackStack() }
                WriteQuestionScreen(qaViewModel.vmQuestionTitle, qaViewModel.vmQuestionContent, qaViewModel.vmQuestionDate.value) {
                    qaViewModel.writeQuestion(context, currentNavController)
                }
            }
        }

        composable(qaArray[5]) {
            qaViewModel.vmAnswerDate.value = SimpleDateFormat(stringResource(id = R.string.announcement_date_format)).format(Date(System.currentTimeMillis()))
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                println("vmWriteNo : ${qaViewModel.vmQuestionNo.intValue}")
                AppBar(false, qaArray[4], null, {}) { currentNavController.popBackStack() }
                AnswerScreen(qaViewModel.vmQuestionContent, qaViewModel.vmAnswerContent) {
                    qaViewModel.writeAnswer(context, currentNavController)
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun WriteQuestionScreen(questionTitle: MutableState<String>, questionContent: MutableState<String>, questionDate: String, writeQuestion: () -> Unit) {
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
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Default, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 10.dp)
                )
                TextComposable(
                    text = "${stringResource(R.string.register_date)} $questionDate",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 10.dp)
                )
            }
        }
        CompleteButton(
            isEnable = questionTitle.value.isNotEmpty() && questionContent.value.isNotEmpty(),
            text = stringResource(id = R.string.q_a_write_answer),
            color = Color.Blue.copy(0.2f),
            modifier = Modifier.fillMaxWidth()) {
            writeQuestion()
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun AnswerScreen(questionContent: MutableState<String>, answerContent: MutableState<String>, writeAnswer: () -> Unit) {
    Column {
        Column(Modifier.weight(1f)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EnterInfoMultiColumn(
                    mean = stringResource(R.string.q_a_content),
                    enabled = false,
                    tfValue = questionContent,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Default, keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                EnterInfoMultiColumn(
                    mean = stringResource(R.string.q_a_answer),
                    enabled = true,
                    tfValue = answerContent,
                    keyboardOptions = textFieldKeyboard(imeAction = ImeAction.Default, keyboardType = KeyboardType.Text),
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
            modifier = Modifier.fillMaxWidth()) { writeAnswer() }
    }
}

@Preview(showSystemUi = true)
@Composable
fun Q_APreview() {
    FamilyBoardTheme {
        Q_AScreen(rememberNavController())
    }
}