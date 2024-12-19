package com.example.if570_lab_uts_willy_00000079845

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var nameEditText: EditText
    private lateinit var nimEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Periksa apakah pengguna sudah login
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Jika pengguna login, dapatkan userId (UID)
            userId = currentUser.uid
        } else {
            // Jika tidak login, arahkan pengguna ke halaman login (opsional)
            Toast.makeText(requireContext(), "Pengguna belum login", Toast.LENGTH_SHORT).show()
            return
        }

        // Inisialisasi komponen UI
        nameEditText = view.findViewById(R.id.edit_text_name)
        nimEditText = view.findViewById(R.id.edit_text_nim)
        saveButton = view.findViewById(R.id.save_button)

        // Set listener untuk tombol simpan
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val nim = nimEditText.text.toString().trim()

            if (name.isEmpty() || nim.isEmpty()) {
                Toast.makeText(requireContext(), "Nama dan NIM tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                saveProfileToFirestore(name, nim)
            }
        }

        // Muat profil pengguna dari Firestore (jika ada), atau atur default jika belum ada
        loadProfileFromFirestore()
    }

    // Fungsi untuk menyimpan profil ke Firestore
    private fun saveProfileToFirestore(name: String, nim: String) {
        val firestore = FirebaseFirestore.getInstance()
        val profileData = hashMapOf(
            "name" to name,
            "nim" to nim
        )

        // Simpan data profil di Firestore dengan user ID sebagai dokumen
        firestore.collection("profiles").document(userId)
            .set(profileData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal memperbarui profil", e)
                Toast.makeText(requireContext(), "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi untuk memuat profil pengguna dari Firestore atau mengatur default values
    private fun loadProfileFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("profiles").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Ambil data dari Firestore dan tampilkan di UI
                    val name = document.getString("name") ?: "Nama"
                    val nim = document.getString("nim") ?: "123456"
                    Log.d("Firestore", "Name: $name, NIM: $nim")

                    nameEditText.setText(name)
                    nimEditText.setText(nim)
                } else {
                    // Jika tidak ada data, tetapkan nilai default
                    Log.d("Firestore", "Tidak ada data profil, menetapkan nilai default.")
                    nameEditText.setText("Nama")
                    nimEditText.setText("123456")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal memuat profil", e)
                // Jika terjadi kegagalan, tetapkan nilai default
                nameEditText.setText("Nama")
                nimEditText.setText("123456")
                Toast.makeText(requireContext(), "Gagal memuat profil, menggunakan nilai default", Toast.LENGTH_SHORT).show()
            }
    }
}