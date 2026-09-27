package com.sarfrazqureshi.clipvault.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ClipType {
    NUMBER, LINK, TEXT, ADULT
}

@Entity(tableName = "clip_items")
data class ClipItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val type: ClipType,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)
