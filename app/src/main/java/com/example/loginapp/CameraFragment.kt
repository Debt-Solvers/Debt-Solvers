package com.example.loginapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.loginapp.databinding.FragmentCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.FileOutputStream
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface ReceiptUploadService {
    @Multipart
    @POST("/api/v1/receipts/upload")
    suspend fun uploadReceipt(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): ApiResponse
}

data class ApiResponse(
    val status: Int,
    val message: String
)

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var savedImageUri: Uri? = null
    private var capturedImageFile: File? = null

    private lateinit var tokenManager: TokenManager
    private val receiptService by lazy {
        createReceiptService()
    }

    private fun createReceiptService(): ReceiptUploadService {
        tokenManager = TokenManager.getInstance(requireContext())

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8082/") // Replace with your actual base URL
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(tokenManager))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ReceiptUploadService::class.java)
    }

    private inner class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest = chain.request()
            val token = tokenManager.getToken()

            val authenticatedRequest = token?.let {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $it")
                    .build()
            } ?: originalRequest

            return chain.proceed(authenticatedRequest)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the launcher for capturing a picture
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap?
                if (imageBitmap != null) {
                    savedImageUri = saveImageToInternalStorage(imageBitmap)
                    Log.d("Image", "Image taken: $savedImageUri")
                    showImagePopup(savedImageUri)

                    // Prepare for upload after capturing
                    capturedImageFile?.let { uploadReceipt(it) }
                } else {
                    Log.e("Image", "Captured image bitmap is null")
                }
            }
        }

        // Set up the FAB to capture a picture
        binding.fabTakePicture.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Create a temporary file for the image
            capturedImageFile = File(requireContext().cacheDir, "receipt_${System.currentTimeMillis()}.jpg")

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(cameraIntent)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    private fun uploadReceipt(file: File) {
        if (!file.exists()) {
            Log.e("UploadReceipt", "File does not exist")
            return
        }

        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), "Receipt Description")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    receiptService.uploadReceipt(multipartBody, description)
                }

                Log.d("UploadReceipt", "Response status: ${response.status}")
                Log.d("UploadReceipt", "Response message: ${response.message}")

                if (response.status == 200) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Upload failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UploadReceipt", "Error uploading receipt", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Upload error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to take a picture", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePopup(imageUri: Uri?) {
        if (imageUri != null) {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_image_popup)

            val imageView = dialog.findViewById<ImageView>(R.id.popupImageView)
            imageView.setImageURI(imageUri)

            dialog.show()
            imageView.setOnClickListener {
                dialog.dismiss()
            }
        } else {
            Toast.makeText(requireContext(), "Error displaying image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val filename = "captured_image_${System.currentTimeMillis()}.jpg"
        var uri: Uri? = null
        try {
            // Create the file in the app's internal storage
            val file = File(requireContext().filesDir, filename)

            // Use FileOutputStream to write the bitmap
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            uri = Uri.fromFile(file)

            // Also save the file reference for upload
            capturedImageFile = file
        } catch (e: Exception) {
            Log.e("SaveImage", "Error saving image", e)
            e.printStackTrace()
        }
        return uri
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}