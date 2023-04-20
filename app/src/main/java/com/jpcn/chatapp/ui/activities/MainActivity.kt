package com.jpcn.chatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jpcn.chatapp.R
import com.jpcn.chatapp.databinding.ActivityMainBinding
import com.jpcn.chatapp.model.User
import com.jpcn.chatapp.ui.fragments.ChatsFragment
import com.jpcn.chatapp.ui.fragments.UsersFragment
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding)
        setUserInfo(binding)
        setupPageAdapter(binding)
    }

    private fun setupPageAdapter(binding: ActivityMainBinding) {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
        viewPagerAdapter.addFragment(UsersFragment(), "Users")
        binding.viewPager.adapter = viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setUserInfo(binding: ActivityMainBinding) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                binding.username.text = user?.username.orEmpty()
                if (user?.imageURL == "default") {
                    binding.profileImage.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Picasso.get().load(user?.imageURL).into(binding.profileImage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setupToolbar(binding: ActivityMainBinding) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this@MainActivity,
                        StartActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                return true
            }
        }
        return false
    }

    internal inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        private val fragments: ArrayList<Fragment> = ArrayList()
        private val titles: ArrayList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }

    private fun setStatus(status: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        setStatus("online")
    }

    override fun onPause() {
        super.onPause()
        setStatus("offline")
    }
}