package com.example.data.model

data class ProjectHistoryItem(
    val id: String,
    val title: String,
    val bengaliSummary: String,
    val timestamp: Long,
    val durationSeconds: Int,
    val sceneCount: Int,
    val filePath: String,
    val fileSizeBytes: Long
)
