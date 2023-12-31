package com.jm.familyboard.datamodel

data class QAResponse(
    val no: No
) {
    data class No(
        val answerContent: AnswerContent,
        val flag: Boolean,
        val no: Int,
        val questionContent: QuestionContent,
        val questionTitle: String,
        val writer: Writer
    ) {
        data class AnswerContent(
            val content: String,
            val date: String
        )

        data class QuestionContent(
            val content: String,
            val date: String
        )

        data class Writer(
            val name: String,
            val uid: String
        )
    }
}