package com.example.if570_lab_uts_willy_00000079845

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddStoryFragment : Fragment(R.layout.fragment_add_story) {

    private lateinit var selectImageButton: Button
    private lateinit var uploadButton: Button
    private lateinit var captionEditText: EditText
    private lateinit var selectedImageView: ImageView
    private var selectedImageUri: Uri? = null

    // Activity result launcher untuk memilih gambar dari galeri
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedImageView.setImageURI(uri)
            selectedImageView.visibility = View.VISIBLE  // Menampilkan gambar yang dipilih
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi komponen UI
        selectImageButton = view.findViewById(R.id.select_image_button)
        uploadButton = view.findViewById(R.id.upload_button)
        captionEditText = view.findViewById(R.id.story_caption)
        selectedImageView = view.findViewById(R.id.selected_image_view)

        // Set listener untuk tombol pilih gambar
        selectImageButton.setOnClickListener {
            // Membuka galeri untuk memilih gambar
            pickImageLauncher.launch("image/*")
        }

        // Set listener untuk tombol upload
        uploadButton.setOnClickListener {
            if (selectedImageUri != null) {
                uploadImageAndSaveStory(selectedImageUri!!)
            } else {
                Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk mengunggah gambar ke Firebase Storage dan menyimpan cerita ke Firestore
    private fun uploadImageAndSaveStory(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(imageUri)

        // Mengunggah gambar ke Firebase Storage
        uploadTask.addOnSuccessListener {
            // Setelah gambar diunggah, ambil URL unduhan
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                saveStoryToFirestore(imageUrl)  // Simpan URL gambar dan data cerita ke Firestore
            }.addOnFailureListener { e ->
                Log.e("Firebase", "Gagal mendapatkan URL gambar", e)
                Toast.makeText(requireContext(), "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Gagal mengunggah gambar", e)
            Toast.makeText(requireContext(), "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk menyimpan cerita ke Firestore
    private fun saveStoryToFirestore(imageUrl: String) {
        val caption = captionEditText.text.toString().trim()

        if (caption.isEmpty()) {
            Toast.makeText(requireContext(), "Caption tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat data cerita
        val storyData = hashMapOf(
            "caption" to caption,
            "imageUrl" to imageUrl,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        // Simpan ke Firestore
        FirebaseFirestore.getInstance().collection("stories")
            .add(storyData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cerita berhasil diunggah", Toast.LENGTH_SHORT).show()
                // Bersihkan tampilan
                captionEditText.text.clear()
                selectedImageView.setImageResource(0)
                selectedImageView.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal menyimpan cerita", e)
                Toast.makeText(requireContext(), "Gagal menyimpan cerita", Toast.LENGTH_SHORT).show()
            }
    }
}