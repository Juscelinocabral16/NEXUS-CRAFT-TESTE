package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BuildProgressEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.MaterialCheckedEntity
import com.example.data.model.BuildDifficulty
import com.example.data.model.MaterialItem
import com.example.data.model.Tutorial
import com.example.data.model.TutorialCategory
import com.example.data.model.TutorialStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.Normalizer

data class TutorialItemUiState(
    val tutorial: Tutorial,
    val isFavorite: Boolean = false,
    val completedStepsCount: Int = 0,
    val totalStepsCount: Int = 0,
    val progressPercent: Float = 0f,
    val isCompleted: Boolean = false
)

data class TutorialDetailUiState(
    val tutorial: Tutorial,
    val isFavorite: Boolean = false,
    val completedStepNumbers: Set<Int> = emptySet(),
    val checkedMaterialIds: Set<String> = emptySet(),
    val currentStepIndex: Int = 0,
    val userNotes: String = "",
    val isFinished: Boolean = false
) {
    val progressPercent: Float
        get() = if (tutorial.steps.isEmpty()) 0f else (completedStepNumbers.size.toFloat() / tutorial.steps.size)
}

class TutorialRepository(
    private val database: AppDatabase
) {
    private val favoriteDao = database.favoriteDao()
    private val progressDao = database.buildProgressDao()
    private val checklistDao = database.materialChecklistDao()

    fun getAllTutorialsStream(
        query: String,
        selectedCategory: TutorialCategory?,
        selectedDifficulty: BuildDifficulty?,
        onlyFavorites: Boolean
    ): Flow<List<TutorialItemUiState>> {
        return combine(
            favoriteDao.getAllFavoriteIds(),
            progressDao.getAllProgress()
        ) { favoriteIds, allProgress ->
            val favSet = favoriteIds.toSet()
            val progressMap = allProgress.associateBy { it.tutorialId }

            TutorialCatalog.tutorials.mapNotNull { tutorial ->
                val isFav = favSet.contains(tutorial.id)
                if (onlyFavorites && !isFav) return@mapNotNull null

                if (selectedCategory != null && selectedCategory != TutorialCategory.ALL && tutorial.category != selectedCategory) {
                    return@mapNotNull null
                }

                if (selectedDifficulty != null && tutorial.difficulty != selectedDifficulty) {
                    return@mapNotNull null
                }

                if (query.isNotBlank()) {
                    val normalizedQuery = normalize(query)
                    val matchTitle = normalize(tutorial.title).contains(normalizedQuery)
                    val matchDesc = normalize(tutorial.shortDescription).contains(normalizedQuery)
                    val matchTags = tutorial.tags.any { normalize(it).contains(normalizedQuery) }
                    val matchMats = tutorial.materials.any { normalize(it.name).contains(normalizedQuery) }
                    if (!matchTitle && !matchDesc && !matchTags && !matchMats) {
                        return@mapNotNull null
                    }
                }

                val progress = progressMap[tutorial.id]
                val completedSteps = progress?.completedStepsCsv
                    ?.split(",")
                    ?.mapNotNull { it.trim().toIntOrNull() }
                    ?.toSet() ?: emptySet()

                val totalSteps = tutorial.steps.size
                val percent = if (totalSteps > 0) completedSteps.size.toFloat() / totalSteps else 0f

                TutorialItemUiState(
                    tutorial = tutorial,
                    isFavorite = isFav,
                    completedStepsCount = completedSteps.size,
                    totalStepsCount = totalSteps,
                    progressPercent = percent,
                    isCompleted = progress?.isCompleted == true || (totalSteps > 0 && completedSteps.size >= totalSteps)
                )
            }
        }
    }

    fun getTutorialDetailStream(tutorialId: String): Flow<TutorialDetailUiState?> {
        val tutorial = TutorialCatalog.tutorials.find { it.id == tutorialId } ?: return kotlinx.coroutines.flow.flowOf(null)

        return combine(
            favoriteDao.isFavorite(tutorialId),
            progressDao.getProgress(tutorialId),
            checklistDao.getCheckedMaterialIds(tutorialId)
        ) { isFav, progress, checkedMaterials ->
            val completedSet = progress?.completedStepsCsv
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?.toSet() ?: emptySet()

            TutorialDetailUiState(
                tutorial = tutorial,
                isFavorite = isFav,
                completedStepNumbers = completedSet,
                checkedMaterialIds = checkedMaterials.toSet(),
                currentStepIndex = progress?.currentStepIndex ?: 0,
                userNotes = progress?.userNotes ?: "",
                isFinished = progress?.isCompleted == true || (tutorial.steps.isNotEmpty() && completedSet.size >= tutorial.steps.size)
            )
        }
    }

    suspend fun toggleFavorite(tutorialId: String, currentStatus: Boolean) {
        if (currentStatus) {
            favoriteDao.deleteFavorite(tutorialId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(tutorialId))
        }
    }

    suspend fun toggleStepCompleted(tutorialId: String, stepNumber: Int) {
        val progress = progressDao.getProgress(tutorialId)
        // Read current once
        var currentEntity: BuildProgressEntity? = null
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val list = database.buildProgressDao().getAllProgress()
            // We can directly query by reading a snapshot or querying
        }
        // Let's create an atomic update in Dao or helper
        updateStepProgress(tutorialId, stepNumber)
    }

    private suspend fun updateStepProgress(tutorialId: String, stepNumber: Int) {
        val tutorial = TutorialCatalog.tutorials.find { it.id == tutorialId } ?: return
        // Fetch current snapshot
        val existing = database.openHelper.readableDatabase
        var currentCsv = ""
        var currentStepIdx = 0
        var userNotes = ""
        val cursor = existing.query(
            "SELECT completedStepsCsv, currentStepIndex, userNotes FROM build_progress WHERE tutorialId = ?",
            arrayOf(tutorialId)
        )
        if (cursor.moveToFirst()) {
            currentCsv = cursor.getString(0) ?: ""
            currentStepIdx = cursor.getInt(1)
            userNotes = cursor.getString(2) ?: ""
        }
        cursor.close()

        val stepList = currentCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableSet()
        if (stepList.contains(stepNumber)) {
            stepList.remove(stepNumber)
        } else {
            stepList.add(stepNumber)
        }

        val newCsv = stepList.sorted().joinToString(",")
        val isFinished = stepList.size >= tutorial.steps.size

        progressDao.saveProgress(
            BuildProgressEntity(
                tutorialId = tutorialId,
                completedStepsCsv = newCsv,
                isCompleted = isFinished,
                currentStepIndex = (stepNumber - 1).coerceAtLeast(0),
                userNotes = userNotes,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun setStepIndex(tutorialId: String, index: Int) {
        val tutorial = TutorialCatalog.tutorials.find { it.id == tutorialId } ?: return
        var currentCsv = ""
        var userNotes = ""
        val cursor = database.openHelper.readableDatabase.query(
            "SELECT completedStepsCsv, userNotes FROM build_progress WHERE tutorialId = ?",
            arrayOf(tutorialId)
        )
        if (cursor.moveToFirst()) {
            currentCsv = cursor.getString(0) ?: ""
            userNotes = cursor.getString(1) ?: ""
        }
        cursor.close()

        val stepList = currentCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
        progressDao.saveProgress(
            BuildProgressEntity(
                tutorialId = tutorialId,
                completedStepsCsv = currentCsv,
                isCompleted = stepList.size >= tutorial.steps.size,
                currentStepIndex = index,
                userNotes = userNotes,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleMaterialChecked(tutorialId: String, materialId: String, currentChecked: Boolean) {
        if (currentChecked) {
            checklistDao.deleteMaterialChecked(tutorialId, materialId)
        } else {
            checklistDao.setMaterialChecked(MaterialCheckedEntity(tutorialId, materialId, true))
        }
    }

    suspend fun resetProgress(tutorialId: String) {
        progressDao.deleteProgress(tutorialId)
        checklistDao.clearChecklistForTutorial(tutorialId)
    }

    suspend fun saveNotes(tutorialId: String, notes: String) {
        val tutorial = TutorialCatalog.tutorials.find { it.id == tutorialId } ?: return
        var currentCsv = ""
        var currentStepIdx = 0
        val cursor = database.openHelper.readableDatabase.query(
            "SELECT completedStepsCsv, currentStepIndex FROM build_progress WHERE tutorialId = ?",
            arrayOf(tutorialId)
        )
        if (cursor.moveToFirst()) {
            currentCsv = cursor.getString(0) ?: ""
            currentStepIdx = cursor.getInt(1)
        }
        cursor.close()

        val stepList = currentCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
        progressDao.saveProgress(
            BuildProgressEntity(
                tutorialId = tutorialId,
                completedStepsCsv = currentCsv,
                isCompleted = stepList.size >= tutorial.steps.size,
                currentStepIndex = currentStepIdx,
                userNotes = notes,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun normalize(str: String): String {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .trim()
    }
}
