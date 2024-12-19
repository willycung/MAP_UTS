package com.example.if570_lab_uts_willy_00000079845

import com.google.firebase.Timestamp

data class StoryData(
    val id: String = "",
    val caption: String = "",
    val imageUrl: String? = null,
    val timestamp: Timestamp? = null,  // Use Firestore's Timestamp instead of Long
    var isLiked: Boolean = false
)