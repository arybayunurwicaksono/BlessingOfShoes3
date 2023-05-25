package com.example.blessingofshoes3.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.MainActivity
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.ui.product.AddProductFragment
import com.example.blessingofshoes3.ui.product.ProductFragment
import com.example.blessingofshoes3.ui.transaction.*
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class TransactionActivity : AppCompatActivity() {

    private var printing : Printing? = null
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    lateinit var bottomNav : BottomNavigationView
    private var isClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        getSupportActionBar()?.hide()
        loadFragment(CartFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomTransactionNavigationView)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.cartFragment -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.washingServicesFragment -> {
                    loadFragment(WashingServicesFragment())
                    true
                }
                R.id.paymentFragment -> {
                    SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.ic_baseline_info_24)
                        .setTitleText(getString(R.string.transaction_type))
                        .setConfirmText(getString(R.string.product))
                        .setCancelText(getString(R.string.services))
                        .setConfirmClickListener { sDialog->
                            var cartTest = appDatabase.testCart("onProgress").toString()
                            if (cartTest == "onProgress") {
                                sDialog.dismissWithAnimation()
                                loadFragment(PaymentFragment())
                            } else {
                                sDialog.dismissWithAnimation()
                                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getString(R.string.empty_cart))
                                    .setContentText(getString(R.string.return_to_cashier))
                                    .setConfirmText("Ok")
                                    .show()
                            }
                        }
                        .setCancelClickListener { pDialog ->
                            var cartTest = appDatabase.testCart("onWashing").toString()
                            if (cartTest == "onWashing") {
                                pDialog.dismissWithAnimation()
                                loadFragment(ServicesPaymentFragment())
                            } else {
                                pDialog.dismissWithAnimation()
                                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getString(R.string.empty_cart))
                                    .setContentText(getString(R.string.return_to_cashier))
                                    .setConfirmText("Ok")
                                    .show()
                            }
                        }
                        .show()


                    true
                }
                R.id.returnFragment -> {
                    loadFragment(ReturnFragment())
                    true
                }
                else -> {
                    loadFragment(CartFragment())
                    true
                }
            }
        }

        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        else resultLauncher.launch(
            Intent(
                this@TransactionActivity,
                ScanningActivity::class.java
            )
        )
        val eStatus = intent.getStringExtra("DATA_STATUS")
        if (eStatus == "print") {

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.transaction_success))
                .setContentText(getString(R.string.print_receipt))
                .setConfirmText(getString(R.string.yes))
                .setConfirmClickListener { sDialog ->
                    if (!Printooth.hasPairedPrinter())
                         resultLauncher.launch(
                            Intent(
                                this@TransactionActivity,
                                ScanningActivity::class.java
                            ),
                        )
                    else {
                        initListeners()
                        sDialog.dismissWithAnimation()
                    }

                }
                .setCancelText(getString(R.string.no))
                .show()
        } else {
            Toast.makeText(this, getString(R.string.ready_to_run), Toast.LENGTH_LONG)
        }

        if (eStatus == "return") {
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.return_success))
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                }.show()
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_transaction_fragment,fragment)
        transaction.commit()
    }

    private fun initListeners() {
        if (!Printooth.hasPairedPrinter())
            resultLauncher.launch(
                Intent(
                    this@TransactionActivity,
                    ScanningActivity::class.java
                ),
            )
        else printDetails()

        /* callback from printooth to get printer process */
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@TransactionActivity, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@TransactionActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@TransactionActivity, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@TransactionActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@TransactionActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@TransactionActivity, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun printDetails() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }

    /* Customize your printer here with text, logo and QR code */
    private fun getSomePrintables() = ArrayList<Printable>().apply {

        add(RawPrintable.Builder(byteArrayOf(27, 20, 4)).build()) // feed lines example in raw mode


        //logo
//            add(ImagePrintable.Builder(R.drawable.bold, resources)
//                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
//                    .build())
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        add(
            TextPrintable.Builder()
                .setText("Blessing Of Shoes")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_OFF)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("Jl. Raya Piyungan - Prambanan \nNo.KM. 1, Gatak, Bokoharjo, \nKec. Prambanan, Kabupaten Sleman\nDaerah Istimewa Yogyakarta 55572")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("0882006058160")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("-------------------------------")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())

        val eStatus = intent.getStringExtra("DATA_STATUS")
        val eCustomer = intent.getStringExtra("DATA_CUSTOMER")
        val eType = intent.getStringExtra("DATA_TYPE")

        add(
            TextPrintable.Builder()
                .setText("customer name :"+eCustomer.toString())
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build())
        add(
            TextPrintable.Builder()
                .setText("-------------------------------")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build())


        if (eCustomer == "empty") {
            var idTransaction = viewModel.readLastTransaction()!!
            add(
                TextPrintable.Builder()
                    .setText("Transaction ID: #"+idTransaction)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setNewLinesAfter(1)
                    .build())
            add(
                TextPrintable.Builder()
                    .setText("-------------------------------")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setNewLinesAfter(1)
                    .build())
            var totalPayment = (numberFormat.format(appDatabase.readTotalTransactionPayment(idTransaction)!!.toDouble()).toString())
            var totalReceived = (numberFormat.format(appDatabase.readTotalReceivedPayment(idTransaction)!!.toDouble()).toString())
            var totalChange = (numberFormat.format(appDatabase.readTotalChangePayment(idTransaction)!!.toDouble()).toString())
            var totalRecord = viewModel.readTotalTransactionRecord(idTransaction)
            if (idTransaction == idTransaction) {
                for (x in 0..totalRecord!!-1) {

                    var nameItem : String = appDatabase.readNameItemReceipt(idTransaction,x)!!.toString()
                    var priceItem : String = (numberFormat.format(appDatabase.readPriceItemReceipt(idTransaction,x)!!.toDouble()).toString())
                    var totalItem : String = appDatabase.readTotalItemReceipt(idTransaction,x).toString()
                    var totalPayment : String = (numberFormat.format(appDatabase.readTotalPaymentReceipt(idTransaction,x)!!.toDouble()).toString())
                    add(
                        TextPrintable.Builder()
                            .setText(nameItem!!)
                            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                            .setNewLinesAfter(1)
                            .build())
                    add(
                        TextPrintable.Builder()
                            .setText("("+priceItem!! + "x" + totalItem!!+")   "+totalPayment!!)
                            .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                            .setNewLinesAfter(1)
                            .build())

                }
            } else {
                Toast.makeText(this@TransactionActivity, "Something Error", Toast.LENGTH_LONG)
            }
            add(
                TextPrintable.Builder()
                    .setText("-------------------------------")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setNewLinesAfter(1)
                    .build())
            add(
                TextPrintable.Builder()
                    .setText("Subtotal "+totalPayment)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(1)
                    .build())
            add(
                TextPrintable.Builder()
                    .setText("-------------------------------")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setNewLinesAfter(1)
                    .build())
            add(
                TextPrintable.Builder()
                    .setText("Received "+totalReceived)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(1)
                    .build())
            add(
                TextPrintable.Builder()
                    .setText("Change "+totalChange)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                    .setNewLinesAfter(3)
                    .build())
        } else {

            var totalRecord = appDatabase.readTotalTransactionServiceRecord(eCustomer.toString())!!
            var totalPayment = (numberFormat.format(appDatabase.readTotalTransactionServicePayment(eCustomer.toString())!!.toDouble()).toString())

            if (totalRecord == totalRecord) {
                for (x in 0..totalRecord!!-1) {

                    var nameItem : String = appDatabase.readNameItemServiceReceipt(eCustomer.toString(),x)!!.toString()
                    var priceItem : String = (numberFormat.format(appDatabase.readPriceItemServiceReceipt(eCustomer.toString(),x)!!.toDouble()).toString())
                    var totalItem : String = appDatabase.readTotalItemWashingReceipt(eCustomer.toString(),x).toString()
                    var totalPayment : String = (numberFormat.format(appDatabase.readTotalPaymentServiceReceipt(eCustomer.toString(),x)!!.toDouble()).toString())
                    add(
                        TextPrintable.Builder()
                            .setText(nameItem!!)
                            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                            .setNewLinesAfter(1)
                            .build())
                    add(
                        TextPrintable.Builder()
                            .setText("("+priceItem!! + "x" + totalItem!!+")   "+totalPayment!!)
                            .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                            .setNewLinesAfter(1)
                            .build())

                }

                add(
                    TextPrintable.Builder()
                        .setText("-------------------------------")
                        .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                        .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                        .setNewLinesAfter(1)
                        .build())
                add(
                    TextPrintable.Builder()
                        .setText("Subtotal "+totalPayment)
                        .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                        .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                        .setNewLinesAfter(1)
                        .build())

            } else {
                Toast.makeText(this@TransactionActivity, "Something Error", Toast.LENGTH_LONG)
            }

        }


        add(
            TextPrintable.Builder()
                .setText("Thank you very much for your\nTrust to Blessing of Shoes")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

    }

    /* Inbuilt activity to pair device with printer or select from list of pair bluetooth devices */
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER &&  result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
//            val intent = result.data
            printDetails()
        }
    }

    fun setClickable(clickable: Boolean) {
        isClickable = clickable
        if (clickable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }
}