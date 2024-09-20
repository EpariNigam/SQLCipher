package com.nigam.dbsqlcipher.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nigam.dbsqlcipher.db.entities.StringEntity

@Dao
interface StringDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(string: StringEntity)

    @Query("SELECT * FROM ${StringEntity.TABLE_NAME} WHERE `${StringEntity.COL_KEY}` = :key")
    fun getData(key: String): StringEntity
}