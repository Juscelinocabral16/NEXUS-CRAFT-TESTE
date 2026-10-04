package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val tutorialId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "build_progress")
data class BuildProgressEntity(
    @PrimaryKey val tutorialId: String,
    val completedStepsCsv: String = "", // e.g. "1,2,3"
    val isCompleted: Boolean = false,
    val currentStepIndex: Int = 0,
    val userNotes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "material_checklist", primaryKeys = ["tutorialId", "materialId"])
data class MaterialCheckedEntity(
    val tutorialId: String,
    val materialId: String,
    val isChecked: Boolean = true,
    val checkedAt: Long = System.currentTimeMillis()
)
