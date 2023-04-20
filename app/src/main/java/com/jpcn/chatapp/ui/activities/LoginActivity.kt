package com.jpcn.chatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.jpcn.chatapp.R
import com.jpcn.chatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.header.toolbar)

        binding.buttonLogin.setOnClickListener {
            val emailValue = binding.mail.text.toString()
            val passwordValue = binding.password.text.toString()
            validateLoginFields(emailValue, passwordValue)
        }
    }

    private fun validateLoginFields(emailValue: String, passwordValue: String) {
        if (emailValue.isBlank() || passwordValue.isBlank()) {
            Toast.makeText(applicationContext, R.string.invalid_login, Toast.LENGTH_SHORT).show()
        } else {
            doLogin(emailValue, passwordValue)
        }
    }

    private fun doLogin(emailValue: String, passwordValue: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Falha ao realizar o login", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}