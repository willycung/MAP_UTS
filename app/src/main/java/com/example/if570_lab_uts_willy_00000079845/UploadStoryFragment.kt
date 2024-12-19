package com.example.if570_lab_uts_willy_00000079845

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class UploadStoryFragment : Fragment() {

    private lateinit var captionEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var storyImageView: ImageView
    private var imageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        captionEditText = view.findViewById(R.id.story_caption)
        storyImageView = view.findViewById(R.id.story_image)
        uploadButton = view.findViewById(R.id.upload_button)

        storyImageView.setOnClickListener {
            checkPermissionAndPickImage()
        }

        uploadButton.setOnClickListener {
            uploadStory()
        }
    }

    private fun checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED) {
            // Request permission
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
        } else {
            // Permission already granted
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            storyImageView.setImageURI(imageUri) // Display selected image
        }
    }

    private fun uploadStory() {
        val caption = captionEditText.text.toString()

        if (caption.isNotEmpty()) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance().reference.child("story_images/${UUID.randomUUID()}.jpg")

            if (imageUri != null) {
                storage.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        saveStoryToFirestore(caption, uri.toString())
                    }
                }
            } else {
                saveStoryToFirestore(caption, null)
            }
        }
    }

    private fun saveStoryToFirestore(caption: String, imageUrl: String?) {
        val story = hashMapOf(
            "caption" to caption,
            "imageUrl" to imageUrl,
            "timestamp" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance().collection("stories").add(story)
            .addOnSuccessListener {
                Toast.makeText(context, "Story uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload story", Toast.LENGTH_SHORT).show()
            }
    }
}