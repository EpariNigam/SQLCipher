package com.nigam.dbsqlcipher.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nigam.dbsqlcipher.db.dao.StringDao
import com.nigam.dbsqlcipher.db.dao.UserDao
import com.nigam.dbsqlcipher.db.entities.StringEntity
import com.nigam.dbsqlcipher.db.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory


@Database(
    entities = [
        User::class,
        StringEntity::class
    ],
    exportSchema = true,
    version = EncryptedAppDatabase.VERSION
)
/**
 * Room database which is encrypted using SQLCipher
 */
abstract class EncryptedAppDatabase : RoomDatabase() {
    companion object {
        const val VERSION = 1
        private const val DB_NAME = "encrypted_app_db"
        private val DBKEY = "mydatabasekey".toCharArray()

        @Volatile
        private var INSTANCE: EncryptedAppDatabase? = null

        fun getInstance(context: Context): EncryptedAppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): EncryptedAppDatabase {
            System.loadLibrary("sqlcipher")
            val passPhrase = getBytes(DBKEY)
            val factory = SupportOpenHelperFactory(passPhrase)

            return Room.databaseBuilder(context, EncryptedAppDatabase::class.java, DB_NAME)
                .openHelperFactory(factory)
                .setQueryExecutor(Dispatchers.IO.asExecutor())
                .setTransactionExecutor(Dispatchers.IO.asExecutor())
                .build()
        }

        @Suppress("SameParameterValue")
        private fun getBytes(data: CharArray?): ByteArray {
            if (data == null || data.isEmpty()) return byteArrayOf()
            return String(data).toByteArray(Charsets.UTF_8)
        }
    }


    abstract fun userDao(): UserDao

    abstract fun stringDao(): StringDao

}
