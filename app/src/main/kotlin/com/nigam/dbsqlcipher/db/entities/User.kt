package com.nigam.dbsqlcipher.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nigam.dbsqlcipher.db.entities.User.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID)
    val id: Long = 0,
    @ColumnInfo(name = COL_NAME)
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "user"
        const val COL_ID = "id"
        const val COL_NAME = "name"
    }
}
