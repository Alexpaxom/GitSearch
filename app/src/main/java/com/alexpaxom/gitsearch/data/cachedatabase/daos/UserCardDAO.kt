package com.alexpaxom.gitsearch.data.cachedatabase.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexpaxom.gitsearch.domain.entities.UserCard

@Dao
interface UserCardDAO {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByID(userId: Long): UserCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserCard)

    @Query("DELETE FROM users")
    fun deleteAll()
}