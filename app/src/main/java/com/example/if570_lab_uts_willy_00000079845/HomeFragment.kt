package com.example.if570_lab_uts_willy_00000079845

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Load stories from Firestore
        loadStories()
    }

    private fun loadStories() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("stories")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val stories = result.toObjects(StoryData::class.java)

                // Check if stories contain valid data
                if (stories.isNotEmpty()) {
                    storyAdapter = StoryAdapter(stories)
                    recyclerView.adapter = storyAdapter
                } else {
                    Log.d("Firestore", "No stories found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error loading stories", e)
            }
    }
}