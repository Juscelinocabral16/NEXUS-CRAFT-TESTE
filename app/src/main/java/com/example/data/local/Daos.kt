package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT tutorialId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE tutorialId = :tutorialId)")
    fun isFavorite(tutorialId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE tutorialId = :tutorialId")
    suspend fun deleteFavorite(tutorialId: String)
}

@Dao
interface BuildProgressDao {
    @Query("SELECT * FROM build_progress WHERE tutorialId = :tutorialId")
    fun getProgress(tutorialId: String): Flow<BuildProgressEntity?>

    @Query("SELECT * FROM build_progress")
    fun getAllProgress(): Flow<List<BuildProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: BuildProgressEntity)

    @Query("DELETE FROM build_progress WHERE tutorialId = :tutorialId")
    suspend fun deleteProgress(tutorialId: String)
}

@Dao
interface MaterialChecklistDao {
    @Query("SELECT materialId FROM material_checklist WHERE tutorialId = :tutorialId AND isChecked = 1")
    fun getCheckedMaterialIds(tutorialId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMaterialChecked(item: MaterialCheckedEntity)

    @Query("DELETE FROM material_checklist WHERE tutorialId = :tutorialId AND materialId = :materialId")
    suspend fun deleteMaterialChecked(tutorialId: String, materialId: String)

    @Query("DELETE FROM material_checklist WHERE tutorialId = :tutorialId")
    suspend fun clearChecklistForTutorial(tutorialId: String)
}
