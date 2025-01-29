package com.example.basiccalculator

import android.net.Uri

data class MediaItems(
    val uri: Uri,
    val displayName: String,
    val dateAdded: Long
)
