package com.jm.familyboard.datamodel

data class QAResponse(
    val answerCount: Long,
    val questionContent: QuestionContent,
    val questionTitle: String,
    val writer: Writer
) {
    data class QuestionContent(
        val content: String,
        val date: String
    )
    data class Writer(
        val name: String,
        val uid: String
    )
}