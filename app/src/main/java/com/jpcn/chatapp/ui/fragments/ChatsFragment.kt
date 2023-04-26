package com.jpcn.chatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.jpcn.chatapp.databinding.FragmentChatsBinding
import com.jpcn.chatapp.model.ChatUser
import com.jpcn.chatapp.model.Token
import com.jpcn.chatapp.model.User
import com.jpcn.chatapp.ui.adapters.UserAdapter

class ChatsFragment : Fragment() {
    lateinit var binding: FragmentChatsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater)
        binding.recyclerViewChats.setHasFixedSize(true)
        binding.recyclerViewChats.layoutManager = LinearLayoutManager(context)
        val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val chatUsers = mutableListOf<ChatUser>()
        FirebaseDatabase.getInstance().getReference("ChatUser").child(firebaseUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    chatUsers.clear()
                    for (snapshot in dataSnapshot.children) {
                        snapshot.getValue(ChatUser::class.java)?.let {
                            chatUsers.add(it)
                        }
                    }
                    chatLists(chatUsers)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) updateToken(task.result, firebaseUserId)
        }
        return binding.root
    }

    private fun updateToken(valueToken: String?, firebaseUserId: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(valueToken)
        reference.child(firebaseUserId).setValue(token)
    }

    private fun chatLists(chatUsers: MutableList<ChatUser>) {
        val users = mutableListOf<User>()
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    for (chatUser in chatUsers) {
                        assert(user != null)
                        if (user!!.id == chatUser.id) {
                            users.add(user)
                        }
                    }
                }
                val userAdapter = UserAdapter(context, users, true)
                binding.recyclerViewChats.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}