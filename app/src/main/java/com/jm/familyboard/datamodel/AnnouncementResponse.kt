package com.jm.familyboard.datamodel

data class AnnouncementResponse(
    val content: String,
    val date: String,
    val title: String,
    val writer: String,
    val writerUid: String
)