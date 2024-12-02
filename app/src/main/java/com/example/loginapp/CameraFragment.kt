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
import com.example.loginapp.databinding.FragmentCameraBinding
import java.io.File

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var savedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                savedImageUri = saveImageToInternalStorage(imageBitmap)
                Log.d("Image", "Image taken: $savedImageUri")
                showImagePopup(savedImageUri)
            }
        }

        // Set up the FAB to capture a picture
        binding.fabTakePicture.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(cameraIntent)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
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
            requireContext().openFileOutput(filename, AppCompatActivity.MODE_PRIVATE).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            uri = Uri.fromFile(File(requireContext().filesDir, filename))
        } catch (e: Exception) {
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
