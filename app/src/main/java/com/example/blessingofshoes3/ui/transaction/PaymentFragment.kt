package com.example.blessingofshoes3.ui.transaction

import android.content.Intent
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.PaymentAdapter
import com.example.blessingofshoes3.databinding.FragmentPaymentBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.ui.TransactionActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.utils.createCustomTempFile
import com.example.blessingofshoes3.utils.uriToFile
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.mazenrashed.printooth.utilities.Printing
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
class PaymentFragment : Fragment() {

    private var binding: FragmentPaymentBinding? = null
    private lateinit var paymentAdapter: PaymentAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    lateinit var cartList: ArrayList<Cart>
    lateinit var cartListData: List<Cart>
    lateinit var listCart : ArrayList<Cart>
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var rvCart: RecyclerView
    private var printing : Printing? = null
    lateinit var transactionReceipt : Cart
    private var getFile: File? = null
    val Fragment.packageManager get() = activity?.packageManager
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = Preferences(requireContext())
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var tvTotalPayment = view.findViewById<TextView>(R.id.txtTotalBayar)
        var statsText = view.findViewById<TextView>(R.id.stats)
        if (statsText.text == "progress") {
            binding?.apply {
                cvProof.visibility = GONE
            }
        }
        binding?.apply {
            tvProofTitle.text = getString(R.string.digital_payment_proof)
        }
        var validateCart = viewModel.checkCart()
        when {
            validateCart == 0 -> {
                tvTotalPayment.text = "Rp.0.00"
            }
            else ->{
                tvTotalPayment.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
            }
        }
        var itemTotalPayment : Int = viewModel.sumTotalPayment()!!
        var itemPayment : Int = 0
        var itemPaymentReturn : Int = 0
        val paymentReceive = view.findViewById<EditText>(R.id.diterima)
        var moneyChange = view.findViewById<TextView>(R.id.txtReturn)
        paymentReceive.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        paymentReceive.error = getString(R.string.fill_payment)
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
                        paymentReceive.error = getString(R.string.fill_payment)
                    }
                    else -> {
                        itemPayment = s.toString().toInt()
                        itemPaymentReturn = itemPayment - itemTotalPayment
                        if(itemPaymentReturn<0){
                            paymentReceive.error = getString(R.string.less_payment_received)
                        } else {
                            moneyChange.text = "Rp."+itemPaymentReturn.toString()
                        }
                    }
                }

            }
        })

        var btnSimpan = view.findViewById<Button>(R.id.btnSimpanBayar)
        btnSimpan.setOnClickListener {
            reduceFileImage(getFile!!)
        }
        btnSimpan.setOnClickListener {

            val paymentReceive = view.findViewById<EditText>(R.id.diterima).text.toString().trim()
            when {
                validateCart == 0 -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.empty_cart))
                        .setContentText(getString(R.string.return_to_cashier))
                        .show()
                }
                paymentReceive.isEmpty() -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.some_data_is_empty))
                        .show()
                }
                paymentReceive.toInt()<0 -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.less_payment_received))
                        .show()
                }
                else -> {
                    var validateReceivedMoney = paymentReceive.toString().toInt()
                    var validateTotalPayment = viewModel.sumTotalPayment()!!
                    when {
                        validateCart == 0 -> {
                            tvTotalPayment?.text = "Rp.0.00"
                            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.empty_cart))
                                .setContentText(getString(R.string.return_to_cashier))
                                .show()
                        }
                        validateTotalPayment>validateReceivedMoney -> {
                            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(getString(R.string.less_payment_received))
                                .show()
                        }
                        else ->{
                            tvTotalPayment?.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
                            var totalProfit = (numberFormat.format(viewModel.sumTotalProfit()!!.toDouble()).toString())
                            var totalCartProfit = viewModel.sumTotalProfit()!!.toInt()
                            var status = "complete"
                            var cartTotal = viewModel.sumTotalPayment()!!.toInt()
                            var moneyReceived = paymentReceive.toString().toInt()
                            var moneyChange = moneyReceived - cartTotal
                            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                            var onProcess : Boolean
                            var proofPhoto = activity?.getDrawable(R.drawable.loading_image)?.toBitmap()
                            var sumTotalItem = appDatabase.sumTotalTransactionItem()
                            val pBitmap = proofPhoto



                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                            val currentDate = sdf.format(Date())
                            SweetAlertDialog(requireContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText(getString(R.string.payment_method))
                                .setContentText(getString(R.string.choose_payment_method))
                                .setConfirmText(getString(R.string.cash))
                                .setCustomImage(R.drawable.ic_baseline_balance_for_payment)
                                .setConfirmClickListener { sDialog ->
                                    lifecycleScope.launch {
                                        var typePayment = "Cash"
                                        val resizedBitmap = Bitmap.createScaledBitmap(pBitmap!!, 200, 200, true)

                                        val byteArrayOutputStream = ByteArrayOutputStream()
                                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                                        val bytes = byteArrayOutputStream.toByteArray()
                                        viewModel.updateCashBalance(requireContext(), cartTotal) {
                                            viewModel.updateProfitBalance(requireContext(), totalCartProfit){
                                                onProcess = java.lang.Boolean.TRUE
                                                if(onProcess == java.lang.Boolean.TRUE) {
                                                    viewModel.insertTransaction(
                                                        Transaction(0, cartTotal, totalCartProfit, moneyReceived,
                                                            moneyChange, sumTotalItem, username, typePayment, currentDate,
                                                            bytes!!, "Product"
                                                        )
                                                    )
                                                    var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                    viewModel.updateCartIdTransaction(requireContext(), idTransaction) {
                                                        viewModel.updateCartStatus(requireContext(), status){
                                                            viewModel.insertBalanceReport(
                                                                BalanceReport(
                                                                    0,
                                                                    cartTotal,
                                                                    "In",
                                                                    typePayment,
                                                                    "Transaction",
                                                                    username,
                                                                    currentDate
                                                                )
                                                            )
                                                            var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                            val intent = Intent(requireContext(), TransactionActivity::class.java)
                                                            intent.putExtra("DATA_STATUS", "print")
                                                            intent.putExtra("DATA_CUSTOMER", "empty")
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                            startActivity(intent)
                                                            /*setRecyclerView()
                                                            observeNotes()*/

                                                        }
                                                    }

                                                }
                                            }


                                        }
                                    }
                                    sDialog.dismissWithAnimation()
                                }
                                .setCancelText(getString(R.string.digital))
                                .setCancelButtonBackgroundColor(R.color.blue_600)
                                .setCancelClickListener { pDialog ->

                                    pDialog.dismissWithAnimation()
                                    SweetAlertDialog(requireContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                        .setTitleText(getString(R.string.payment))
                                        .setContentText(getString(R.string.insert_proof_photo))
                                        .setConfirmText(getString(R.string.camera))
                                        .setCancelText(getString(R.string.galery))
                                        .setCustomImage(R.drawable.ic_baseline_balance_for_payment)
                                        .setConfirmClickListener { qDialog ->
                                            takePicture()
                                            qDialog.dismissWithAnimation()
                                            statsText.text = "complete"
                                        }
                                        .setCancelClickListener { wDialog ->
                                            startGallery()
                                            wDialog.dismissWithAnimation()
                                            statsText.text = "complete"
                                        }
                                        .show()
                                    if (statsText.text == "complete") {
                                        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.proof_inserted))
                                            .setConfirmText("Ok")
                                            .setCancelText(getString(R.string.cancel))
                                            .setConfirmClickListener { qDialog ->
                                                var typePayment = "Digital"
                                                binding?.apply {
                                                    val pPhoto = imageView.drawToBitmap()

                                                    val byteArrayOutputStream = ByteArrayOutputStream()
                                                    pPhoto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                                                    val bytes = byteArrayOutputStream.toByteArray()
                                                    viewModel.updateDigitalBalance(requireContext(), cartTotal) {
                                                        viewModel.updateProfitBalance(requireContext(), totalCartProfit){
                                                            onProcess = java.lang.Boolean.TRUE
                                                            if(onProcess == java.lang.Boolean.TRUE) {
                                                                viewModel.insertTransaction(
                                                                    Transaction(0, cartTotal, totalCartProfit, moneyReceived,
                                                                        moneyChange, sumTotalItem, username, typePayment, currentDate, bytes!!, "Product")
                                                                )
                                                                var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                                viewModel.updateCartIdTransaction(requireContext(), idTransaction) {
                                                                    viewModel.updateCartStatus(requireContext(), status){
                                                                        viewModel.insertBalanceReport(
                                                                            BalanceReport(
                                                                                0,
                                                                                cartTotal,
                                                                                "In",
                                                                                typePayment,
                                                                                "Transaction",
                                                                                username,
                                                                                currentDate
                                                                            )
                                                                        )
                                                                        val intent = Intent(requireContext(), TransactionActivity::class.java)
                                                                        intent.putExtra("DATA_STATUS", "print")
                                                                        intent.putExtra("DATA_CUSTOMER", "empty")
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                                        startActivity(intent)
                                                                    }
                                                                }
                                                            }
                                                        }


                                                    }
                                                }

                                                qDialog.dismissWithAnimation()
                                            }
                                            .setCancelClickListener { wDialog ->

                                                wDialog.dismissWithAnimation()
                                                statsText.text = "progress"
                                            }
                                            .show()
                                    }


                                }
                                .show()
                        }
                    }




                }
            }

        }

        observeNotes()
        cartList = ArrayList()
        rvCart()
        initAction()

        var btnInformation = view.findViewById<ImageView>(R.id.btn_help)

        btnInformation.setOnClickListener {
            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setCustomImage(R.drawable.ic_baseline_info_24)
                .setTitleText(getString(R.string.information_title))
                .setContentText(getString(R.string.delete_tutorial))
                .setConfirmText(getString(R.string.got_it))
                .show()
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
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())

            getFile = myFile

            binding?.apply {
                imageView.setImageURI(selectedImg)
                TransitionManager.beginDelayedTransition(binding?.root, AutoTransition())
                cvProof.visibility = VISIBLE
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
                    TransitionManager.beginDelayedTransition(binding?.root, AutoTransition())
                    cvProof.visibility = VISIBLE
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

    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvCart)
    }

    private fun rvCart() {
        rvCart = requireView().findViewById(R.id.rv_product_cart)
        paymentAdapter = PaymentAdapter(context, cartList)
        rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentAdapter
        }

    }
    private fun observeNotes() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllCartItemByStatus().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    cartListData = itemList
                    paymentAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    inner class Callback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val data = cartListData[position]
            val data2 = cartListData[position]
            //Toast.makeText(context, "Berhasil Menghapus : " + data.nameProduct, Toast.LENGTH_LONG).show()
            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.delete)+" "+ data.nameItem.toString() + "?")
                .setContentText(getString(R.string.event_confirmation))
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.sumCancelableStockItem(data2.idProduct, data2.totalItem)
                    var tPayment = data2.totalpayment.toString().toInt()
                    var tProfit = data2.totalProfit!!.toString().toInt()
                    var priceFix = tPayment - tProfit
                    viewModel.sumCancelableTotalPurchasesItem(data2.idProduct, priceFix)
                    viewModel.deleteCart(data.idItem)
                    val localeID =  Locale("in", "ID")
                    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                    var tvTotalPayment = view?.findViewById<TextView>(R.id.txtTotalBayar)
                    var validateCart = viewModel.checkCart()
                    when {
                        validateCart == 0 -> {
                            tvTotalPayment?.text = "Rp.0.00"
                        }
                        else ->{
                            tvTotalPayment?.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
                        }
                    }
                    sDialog.dismissWithAnimation()
                }
                .setCancelText(getString(R.string.cancel))
                .setCancelClickListener { pDialog ->
                    viewModel.insertCart(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }
    }
}

