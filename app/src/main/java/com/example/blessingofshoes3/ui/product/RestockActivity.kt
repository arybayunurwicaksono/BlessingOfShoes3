package com.example.blessingofshoes3.ui.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ActivityRestockBinding
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Restock
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RestockActivity : AppCompatActivity() {
    private lateinit var _activityRestockBinding: ActivityRestockBinding
    private val binding get() = _activityRestockBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private var idProduct : Int? = 0
    lateinit var sharedPref: Preferences

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@RestockActivity, ProductActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityRestockBinding = ActivityRestockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        sharedPref = Preferences(this)
        val eName = intent.getStringExtra("DATA_NAME")
        val eId = intent.getIntExtra("DATA_ID", 0)
        var itemPrice : Int = 0
        var itemRealPrice : Int = 0
        var totalStockPrice : Int = 0
        var itemProfit : Int
        var newStock : Int = 0
        var newProfit : Int = 0
        var newVar : Int = 0
        var itemProfitValue : String
        var oldVar : Int = 0
        var oldStock : Int = 0
        var overallStock : Int = 0
        var newRealPricePerItem : Int = 0
        var ePrice : Int = 0
        var eProfit : Int = 0
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var productBrand : String = ""
        var productPrice : String = ""
        var productSize : String = ""
        var productStock : Int = 0
        var productName : String

        binding.btnHelp.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(getString(R.string.information_title))
                .setCustomImage(R.drawable.ic_baseline_info_24)
                .setContentText(getString(R.string.restock_price_explainer))
                .setConfirmText("Ok")
                .show()
        }

        Log.i("extraId", "ID : $eId")
        viewModel.readProductItem(eId).observe(this, Observer {
            productName = it.nameProduct!!
            binding.collapsingToolbar.title = getString(R.string.restock)+" "+ productName
            binding.profitValue.setText(it.profitProduct.toString())
            binding.priceValue.setText(it.realPriceProduct.toString())
            idProduct = it.idProduct
            oldStock = it.stockProduct!!.toString().toInt()
            oldVar = it.totalPurchases!!
            eProfit = it.profitProduct!!
            ePrice = it.priceProduct!!.toInt()
            productBrand = it.brandProduct!!.toString()
            productPrice = it.priceProduct!!.toString()
            productSize = it.sizeProduct!!.toString()
            productStock = it.stockProduct!!
            Glide.with(this@RestockActivity)
                .load(it.productPhoto)
                .fitCenter()
                .into(binding.imageView)
            binding.edtProductStock.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtProductStock.error = getString(R.string.fill_new_stock)
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
                            binding.edtProductStock.error = getString(R.string.fill_new_stock)
                        }
                        else -> {
                            newStock = s.toString().toInt()
                        }
                    }

                }
            })
            binding.edtTotalStockPurchases.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtTotalStockPurchases.error = getString(R.string.fill_total_purchases)
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
                            binding.edtTotalStockPurchases.error = getString(R.string.fill_total_purchases)
                        }
                        else -> {

                            totalStockPrice = s.toString().toInt()
                            newVar = totalStockPrice
                            overallStock = oldStock + newStock


                            newRealPricePerItem = (oldVar + newVar)/overallStock
                            newProfit = ePrice - newRealPricePerItem
                            binding.priceValue.text = newRealPricePerItem.toString()
                            binding.profitValue.text = newProfit.toString()

                        }
                    }

                }
            })
            binding.btnInsertProduct.setOnClickListener {
                var pStock = binding.edtProductStock.text.toString().trim()


                var productRealPrice = binding.priceValue.text.toString().trim()
                val totalStockPurchases = binding.edtTotalStockPurchases.text.toString().trim()
                val supplier = binding.edtSupplier.text.toString().trim()
                val productProfit = binding.profitValue.text.toString().toInt()
                when {
                    pStock.isEmpty() -> {
                        binding.edtProductStock.error = getString(R.string.fill_new_stock)
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    totalStockPurchases.isEmpty() -> {
                        binding.edtTotalStockPurchases.error = getString(R.string.fill_total_purchases)
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    supplier.isEmpty() -> {
                        binding.edtSupplier.error = "Fill Supplier"
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    else -> {

                        var productStockFix : Int = productStock + pStock.toInt()
                        var eIdProduct = eId.toString()
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val currentDate = sdf.format(Date())
                        var productPriceFix = productPrice.toInt()
                        var productRealPriceFix = productRealPrice.toInt()
                        var totalPurchasesFix = oldVar + totalStockPurchases.toInt()
                        var pStockFix = pStock.toInt()
                        var pTotalPurchases = totalStockPurchases.toInt()
                        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getString(R.string.payment_method))
                            .setConfirmText(getString(R.string.cash))
                            .setCancelText(getString(R.string.digital))
                            .setConfirmClickListener { sDialog ->
                                binding.edtType.setText("Cash")
                                var typePayment = binding.edtType.text.toString()

                                sDialog.dismissWithAnimation()
                                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getString(R.string.data_is_correct))
                                    .setContentText(getString(R.string.insert_data))
                                    .setConfirmText(getString(R.string.save))
                                    .setConfirmClickListener { pDialog ->
                                        lifecycleScope.launch {
                                            val productPhoto = binding.imageView.drawToBitmap()
                                            var reportPurchases = binding.edtTotalStockPurchases.text.toString().toInt()
                                            viewModel.updateProductItem(applicationContext, eId, productName, productBrand,
                                                productPriceFix, productStockFix, productSize, productRealPriceFix, totalPurchasesFix, productProfit, productPhoto, username, currentDate) {
                                                viewModel.insertRestock(Restock(0, eId, productName, pStockFix, pTotalPurchases, supplier, username, currentDate))
                                                viewModel.insertBalanceReport(BalanceReport(0, reportPurchases, "Out", typePayment, "restock", username, currentDate))
                                                viewModel.updateCashOutBalance(this@RestockActivity,reportPurchases) {
                                                    finishUpdate()
                                                }

                                            }
                                        }

                                    }
                                    .show()
                            }
                            .setCancelClickListener { xDialog ->
                                binding.edtType.setText("Digital")
                                var typePayment = binding.edtType.text.toString()

                                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getString(R.string.data_is_correct))
                                    .setContentText(getString(R.string.insert_data))
                                    .setConfirmText(getString(R.string.save))
                                    .setConfirmClickListener { pDialog ->
                                        pDialog.dismissWithAnimation()
                                        lifecycleScope.launch {
                                            var reportPurchases = binding.edtTotalStockPurchases.text.toString().toInt()
                                            val productPhoto = binding.imageView.drawToBitmap()
                                            viewModel.updateProductItem(applicationContext, eId, productName, productBrand,
                                                productPriceFix, productStockFix, productSize, productRealPriceFix, totalPurchasesFix, productProfit, productPhoto, username, currentDate) {
                                                viewModel.insertRestock(Restock(0, eId, productName, pStockFix, pTotalPurchases, supplier, currentDate))
                                                viewModel.insertBalanceReport(BalanceReport(0, reportPurchases, "Out", typePayment, "restock", username, currentDate))
                                                viewModel.updateDigitalOutBalance(this@RestockActivity,reportPurchases) {
                                                    finishUpdate()
                                                }

                                            }
                                        }

                                    }.show()
                            }
                            .show()

                    }
                }
            }

        })

    }


    fun finishUpdate(){
        Toast.makeText(this, getString(R.string.restock_complete), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ProductActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

}