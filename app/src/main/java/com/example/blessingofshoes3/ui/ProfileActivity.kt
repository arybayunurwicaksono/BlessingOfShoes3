package com.example.blessingofshoes3.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes3.MainActivity
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ActivityEditProductBinding
import com.example.blessingofshoes3.databinding.ActivityMainBinding
import com.example.blessingofshoes3.databinding.ActivityProfileBinding
import com.example.blessingofshoes3.starter.WelcomeActivity
import com.example.blessingofshoes3.ui.product.EditProductActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.utils.createCustomTempFile
import com.example.blessingofshoes3.utils.uriToFile
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var _activityProfileBinding: ActivityProfileBinding
    private val binding get() = _activityProfileBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    lateinit var sharedPref: Preferences


    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@ProfileActivity, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityProfileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        sharedPref = Preferences(this)
        binding.imgBackMain.setOnClickListener{
            val i = Intent(this@ProfileActivity, MainActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            finish()
        }
        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
        var edtUsername = findViewById<EditText>(R.id.edt_username)
        var edtEmail = findViewById<EditText>(R.id.edt_email)
        var edtFullname = findViewById<EditText>(R.id.edt_full_name)
        var edtPassword = findViewById<EditText>(R.id.edt_password)
        var imageView = findViewById<ImageView>(R.id.imageView)
        viewModel.readUserDetail(sharedPref.getString(Constant.PREF_EMAIL)).observe(this, Observer {
            edtUsername.setText(it.username.toString())
            edtEmail.setText(it.email.toString())
            edtFullname.setText(it.fullname.toString())
            edtPassword.setText(it.password.toString())
            var eId = it.idUser
            var eRole = it.role!!
            Glide.with(this@ProfileActivity)
                .load(it.photoUser)
                .fitCenter()
                .into(imageView)

            binding.btnEdit.setOnClickListener {
                if (!edtUsername.isEnabled) {
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    edtUsername.isEnabled = true
                    edtEmail.isEnabled = true
                    edtFullname.isEnabled = true
                    edtPassword.isEnabled = true

                    binding.btnEdit.setBackgroundResource(R.drawable.rounded_primary)
                    binding.btnEdit.setTextColor(Color.WHITE)
                    binding.btnSignOut.visibility = GONE
                    binding.btnFinishEdit.visibility = VISIBLE
                    binding.layoutPhoto.visibility = VISIBLE

                    binding.btnFinishEdit.setOnClickListener {
                        var eEmail = edtEmail.text.toString().trim()
                        var eFullname = edtFullname.text.toString().trim()
                        var eUsername = edtUsername.text.toString().trim()
                        var ePassword = edtPassword.text.toString().trim()
                        var ePhoto = imageView.drawToBitmap()
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getString(R.string.confirmation))
                            .setContentText(getString(R.string.content_confirmation))
                            .setConfirmText(getString(R.string.save))
                            .setConfirmClickListener { sDialog ->
                                lifecycleScope.launch {
                                    //viewModel.updateProduct(idProduct, productName, productPrice,productStock, productPhoto)
                                    viewModel.updateUsersData(applicationContext, eId!!, eFullname, eUsername,
                                        eEmail, ePassword, eRole, ePhoto) {
                                        sharedPref.clear()
                                        sharedPref.put(Constant.PREF_EMAIL, eEmail)
                                        sharedPref.put(Constant.PREF_PASSWORD, ePassword)
                                        sharedPref.put(Constant.PREF_IS_LOGIN, true)
                                        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                                        edtUsername.isEnabled = false
                                        edtEmail.isEnabled = false
                                        edtFullname.isEnabled = false
                                        edtPassword.isEnabled = false
                                        binding.btnSignOut.visibility = VISIBLE
                                        binding.btnEdit.setBackgroundResource(R.drawable.round_transparent_button)
                                        binding.btnEdit.setTextColor(getColor(R.color.light_green))
                                        binding.btnFinishEdit.visibility = GONE
                                        binding.layoutPhoto.visibility = GONE
                                        sDialog.dismissWithAnimation()
                                        finishUpdate()
                                    }
                                }

                            }
                            .setCancelText(getString(R.string.cancel))
                            .show()
                    }

                } else {
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    edtUsername.isEnabled = false
                    edtEmail.isEnabled = false
                    edtFullname.isEnabled = false
                    edtPassword.isEnabled = false

                    binding.btnEdit.setBackgroundResource(R.drawable.round_transparent_button)
                    binding.btnEdit.setTextColor(getColor(R.color.light_green))
                    binding.btnFinishEdit.visibility = GONE
                    binding.layoutPhoto.visibility = GONE
                }
            }
        })

        binding.btnSignOut.setOnClickListener{
            sharedPref.clear()
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                ProfileActivity.REQUIRED_PERMISSIONS,
                ProfileActivity.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnFinishEdit.setOnClickListener{
            reduceFileImage(getFile!!)
        }
        binding.btnCamera.setOnClickListener {
            takePicture()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }




    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding.imageView.setImageURI(selectedImg)
        }
    }

    fun takePicture(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.blessingofshoes3",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)

        }
    }
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            val rotatedBitmap: Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }
            var compressQuality = 100
            var streamLength: Int
            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
            } while (streamLength > 1000000)
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
            try {
                os = FileOutputStream(file)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
                os.flush()
                os.close()
                getFile = file
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.imageView.setImageBitmap(rotatedBitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.img_upload_error),
                    Snackbar.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }


    fun finishUpdate(){
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(getString(R.string.profile_update_success))
            .setConfirmText("Ok")
            .show()
    }
}