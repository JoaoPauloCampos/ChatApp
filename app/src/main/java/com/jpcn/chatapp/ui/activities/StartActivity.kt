package com.jpcn.chatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jpcn.chatapp.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().currentUser?.let {
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener {
            startActivity(
                Intent(this@StartActivity, LoginActivity::class.java)
            )
        }
        binding.buttonRegister.setOnClickListener {
            startActivity(
                Intent(this@StartActivity, RegisterActivity::class.java)
            )
        }
    }
}