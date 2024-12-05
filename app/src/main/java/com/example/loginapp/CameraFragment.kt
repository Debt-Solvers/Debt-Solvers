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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
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
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

interface ReceiptUploadService {
    @Multipart
    @POST("/api/v1/receipts/upload")
    suspend fun uploadReceipt(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("category_id") categoryId: RequestBody
    ): ApiResponse

    @GET("/api/v1/categories/")
    suspend fun fetchCategories(): CategoryResponse
}
data class CategoryResponse(
    val status: Int,
    val message: String,
    val data: List<CategoryReceipt>
)

data class CategoryReceipt(
    val category_id: String,
    val name: String,
    val description: String
)

data class ApiResponse(
    val status: Int,
    val message: String
)

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectGalleryLauncher: ActivityResultLauncher<Intent>
    private var savedImageUri: Uri? = null
    private var capturedImageFile: File? = null

    private lateinit var tokenManager: TokenManager

    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val viewModel: CameraViewModel by viewModels()



    private val categoriesService by lazy {
        createCategoriesService()
    }

    private val receiptService by lazy {
        createReceiptService()
    }

    private fun fetchCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    categoriesService.fetchCategories() // Use categoriesService instead of receiptService
                }

                if (response.status == 200) {
                    val categories = response.data
                    showCategorySelectionDialog(categories)
                    categories.forEach { category ->
                        Log.d("FetchCategories", "Fetched category: id=${category.category_id}, name=${category.name}")
                    }

                } else {
                    Toast.makeText(requireContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FetchCategories", "Error fetching categories", e)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCategorySelectionDialog(categories: List<CategoryReceipt>) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_category_selection)

        val spinner = dialog.findViewById<Spinner>(R.id.categorySpinner)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        dialog.findViewById<Button>(R.id.btnSelect).setOnClickListener {
            val selectedCategoryIndex = spinner.selectedItemPosition
            val selectedCategory = categories[selectedCategoryIndex]
            //Show selected id
            Log.d("CategorySelection", "Selected category: id=${selectedCategory.category_id}, name=${selectedCategory.name}")

            dialog.dismiss()
            uploadReceipt(selectedCategory.category_id)
        }

        dialog.show()
    }


    private fun createReceiptService(): ReceiptUploadService {
        tokenManager = TokenManager.getInstance(requireContext())

        val retrofit = Retrofit.Builder()
            .baseUrl("http://caa900debtsolverappbe.eastus.cloudapp.azure.com:30002/") // Replace with your actual base URL
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

    private fun createCategoriesService(): ReceiptUploadService {
        tokenManager = TokenManager.getInstance(requireContext())

        val retrofit = Retrofit.Builder()
            .baseUrl("http://caa900debtsolverappbe.eastus.cloudapp.azure.com:30001/") // Categories service base URL
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

        // Restore saved captures when fragment is created
        cameraViewModel.restoreSavedCaptures()

        // Observe recent captures using StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            cameraViewModel.recentCaptures.collect { captures ->
                binding.recentCapturesContainer1.removeAllViews()
                binding.recentCapturesContainer2.removeAllViews()

                // Only add unique captures to prevent duplicates
                val uniqueCaptures = captures.distinct()

                uniqueCaptures.forEachIndexed { index, uri ->
                    val containerToUse = if (index % 2 == 0) {
                        binding.recentCapturesContainer1
                    } else {
                        binding.recentCapturesContainer2
                    }
                    addImageToContainer(uri, containerToUse)
                }
            }
        }

        // Initialize the launcher for selecting an image from gallery
        selectGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    savedImageUri = copyGalleryImageToInternalStorage(selectedImageUri)
                    Log.d("Image", "Image selected from gallery: $savedImageUri")

                    savedImageUri?.let { uri ->
                        cameraViewModel.addCapture(uri)
                    }
                    fetchCategories()
                }
            }
        }

        // Initialize the launcher for capturing a picture
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap?
                if (imageBitmap != null) {
                    savedImageUri = saveImageToInternalStorage(imageBitmap)
                    Log.d("Image", "Image taken: $savedImageUri")
                    //showImagePopup(savedImageUri)

                    // Only update the ViewModel, and let the observer handle UI updates
                    savedImageUri?.let { uri ->
                        cameraViewModel.addCapture(uri)

                    }
                    fetchCategories()
                }
            }
        }

        // Restore saved image URI if available
        viewModel.savedImageUri?.let {
            // Display the saved image in the appropriate container
            val containerToUse = if (binding.recentCapturesContainer1.childCount <= binding.recentCapturesContainer2.childCount) {
                binding.recentCapturesContainer1
            } else {
                binding.recentCapturesContainer2
            }
            addImageToContainer(it, containerToUse)
        }

        // Set up the FAB to capture a picture
        binding.fabTakePicture.setOnClickListener {
            showImageSourceDialog()
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

    private fun uploadReceipt(categoryId: String) {
        if (capturedImageFile == null || !capturedImageFile!!.exists()) {
            Log.e("UploadReceipt", "File does not exist")
            return
        }

        val fileRequestBody = capturedImageFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            "receipt",
            capturedImageFile!!.name,
            fileRequestBody
        )
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), "Receipt Description")
        val categoryRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), categoryId)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    receiptService.uploadReceipt(multipartBody, description, categoryRequestBody)
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
                Log.e("UploadReceipt", "Complete error details:", e)
                Log.e("UploadReceipt", "Error uploading receipt", e)
                Log.d("UploadReceipt", "File: ${capturedImageFile!!.name}")
                Log.d("UploadReceipt", "Description: Receipt Description")
                Log.d("UploadReceipt", "Category ID: $categoryId")
                Log.d("UploadReceipt", "File path: ${capturedImageFile!!.absolutePath}")
                Log.d("UploadReceipt", "File exists: ${capturedImageFile!!.exists()}")
                Log.d("UploadReceipt", "File size: ${capturedImageFile!!.length()} bytes")

                if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("UploadReceipt", "Detailed HTTP error: $errorBody")
                }

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
    // New method to show dialog for choosing image source
    private fun showImageSourceDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_image_source)

        val btnCamera = dialog.findViewById<Button>(R.id.btnCamera)
        val btnGallery = dialog.findViewById<Button>(R.id.btnGallery)

        btnCamera.setOnClickListener {
            dialog.dismiss()
            openCamera()
        }

        btnGallery.setOnClickListener {
            dialog.dismiss()
            openGallery()
        }

        dialog.show()
    }

    // New method to open gallery
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectGalleryLauncher.launch(galleryIntent)
    }

    // New method to copy gallery image to internal storage
    private fun copyGalleryImageToInternalStorage(imageUri: Uri): Uri? {
        return try {
            // Read the bitmap from the gallery
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)

            // Save the bitmap to internal storage
            saveImageToInternalStorage(bitmap)
        } catch (e: Exception) {
            Log.e("CopyGalleryImage", "Error copying gallery image", e)
            null
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

            // Save the file reference in ViewModel
            viewModel.capturedImageFile = file
            viewModel.savedImageUri = uri
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

    private fun addImageToContainer(imageUri: Uri, container: ViewGroup) {
        val imageView = ImageView(requireContext())
        val layoutParams = ViewGroup.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.recent_capture_width), // Define this dimension in dimens.xml
            resources.getDimensionPixelSize(R.dimen.recent_capture_height)
        )
        imageView.layoutParams = layoutParams
        imageView.setImageURI(imageUri)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.adjustViewBounds = true

        // Add padding between images
        val padding = resources.getDimensionPixelSize(R.dimen.recent_capture_padding)
        imageView.setPadding(padding, 0, padding, 0)

        // Optional: Add click listener to show full image
        imageView.setOnClickListener {
            showImagePopup(imageUri)
        }

        container.addView(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}