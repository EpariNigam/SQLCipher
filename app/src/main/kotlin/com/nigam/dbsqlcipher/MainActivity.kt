package com.nigam.dbsqlcipher

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nigam.dbsqlcipher.db.EncryptedAppDatabase
import com.nigam.dbsqlcipher.db.dao.UserDao
import com.nigam.dbsqlcipher.db.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var db: EncryptedAppDatabase
    private lateinit var userDao: UserDao

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
        tvData.movementMethod = ScrollingMovementMethod()
    }

    fun insertUsers(view: View) {
        lifecycleScope.launch(Dispatchers.Main) {
            view.isEnabled = false
            tvStatus.text = getString(R.string.status_s, "Inserting")
            withContext(Dispatchers.IO) {
                delay(1000)
                for (i in 1..20) {
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
            withContext(Dispatchers.IO) {
                delay(1000)
                users = userDao.getAll()
            }
            tvData.text = users.toString()
            tvStatus.text = getString(R.string.status_s, "Fetched")
            view.isEnabled = true
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
