package com.nigam.dbsqlcipher

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nigam.dbsqlcipher.db.EncryptedAppDatabase
import com.nigam.dbsqlcipher.db.dao.StringDao
import com.nigam.dbsqlcipher.db.dao.UserDao
import com.nigam.dbsqlcipher.db.entities.StringEntity
import com.nigam.dbsqlcipher.db.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.time.measureTimedValue

class MainActivity : AppCompatActivity() {

    private lateinit var db: EncryptedAppDatabase
    private lateinit var userDao: UserDao
    private lateinit var stringDao: StringDao

    private lateinit var tvStatus: TextView
    private lateinit var tvData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        tvStatus = findViewById(R.id.tv_status)
        tvData = findViewById(R.id.tv_data)
        db = EncryptedAppDatabase.getInstance(this)
        userDao = db.userDao()
        stringDao = db.stringDao()
        tvData.movementMethod = ScrollingMovementMethod()
    }

    fun insertUsers(view: View) {
        lifecycleScope.launch(Dispatchers.Main) {
            view.isEnabled = false
            tvStatus.text = getString(R.string.status_s, "Inserting")
            withContext(Dispatchers.IO) {
                delay(1000)
                for (i in 1..10_000) {
                    userDao.insert(User(name = "User ${UUID.randomUUID()}"))
                }
            }
            tvStatus.text = getString(R.string.status_s, "Inserted")
            view.isEnabled = true
        }
    }

    fun deleteUsers(view: View) {
        lifecycleScope.launch(Dispatchers.Main) {
            view.isEnabled = false
            tvStatus.text = getString(R.string.status_s, "Deleting")
            withContext(Dispatchers.IO) {
                delay(1000)
                userDao.deleteAll()
            }
            tvStatus.text = getString(R.string.status_s, "Deleted")
            view.isEnabled = true
        }
    }

    fun fetchUsers(view: View) {
        var users: List<User>
        lifecycleScope.launch(Dispatchers.Main) {
            view.isEnabled = false
            tvStatus.text = getString(R.string.status_s, "Fetching")
            var timeTaken: Long
            withContext(Dispatchers.IO) {
                delay(1000)
                val value = measureTimedValue {
                    userDao.getAll()
                }
                Log.d(TAG, "fetchUsers: Time taken: ${value.duration.inWholeMilliseconds}ms")
                timeTaken = value.duration.inWholeMilliseconds
                users = value.value
            }
            tvData.text = users.toString()
            tvStatus.text = getString(R.string.status_time_taken_s, "Fetched", timeTaken.toString())
            view.isEnabled = true
        }
    }

    fun insertString(view: View) {
        lifecycleScope.launch(Dispatchers.Main) {
            view.isEnabled = false
            tvStatus.text = getString(R.string.status_s, "Inserting")
            withContext(Dispatchers.IO) {
                delay(1000)
                stringDao.insert(StringEntity(KEY_DUMMY, getRandomString(1_00_000)))
            }
            tvStatus.text = getString(R.string.status_s, "Inserted")
            view.isEnabled = true
        }
    }

    fun fetchString(view: View) {
        var data: String
        lifecycleScope.launch(Dispatchers.Main) {
            view.isEnabled = false
            tvStatus.text = getString(R.string.status_s, "Fetching")
            var timeTaken: Long
            withContext(Dispatchers.IO) {
                delay(1000)
                val value = measureTimedValue {
                    stringDao.getData(KEY_DUMMY)
                }
                Log.d(TAG, "fetchUsers: Time taken: ${value.duration.inWholeMilliseconds}ms")
                timeTaken = value.duration.inWholeMilliseconds
                data = value.value.data
            }
            tvData.text = data
            tvStatus.text = getString(R.string.status_time_taken_s, "Fetched", timeTaken.toString())
            view.isEnabled = true
        }
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_DUMMY = "KEY_DUMMY"
    }
}
