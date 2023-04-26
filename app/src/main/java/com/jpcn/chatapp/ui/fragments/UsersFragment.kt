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
import com.jpcn.chatapp.databinding.FragmentUsersBinding
import com.jpcn.chatapp.model.User
import com.jpcn.chatapp.ui.adapters.UserAdapter

class UsersFragment : Fragment() {
    lateinit var binding: FragmentUsersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater)

        binding.recyclerViewUsers.setHasFixedSize(true)
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(context)
        readUsers()
        return binding.root
    }

    private fun readUsers() {
        val users = mutableListOf<User>()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                for (snapshot in dataSnapshot.children) {
                    snapshot.getValue(User::class.java)?.let { user ->
                        firebaseUser?.uid?.let {
                            if (user.id != it) users.add(user)
                        }
                    }
                }
                val userAdapter = UserAdapter(context, users, false)
                binding.recyclerViewUsers.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}