package com.jm.familyboard.datamodel

data class AnswerResponse(
    val answerTitle: String,
    val content: String,
    val date: String,
    val name: String,
    val uid: String
)