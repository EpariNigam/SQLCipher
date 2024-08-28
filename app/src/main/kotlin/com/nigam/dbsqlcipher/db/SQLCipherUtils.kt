package com.nigam.dbsqlcipher.db

import android.content.Context
import android.text.Editable
import android.util.Log
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Helps check DB state, whether encrypted/unencrypted, and also encrypts DB when needed. This is
 * necessary for seamlessly upgrading DBs which may have been previously unencrypted, to the
 * encrypted state. Ref:
 *
 * https://medium.com/@khambhaytajaydip/encrypting-an-existing-room-database-with-sqlcipher-in-android-50cdc98fe6c
 * https://github.com/commonsguy/cwac-saferoom/blob/master/saferoom/src/main/java/com/commonsware/cwac/saferoom/SQLCipherUtils.java
 * https://epifi.slack.com/archives/C01E60JJ0DP/p1715845300220719
 */
object SQLCipherUtils {
    /**
     * Determine whether or not this database appears to be encrypted, based
     * on whether we can open it without a passphrase.
     *
     * @param ctxt a Context
     * @param dbName the name of the database, as used with Room, SQLiteOpenHelper,
     * etc.
     * @return the detected state of the database
     */
    fun getDatabaseState(ctxt: Context, dbName: String?): State {
        System.loadLibrary("sqlcipher")

        return (getDatabaseState(ctxt.getDatabasePath(dbName)))
    }

    /**
     * Determine whether or not this database appears to be encrypted, based
     * on whether we can open it without a passphrase.
     *
     * NOTE: You are responsible for ensuring that net.sqlcipher.database.SQLiteDatabase.loadLibs()
     * is called before calling this method. This is handled automatically with the
     * getDatabaseState() method that takes a Context as a parameter.
     *
     * @param dbPath a File pointing to the database
     * @return the detected state of the database
     */
    fun getDatabaseState(dbPath: File): State {
        if (dbPath.exists()) {
            var db: SQLiteDatabase? = null

            try {
                db = SQLiteDatabase.openDatabase(
                    dbPath.absolutePath,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )
                db.version

                return (State.UNENCRYPTED)
            } catch (e: Exception) {
                return (State.ENCRYPTED)
            } finally {
                db?.close()
            }
        }

        return (State.DOES_NOT_EXIST)
    }

    /**
     * Replaces this database with a version encrypted with the supplied
     * passphrase, deleting the original. Do not call this while the database
     * is open, which includes during any Room migrations.
     *
     * The passphrase is untouched in this call. If you are going to turn around
     * and use it with SafeHelperFactory.fromUser(), fromUser() will clear the
     * passphrase. If not, please set all bytes of the passphrase to 0 or something
     * to clear out the passphrase.
     *
     * @param ctxt a Context
     * @param dbName the name of the database, as used with Room, SQLiteOpenHelper,
     * etc.
     * @param editor the passphrase, such as obtained by calling getText() on an
     * EditText
     * @throws IOException
     */
    @Throws(IOException::class)
    fun encrypt(ctxt: Context, dbName: String?, editor: Editable) {
        val passphrase = CharArray(editor.length)

        editor.getChars(0, editor.length, passphrase, 0)
        encrypt(ctxt, dbName, passphrase)
    }

    /**
     * Replaces this database with a version encrypted with the supplied
     * passphrase, deleting the original. Do not call this while the database
     * is open, which includes during any Room migrations.
     *
     * The passphrase is untouched in this call. If you are going to turn around
     * and use it with SafeHelperFactory.fromUser(), fromUser() will clear the
     * passphrase. If not, please set all bytes of the passphrase to 0 or something
     * to clear out the passphrase.
     *
     * @param ctxt a Context
     * @param dbName the name of the database, as used with Room, SQLiteOpenHelper,
     * etc.
     * @param passphrase the passphrase from the user
     * @throws IOException
     */
    @Throws(IOException::class)
    fun encrypt(ctxt: Context, dbName: String?, passphrase: CharArray?) {
        encrypt(ctxt, ctxt.getDatabasePath(dbName), EncryptedAppDatabase.getBytes(passphrase))
    }

    /**
     * Replaces this database with a version encrypted with the supplied
     * passphrase, deleting the original. Do not call this while the database
     * is open, which includes during any Room migrations.
     *
     * The passphrase is untouched in this call. If you are going to turn around
     * and use it with SafeHelperFactory.fromUser(), fromUser() will clear the
     * passphrase. If not, please set all bytes of the passphrase to 0 or something
     * to clear out the passphrase.
     *
     * @param ctxt a Context
     * @param dbName the name of the database, as used with Room, SQLiteOpenHelper,
     * etc.
     * @param passphrase the passphrase
     * @throws IOException
     */
    @Throws(IOException::class)
    fun encrypt(ctxt: Context, dbName: String?, passphrase: ByteArray?) {
        encrypt(ctxt, ctxt.getDatabasePath(dbName), passphrase)
    }

    /**
     * Replaces this database with a version encrypted with the supplied
     * passphrase, deleting the original. Do not call this while the database
     * is open, which includes during any Room migrations.
     *
     * The passphrase is untouched in this call. If you are going to turn around
     * and use it with SafeHelperFactory.fromUser(), fromUser() will clear the
     * passphrase. If not, please set all bytes of the passphrase to 0 or something
     * to clear out the passphrase.
     *
     * @param ctxt a Context
     * @param originalFile a File pointing to the database
     * @param passphrase the passphrase from the user
     * @throws IOException
     */
    @Throws(IOException::class)
    fun encrypt(ctxt: Context, originalFile: File, passphrase: CharArray?) {
        encrypt(ctxt, originalFile, EncryptedAppDatabase.getBytes(passphrase))
    }

    /**
     * Replaces this database with a version encrypted with the supplied
     * passphrase, deleting the original. Do not call this while the database
     * is open, which includes during any Room migrations.
     *
     * The passphrase is untouched in this call. If you are going to turn around
     * and use it with SafeHelperFactory.fromUser(), fromUser() will clear the
     * passphrase. If not, please set all bytes of the passphrase to 0 or something
     * to clear out the passphrase.
     *
     * @param ctxt a Context
     * @param originalFile a File pointing to the database
     * @param passphrase the passphrase from the user
     * @throws IOException
     */
    @Throws(IOException::class)
    fun encrypt(ctxt: Context, originalFile: File, passphrase: ByteArray?) {
        System.loadLibrary("sqlcipher")

        if (originalFile.exists()) {
            val newFile = File.createTempFile(
                "sqlcipherutils", "tmp",
                ctxt.cacheDir
            )
            var db =
                SQLiteDatabase.openDatabase(
                    originalFile.absolutePath,
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                )
            val version: Int = db.version
            Log.d(TAG, "version: $version")

            db.close()

            db = SQLiteDatabase.openDatabase(
                newFile.absolutePath, passphrase,
                null, SQLiteDatabase.OPEN_READWRITE, null, null
            )

            val st = db.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''")

            st.bindString(1, originalFile.absolutePath)
            st.execute()
            val cursor = db.rawQuery("SELECT sqlcipher_export('main', 'plaintext')")
            if (cursor?.moveToFirst() == true) {
                cursor.close()
            }
            db.rawExecSQL("DETACH DATABASE plaintext")
            db.version = version
            st.close()
            db.close()

            originalFile.delete()
            newFile.renameTo(originalFile)
        } else {
            throw FileNotFoundException(originalFile.absolutePath + " not found")
        }
    }

    @Throws(IOException::class)
    fun decrypt(ctxt: Context, dbName: String?, passphrase: ByteArray?) {
        decrypt(ctxt, ctxt.getDatabasePath(dbName), passphrase)
    }

    /**
     * Replaces this database with a decrypted version, deleting the original
     * encrypted database. Do not call this while the database is open, which
     * includes during any Room migrations.
     *
     * The passphrase is untouched in this call. Please set all bytes of the
     * passphrase to 0 or something to clear out the passphrase if you are done
     * with it.
     *
     * @param ctxt a Context
     * @param originalFile a File pointing to the encrypted database
     * @param passphrase the passphrase from the user for the encrypted database
     * @throws IOException
     */
    @Throws(IOException::class)
    fun decrypt(ctxt: Context, originalFile: File, passphrase: CharArray?) {
        decrypt(ctxt, originalFile, EncryptedAppDatabase.getBytes(passphrase))
    }

    /**
     * Replaces this database with a decrypted version, deleting the original
     * encrypted database. Do not call this while the database is open, which
     * includes during any Room migrations.
     *
     * The passphrase is untouched in this call. Please set all bytes of the
     * passphrase to 0 or something to clear out the passphrase if you are done
     * with it.
     *
     * @param ctxt a Context
     * @param originalFile a File pointing to the encrypted database
     * @param passphrase the passphrase from the user for the encrypted database
     * @throws IOException
     */
    @Throws(IOException::class)
    fun decrypt(ctxt: Context, originalFile: File, passphrase: ByteArray?) {
        System.loadLibrary("sqlcipher")

        if (originalFile.exists()) {
            val newFile =
                File.createTempFile(
                    "sqlcipherutils", "tmp",
                    ctxt.cacheDir
                )
            var db =
                SQLiteDatabase.openDatabase(
                    originalFile.absolutePath,
                    passphrase, null, SQLiteDatabase.OPEN_READWRITE, null, null
                )

            val st = db.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''")

            st.bindString(1, newFile.absolutePath)
            st.execute()

            val cursor = db.rawQuery("SELECT sqlcipher_export('plaintext')")
            if (cursor?.moveToFirst() == true) {
                cursor.close()
            }
            db.rawExecSQL("DETACH DATABASE plaintext")

            val version: Int = db.version

            st.close()
            db.close()

            db = SQLiteDatabase.openDatabase(
                newFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READWRITE
            )
            db.version = version
            db.close()

            originalFile.delete()
            newFile.renameTo(originalFile)
        } else {
            throw FileNotFoundException(originalFile.absolutePath + " not found")
        }
    }

    /**
     * The detected state of the database, based on whether we can open it
     * without a passphrase.
     */
    enum class State {
        DOES_NOT_EXIST, UNENCRYPTED, ENCRYPTED
    }

    private const val TAG = "SQLCiperUtils"
}
