package com.sarfrazqureshi.clipvault.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClipDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ClipItem)

    @Query("SELECT * FROM clip_items WHERE type = :type ORDER BY timestamp DESC")
    fun getByType(type: ClipType): LiveData<List<ClipItem>>

    @Query("SELECT content FROM clip_items ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastSavedContent(): String?

    @Query("DELETE FROM clip_items WHERE id = :id")
    suspend fun delete(id: Long)
}
