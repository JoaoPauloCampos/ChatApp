package com.jpcn.chatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jpcn.chatapp.R
import com.jpcn.chatapp.databinding.ActivityMessageBinding
import com.jpcn.chatapp.model.Chat
import com.jpcn.chatapp.model.User
import com.jpcn.chatapp.ui.adapters.MessageAdapter
import com.squareup.picasso.Picasso

class MessageActivity : AppCompatActivity() {
    var notify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding)
        setupRecyclerVIew(binding)

        val userId = intent.getStringExtra("userId").orEmpty()

        FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
            setupSendMessage(binding, firebaseUser, userId)
            setupUserInfo(userId, binding, firebaseUser)
        }
    }

    private fun setupSendMessage(
        binding: ActivityMessageBinding,
        firebaseUser: FirebaseUser,
        userId: String
    ) {
        binding.buttonSend.setOnClickListener {
            notify = true
            val message = binding.textSend.text.toString()
            if (message.isNotBlank()) {
                sendMessage(firebaseUser.uid, userId, message)
            } else {
                Toast.makeText(this@MessageActivity, "You can't send empty message", Toast.LENGTH_SHORT).show()
            }
            binding.textSend.setText("")
        }
    }

    private fun setupUserInfo(
        userId: String,
        binding: ActivityMessageBinding,
        firebaseUser: FirebaseUser
    ) = FirebaseDatabase.getInstance().getReference("users").child(userId)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(User::class.java)?.let { user ->
                    binding.username.text = user.username
                    if (user.imageURL == "default") {
                        binding.profileImage.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        Picasso.get().load(user.imageURL).into(binding.profileImage)
                    }
                    readMessage(firebaseUser.uid, userId, user.imageURL, binding)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) = Unit
        })

    private fun setupRecyclerVIew(binding: ActivityMessageBinding) {
        binding.recyclerViewMessages.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        binding.recyclerViewMessages.layoutManager = linearLayoutManager
    }

    private fun setupToolbar(binding: ActivityMessageBinding) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@MessageActivity,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }

    private fun sendMessage(sender: String, receiver: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, Any?>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        reference.child("Chats").push().setValue(hashMap)
        putChatUser(receiver, sender, message)
        putChatUser(sender, receiver, message)
    }

    private fun putChatUser(receiver: String?, sender: String?, message: String) {
        val chatsReference = FirebaseDatabase.getInstance().getReference("ChatUser")
            .child(receiver!!)
            .child(sender!!)
        chatsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatsReference.child("id").setValue(sender)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val reference = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    private fun readMessage(userId: String, userid: String?, imageUrl: String?, binding: ActivityMessageBinding) {
        val chats = arrayListOf<Chat>()
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chats.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat?.receiver == userId && chat.sender == userid ||
                        chat?.receiver == userid && chat?.sender == userId
                    ) {
                        chats.add(chat)
                    }
                }
                val messageAdapter = MessageAdapter(this@MessageActivity, chats, imageUrl)
                binding.recyclerViewMessages.adapter = messageAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun status(status: String) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val reference = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
            val hashMap = HashMap<String, Any>()
            hashMap["status"] = status
            reference.updateChildren(hashMap)
        }
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}