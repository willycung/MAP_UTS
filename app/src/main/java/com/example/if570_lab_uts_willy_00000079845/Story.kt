package com.example.if570_lab_uts_willy_00000079845

data class Story(
    val caption: String = "",  // Misalnya untuk teks cerita
    val imageUrl: String? = null,  // URL gambar, bisa null jika tidak ada gambar
    val timestamp: com.google.firebase.Timestamp? = null  // Timestamp dari Firestore
)