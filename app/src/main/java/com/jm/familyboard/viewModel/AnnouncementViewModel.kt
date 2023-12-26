package com.jm.familyboard.viewModel

import androidx.lifecycle.ViewModel
import com.jm.familyboard.User

class AnnouncementViewModel: ViewModel() {
    private var vmTitle = ""
    private var vmContent = ""

    private val writer = User.name
    private val writerUid = User.uid

    fun init() {
        vmTitle = ""
        vmContent = ""
    }
    fun renewal(title: String, content: String) {
        vmTitle = title
        vmContent = content
    }
}