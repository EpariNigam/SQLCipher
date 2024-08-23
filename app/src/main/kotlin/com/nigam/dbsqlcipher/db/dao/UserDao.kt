package com.nigam.dbsqlcipher.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nigam.dbsqlcipher.db.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("DELETE FROM ${User.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${User.TABLE_NAME}")
    suspend fun getAll(): List<User>
}
