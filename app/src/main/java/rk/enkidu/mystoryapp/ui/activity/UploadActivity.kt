package rk.enkidu.mystoryapp.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rk.enkidu.mystoryapp.R
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.databinding.ActivityUploadBinding
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.helper.uriToFile
import rk.enkidu.mystoryapp.logic.model.SettingsViewModel
import rk.enkidu.mystoryapp.logic.model.UploadViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UploadActivity : AppCompatActivity() {

    private var _binding : ActivityUploadBinding? = null
    private val binding get() = _binding

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showLoading(false)
        supportActionBar?.title = "Upload"

        //viewModel
        settingsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingsViewModel::class.java]
        uploadViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[UploadViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //permission
        permission()

        binding?.cameraButton?.setOnClickListener {
            startPhoto()
        }

        binding?.galleryButton?.setOnClickListener {
            startGallery()
        }

        binding?.uploadButton?.setOnClickListener {
            upload()
        }
    }

    private fun reduceFileSize(file: File): File{
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLenght: Int

        do{
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLenght = bmpPicByteArray.size
            compressQuality -= 5
        } while(streamLenght > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }

    private fun upload() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),  100)
            return
        }
        when{
            getFile == null -> {
                Toast.makeText(this@UploadActivity, getString(R.string.choose), Toast.LENGTH_SHORT).show()
            }
            binding?.etDesc?.text?.length!! > 300 -> {
                binding?.etDesc?.error = getString(R.string.max_300_kata)
            }
            binding?.etDesc?.text?.length!! <= 0 -> {
                binding?.etDesc?.error = getString(R.string.deskripsiempty)

            }
            else -> {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    if(it != null) {
                        try {
                            val file = reduceFileSize(getFile as File)
                            val description = binding?.etDesc?.text.toString()
                                .toRequestBody("text/plain".toMediaType())
                            val requestImageFile =
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            val imageMultipart: MultipartBody.Part =
                                MultipartBody.Part.createFormData(
                                    "photo",
                                    file.name,
                                    requestImageFile
                                )

                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.alert))
                                setMessage(getString(R.string.confirmation_upload))
                                setNegativeButton(getString(R.string.no)) { _, _ -> }
                                setPositiveButton(getString(R.string.yes)) { _, _ ->

                                    settingsViewModel.getUser().observe(this@UploadActivity){ user ->
                                        if(user.token.isNotEmpty()){
                                            uploadViewModel.uploadToServer(
                                                user.token,
                                                description,
                                                imageMultipart,
                                                it.latitude.toFloat(),
                                                it.longitude.toFloat()
                                            ).observe(this@UploadActivity){ result ->
                                                if (result != null) {
                                                    when (result) {
                                                        is Result.Loading -> {
                                                            showLoading(true)
                                                        }
                                                        is Result.Success -> {
                                                            showLoading(false)
                                                            Toast.makeText(this@UploadActivity, getString(R.string.upload_sukses), Toast.LENGTH_SHORT).show()
                                                            CoroutineScope(Dispatchers.Main).launch {
                                                                delay(5000)
                                                                val intent =
                                                                    Intent(this@UploadActivity, HomeActivity::class.java)
                                                                intent.flags =
                                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                                startActivity(
                                                                    intent,
                                                                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@UploadActivity as Activity)
                                                                        .toBundle()
                                                                )
                                                            }
                                                        }
                                                        is Result.Error -> {
                                                            showLoading(false)
                                                            Toast.makeText(this@UploadActivity, getString(R.string.upload_gagal), Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }
                                create()
                                show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this, "Lokasi belum dinyalakan", Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == RESULT_OK){
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadActivity)
            getFile = myFile
            binding?.previewImageView?.setImageURI(selectedImg)
        }
    }
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(intent)
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == RESULT_OK){
            val myFile = File(currentPhotoPath)
            val imageBitmap = BitmapFactory.decodeFile(myFile.path)
            getFile = myFile
            binding?.previewImageView?.setImageBitmap(imageBitmap)
        }
    }
    private fun startPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        rk.enkidu.mystoryapp.logic.helper.createTempFile(application).also {
            val photo: Uri = FileProvider.getUriForFile(this@UploadActivity, "rk.enkidu.mystoryapp", it)
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photo)
            launcherIntentCamera.launch(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // permission
    private fun permission(){
        if(!allPermisionGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION)
        }
    }

    private fun allPermisionGranted() = REQUIRED_PERMISSION.all{
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSION){
            if(!allPermisionGranted()){
                Toast.makeText(
                    this,
                    getString(R.string.izin),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    }

    private fun showLoading(isLoading: Boolean){ binding?.pbUpload?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    companion object{
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}