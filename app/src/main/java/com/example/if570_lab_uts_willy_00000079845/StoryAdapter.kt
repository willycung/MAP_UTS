package com.example.if570_lab_uts_willy_00000079845

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import android.text.format.DateFormat
import com.google.firebase.Timestamp

class StoryAdapter(private val storyList: List<StoryData>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val captionTextView: TextView = itemView.findViewById(R.id.captionTextView)
        val storyImageView: ImageView = itemView.findViewById(R.id.storyImageView)
        val likeButton: Button = itemView.findViewById(R.id.like_button)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)  // Bind timestampTextView here
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val currentStory = storyList[position]
        holder.captionTextView.text = currentStory.caption

        // Handle image loading
        if (!currentStory.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(currentStory.imageUrl).into(holder.storyImageView)
            holder.storyImageView.visibility = View.VISIBLE
        } else {
            holder.storyImageView.visibility = View.GONE  // Hide ImageView if there's no image
        }

        // Display the timestamp (if available)
        currentStory.timestamp?.let {
            val date = DateFormat.format("dd MMM yyyy", it.toDate()).toString()
            holder.timestampTextView.text = date
        }

        // Handle like button
        holder.likeButton.text = if (currentStory.isLiked) "Unlike" else "Like"
        holder.likeButton.setOnClickListener {
            toggleLike(currentStory, holder)
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    private fun toggleLike(story: StoryData, holder: StoryViewHolder) {
        // Toggle the like state locally
        story.isLiked = !story.isLiked

        // Update the UI immediately
        holder.likeButton.text = if (story.isLiked) "Unlike" else "Like"

        // Update the like status in Firestore
        val firestore = FirebaseFirestore.getInstance()

        if (story.id.isNotEmpty()) {
            firestore.collection("stories").document(story.id)
                .update("isLiked", story.isLiked)
                .addOnSuccessListener {
                    Log.d("Firestore", "Like status updated for story: ${story.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error updating like status for story: ${story.id}", e)
                    // Optionally: Revert the UI change if the update fails
                    story.isLiked = !story.isLiked
                    holder.likeButton.text = if (story.isLiked) "Unlike" else "Like"
                }
        } else {
            Log.e("Firestore", "Story ID is null or empty, cannot update like status.")
        }
    }
}