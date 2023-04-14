package com.example.blessingofshoes3.ui.product

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.FragmentAddProductBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.db.Restock
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.ui.RegisterActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.utils.createCustomTempFile
import com.example.blessingofshoes3.utils.uriToFile
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.example.blessingofshoes3.viewModel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private var binding: FragmentAddProductBinding? = null
    private lateinit var appViewModel: AppViewModel
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    lateinit var sharedPref: Preferences
    private var getFile: File? = null
    val Fragment.packageManager get() = activity?.packageManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = Preferences(requireContext())

        var itemPrice : Int = 0
        var itemRealPrice : Int = 0
        var itemProfit : Int
        var itemProfitValue : Int
        var itemStock : Int = 0
        var itemTotalPurchases : Int = 0

        binding?.apply {
            btnCamera.setOnClickListener {
                takePicture()
            }
            btnInsertProduct.setOnClickListener {
                reduceFileImage(getFile!!)
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            edtProductPrice.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductPrice.error = getString(R.string.fill_price)
                        }
                    }

                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductPrice.error = getString(R.string.fill_price)
                        }
                        else -> {
                            itemPrice = s.toString().toInt()
                        }
                    }


                }
            })
            edtProductStock.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = getString(R.string.fill_stock)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = getString(R.string.fill_stock)
                        }
                        else -> {
                            itemStock = s.toString().toInt()
                        }
                    }

                }
            })
            edtProductStock.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = getString(R.string.fill_stock)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    when {
                        s.isNullOrBlank() -> {
                            edtProductStock!!.error = getString(R.string.fill_stock)
                        }
                        else -> {
                            itemStock = s.toString().toInt()
                        }
                    }

                }
            })
            edtTotalPurchases.isEnabled = false
            when {
                edtProductPrice.toString().isNullOrEmpty() -> {

                    edtTotalPurchases.isEnabled =false
                }
                edtProductStock.toString().isNullOrEmpty() -> {

                    edtTotalPurchases.isEnabled =false
                }
                else -> {
                    edtTotalPurchases.isEnabled =true
                }

            }
            edtTotalPurchases.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtTotalPurchases.error = getString(R.string.fill_total_purchases)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    when {
                        s.isNullOrBlank() -> {
                            edtTotalPurchases.error = getString(R.string.fill_total_purchases)
                        }
                        else -> {
                            itemTotalPurchases = s.toString().toInt()
                            if (itemStock!=0 && itemPrice!=0) {
                                itemRealPrice = itemTotalPurchases / itemStock
                                itemProfitValue = itemPrice - itemRealPrice
                                profitValue.text = itemProfitValue.toString()
                                priceValue.text = itemRealPrice.toString()
                            } else {
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(getString(R.string.some_data_is_empty))
                                    .show()
                                edtTotalPurchases.setText("")
                                edtProductStock!!.error = getString(R.string.fill_stock)
                                edtProductPrice.error = getString(R.string.fill_price)
                            }
                        }
                    }

                }
            })
            btnInsertProduct.setOnClickListener {
                val productName = edtProductName.text.toString().trim()
                val productBrand = edtProductBrand.text.toString().trim()
                val productPrice = edtProductPrice.text.toString().trim()
                var productStock = edtProductStock.text.toString().trim()
                var productStockFix: Int = 0
                var productPriceFix: Int = 0
                var productRealPriceFix: Int = 0
                var productTotalPurchasesFix: Int = 0
                val productSize = edtProductSize.text.toString().trim()
                val productRealPrice = priceValue.text.toString().trim()
                val productProfit = profitValue.text.toString().toInt()
                var productTotalPurchases = edtTotalPurchases.text.toString().trim()
                var productSupplier = edtSupplier.text.toString().trim()
                val photoItem = imageView.toString().trim()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                when {
                    productName.isEmpty() -> {
                        edtProductName.error = getString(R.string.fill_product_name)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    productSupplier.isEmpty() -> {
                        edtSupplier.error = getString(R.string.fill_product_supplier)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    productBrand.isEmpty() -> {
                        edtProductBrand.error = getString(R.string.fill_product_brand)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    productPrice.isEmpty() -> {
                        edtProductPrice.error = getString(R.string.fill_price)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    productStock.isEmpty() -> {
                        edtProductStock.error = getString(R.string.fill_stock)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    productSize.isEmpty() -> {
                        edtProductSize.error = getString(R.string.fill_product_size)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    productTotalPurchases.isEmpty() -> {
                        edtTotalPurchases.error = getString(R.string.fill_total_purchases)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    else -> {
                        productStockFix = productStock.toInt()
                        productPriceFix = productPrice.toInt()
                        productRealPriceFix = productRealPrice.toInt()
                        productTotalPurchasesFix = productTotalPurchases.toInt()
                        val username =
                            appDatabase.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getString(R.string.payment_method))
                            .setConfirmText(getString(R.string.cash))
                            .setCancelText(getString(R.string.digital))
                            .setConfirmClickListener { sDialog ->
                                edtTypeBalance.setText("Cash")
                                var typePayment = edtTypeBalance.text.toString()
                                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getString(R.string.data_is_correct))
                                    .setContentText(getString(R.string.insert_data))
                                    .setConfirmText(getString(R.string.save))
                                    .setConfirmClickListener { pDialog ->
                                        lifecycleScope.launch {
                                            val productPhoto = imageView.drawToBitmap()
                                            appDatabase.insertProduct(
                                                Product(
                                                    0,
                                                    productName,
                                                    productBrand,
                                                    productPriceFix,
                                                    productStockFix,
                                                    productSize,
                                                    productRealPriceFix,
                                                    productTotalPurchasesFix,
                                                    productProfit,
                                                    productPhoto,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            var lastProductAdded = appDatabase.readLastProduct()!!
                                            appDatabase.insertRestock(
                                                Restock(
                                                    0,
                                                    lastProductAdded,
                                                    productName,
                                                    productStockFix,
                                                    productTotalPurchasesFix,
                                                    productSupplier,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.insertBalanceReport(
                                                BalanceReport(
                                                    0,
                                                    productTotalPurchasesFix,
                                                    "Out",
                                                    typePayment,
                                                    "restock",
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.updateCashOutBalance(
                                                productTotalPurchasesFix
                                            )

                                        }
//val intent = Intent(this, LoginActivity::class.java)
                                        pDialog.dismissWithAnimation()
                                        sDialog.dismissWithAnimation()
                                        finishTask()
                                    }
                                    .show()
                            }
                            .setCancelClickListener { xDialog ->
                                edtTypeBalance.setText("Digital")
                                var typePayment = edtTypeBalance.text.toString()
                                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getString(R.string.data_is_correct))
                                    .setContentText(getString(R.string.insert_data))
                                    .setConfirmText(getString(R.string.save))
                                    .setConfirmClickListener { pDialog ->
                                        lifecycleScope.launch {

                                            val productPhoto = imageView.drawToBitmap()
                                            appDatabase.insertProduct(
                                                Product(
                                                    0,
                                                    productName,
                                                    productBrand,
                                                    productPriceFix,
                                                    productStockFix,
                                                    productSize,
                                                    productRealPriceFix,
                                                    productTotalPurchasesFix,
                                                    productProfit,
                                                    productPhoto,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            var lastProductAdded = appDatabase.readLastProduct()!!
                                            appDatabase.insertRestock(
                                                Restock(
                                                    0,
                                                    lastProductAdded,
                                                    productName,
                                                    productStockFix,
                                                    productTotalPurchasesFix,
                                                    productSupplier,
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.insertBalanceReport(
                                                BalanceReport(
                                                    0,
                                                    productTotalPurchasesFix,
                                                    "Out",
                                                    typePayment,
                                                    "restock",
                                                    username,
                                                    currentDate
                                                )
                                            )
                                            appDatabase.updateDigitalOutBalance(
                                                productTotalPurchasesFix
                                            )

                                        }
                                        pDialog.dismissWithAnimation()
                                        xDialog.dismissWithAnimation()
                                        finishTask()

                                    }
                                    .show()
                            }
                            .show()


                    }

                }

            }
        }



    }

    private fun finishTask() {
        val i = Intent(requireContext(), ProductActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())

            getFile = myFile

            binding?.apply {
                imageView.setImageURI(selectedImg)
            }
        }
    }

    private suspend fun getBitmap(): Bitmap {
        val result = binding?.imageView
        return (result as BitmapDrawable).bitmap
    }
    fun takePicture(){

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        packageManager?.resolveActivity(intent, 0)

        createCustomTempFile(requireContext()).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
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
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            if (getFile != null) {
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
                binding?.apply {
                    imageView.setImageBitmap(rotatedBitmap)
                }
            } else {
                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(getString(R.string.some_data_is_empty))
                    .setContentText(getString(R.string.empty_image))
                    .setConfirmText("Ok")
                    .show()
            }
        }
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
}