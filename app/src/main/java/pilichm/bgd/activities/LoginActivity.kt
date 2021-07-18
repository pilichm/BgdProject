package pilichm.bgd.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.snappydb.DB
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import kotlinx.android.synthetic.main.activity_login.*
import pilichm.bgd.R


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

            btnLogin.setOnClickListener {
            val userName = txtUserName.text.toString()
            val password = txtPassword.text.toString()

            when (checkUser(userName, password)) {
                0 -> {
                    openListOfSeasonsActivity(userName)
                }
                1 -> {
                    Toast.makeText(this@LoginActivity, "Username and password don't match!", Toast.LENGTH_LONG).show()
                }
                else -> {
                    /**
                     * If user doesn't exist ask him if he wants to create account.
                     */
                    /**
                     * If user doesn't exist ask him if he wants to create account.
                     */
                    /**
                     * If user doesn't exist ask him if he wants to create account.
                     */
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
                    builder.setTitle("Create account?")

                    builder.setPositiveButton(
                        "Create"
                    ) { _, _ -> if (addUser(userName, password)){
                        Toast.makeText(this@LoginActivity, "Account created!", Toast.LENGTH_LONG).show()
                        openListOfSeasonsActivity(userName)
                    } else {
                        Toast.makeText(this@LoginActivity, "Error creating account!", Toast.LENGTH_LONG).show()
                    }
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog.cancel() }

                    builder.show()
                }
            }
        }
    }

    /***
     * Checks if user with submitted password exists in snappy db.
     * 0 - user exists and password matches,
     * 1- user exists but password doesn't match,
     * 2 - user doesn't exist.
     */
    private fun checkUser(userName: String, password: String): Int{
        return try {
            val snappydb: DB = DBFactory.open(application)
            val storedPassword = snappydb.get(userName)
            if (!storedPassword.isNullOrEmpty()&&storedPassword.equals(password)) 0 else 1
        } catch (e: SnappydbException){
            Log.e("LoginActivity", "Error getting value from SnappyDB!")
            2
        }
    }

    /**
     * Adds new user with given name and password to snappy db.
     */
    private fun addUser(userName: String, password: String): Boolean{
        return try {
            val snappydb: DB = DBFactory.open(application)
            snappydb.put(userName, password)
            true
        } catch (e: SnappydbException){
            Log.e("LoginActivity", "Error getting value from SnappyDB!")
            false
        }
    }

    private fun openListOfSeasonsActivity(userName: String){
        val myIntent = Intent(this@LoginActivity, ListOfSeasonsActivity::class.java)
        myIntent.putExtra("userName", userName);
        this@LoginActivity.startActivity(myIntent);
    }
}