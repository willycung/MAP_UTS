package com.example.if570_lab_uts_willy_00000079845

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already logged in
        checkUserLogin()

        // If the user is logged in, load the main content
        setContentView(R.layout.activity_main)

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment to HomeFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        // Set listener for BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AddStoryFragment())
                        .commit()
                    true
                }
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Sign out the user when the app is backgrounded or closed
        auth.signOut()
    }

    // Method to check if the user is logged in, and redirect if they are not
    private fun checkUserLogin() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // If the user is not logged in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            // Clear back stack to prevent going back to MainActivity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close MainActivity to prevent navigating back to it
        }
    }
}