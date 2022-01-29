package com.alexpaxom.gitsearch.data.cachedatabase.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard

@Dao
interface RepositoryCardDAO {
    @Query("SELECT * FROM repositories")
    fun getAll(): List<RepositoryCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(channels: List<RepositoryCard>)

    @Query("DELETE FROM repositories")
    fun deleteAll()
}