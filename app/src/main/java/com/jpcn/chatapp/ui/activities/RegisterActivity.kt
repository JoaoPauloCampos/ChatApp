package com.jpcn.chatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jpcn.chatapp.R
import com.jpcn.chatapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding)

        binding.buttonRegister.setOnClickListener {
            validateRegisterFields(binding)
        }
    }

    private fun validateRegisterFields(binding: ActivityRegisterBinding) {
        val userNameValue = binding.username.text.toString()
        val emailValue = binding.mail.text.toString()
        val passwordValue = binding.password.text.toString()

        if (userNameValue.isBlank() || emailValue.isBlank() || passwordValue.isBlank()) {
            Toast.makeText(applicationContext, getString(R.string.register_error_message), Toast.LENGTH_SHORT).show()
        } else if (passwordValue.length < 6) {
            Toast.makeText(applicationContext, getString(R.string.passoword_error_msg), Toast.LENGTH_SHORT).show()
        } else {
            register(userNameValue, emailValue, passwordValue)
        }
    }

    private fun setupToolbar(binding: ActivityRegisterBinding) {
        setSupportActionBar(binding.header.toolbar)
        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun register(username: String, mail: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                persistUser(auth, username)
            } else {
                Toast.makeText(applicationContext, getString(R.string.register_failed_message), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun persistUser(auth: FirebaseAuth, username: String) {
        val firebaseUser = auth.currentUser
        val userId = firebaseUser!!.uid
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users").child(userId)
        val hashMap = hashMapOf(
            "id" to userId,
            "username" to username,
            "imageURL" to "default"
        )
        reference.setValue(hashMap).addOnCompleteListener { task2 ->
            if (task2.isSuccessful) {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }
}