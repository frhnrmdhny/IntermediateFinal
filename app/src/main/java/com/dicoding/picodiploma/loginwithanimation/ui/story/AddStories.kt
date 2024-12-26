package com.dicoding.picodiploma.loginwithanimation.ui.story

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoriesBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.ui.home.HomeActivity
import com.dicoding.picodiploma.loginwithanimation.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStories : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoriesBinding
    private lateinit var viewModel: StoriesDetailViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Log.d("AddStories", "Image selected URI: $it")
            showLoading(true)
            Glide.with(this)
                .load(it)
                .into(binding.imageView)
            showLoading(false)
            selectedImageUri = it
        } ?: Log.e("AddStories", "Failed to pick image")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory(this)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        homeViewModel.uploadSuccess.observe(this) { response ->
            if (response != null && !response.error) {
                Toast.makeText(this, "Success added story", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddStories, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        homeViewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }

        homeViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.btnGallery.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnUpload.setOnClickListener {
            saveStory()
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsNeeded.isNotEmpty()) {
            Log.d("AddStories", "Requesting permissions: $permissionsNeeded")
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("AddStories", "Permissions granted")
            } else {
                Toast.makeText(
                    this,
                    "Permission is required to use camera and gallery",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveStory() {
        val descriptionText = binding.descriptionEditText.text.toString()
        if (descriptionText.isBlank()) {
            showError("Description cannot be empty")
            return
        }

        if (selectedImageUri == null) {
            showError("Image must be selected")
            return
        }

        val file = getFileFromUri(selectedImageUri)
        if (file == null) {
            showError("Failed to process selected image")
            return
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val description = descriptionText.toRequestBody("text/plain".toMediaTypeOrNull())

        lifecycleScope.launch {
            homeViewModel.uploadStories(body, description)
        }
    }

    private fun getImageUri(context: Context): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun getFileFromUri(uri: Uri?): File? {
        if (uri == null) return null

        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()
            tempFile
        } catch (e: Exception) {
            Log.e("AddStories", "Error converting URI to file: ${e.message}")
            null
        }
    }

    private fun startCamera() {
        selectedImageUri = getImageUri(this)
        if (selectedImageUri == null) {
            showError("Failed to create URI for camera")
            return
        }
        launcherIntentCamera.launch(selectedImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            selectedImageUri = null
        }
    }

    private fun showImage() {
        selectedImageUri?.let {
            Log.d("AddStories", "Showing image: $it")
            binding.imageView.setImageURI(it)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
