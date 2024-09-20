package com.nigam.dbsqlcipher.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nigam.dbsqlcipher.db.entities.StringEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class StringEntity(
    @PrimaryKey
    @ColumnInfo(name = COL_KEY)
    val key: String,
    @ColumnInfo(name = COL_DATA)
    val data: String
) {
    companion object {
        const val TABLE_NAME = "string_entity"
        const val COL_KEY = "key"
        const val COL_DATA = "data"
    }
}
