package com.example.blessingofshoes3.ui.report

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.lifecycle.Observer
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.DetailReportAdapter
import com.example.blessingofshoes3.databinding.ActivityDetailReportBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.*
import kotlin.math.min

@AndroidEntryPoint
class DetailReportActivity : AppCompatActivity() {

    private lateinit var _activityDetailReportBinding: ActivityDetailReportBinding
    private val binding get() = _activityDetailReportBinding
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var adapter: DetailReportAdapter
    var total: Int = 0
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private var isClickable = true
    var profitItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailReportBinding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = Preferences(this)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        getSupportActionBar()?.hide()
        val eId = intent.getIntExtra("DATA_ID", 0)
        Log.i("extraData", "ID : $eId")
        var extraId = eId.toString()

        viewModel.readTransactionById(eId).observe(this, Observer {
            binding.tvItemTotalProfitTitle.text = getString(R.string.total_profit)
            binding.tvItemTotalPriceTitle.text = getString(R.string.total_price)
            binding.usernameTitle.text = getString(R.string.cashier_username_title)
            binding.transactionIdTitle.text = getString(R.string.transaction_id)

            binding.transactionId.setText("#00"+it.idTransaction!!.toString())
            binding.username.setText(it.username!!.toString())
            var total = it.totalTransaction!!.toString()
            var profit = it.profitTransaction!!.toString()
            var paymentMethod = it.typePayment!!.toString()
            val byteArray = it.proofPhoto
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

// Menentukan ukuran bitmap yang diinginkan
            val desiredWidth = 300
            val desiredHeight = 500

// Menghitung skala faktor yang diperlukan untuk mengubah ukuran bitmap
            val widthScaleFactor = desiredWidth.toFloat() / bitmap.width
            val heightScaleFactor = desiredHeight.toFloat() / bitmap.height
            val scaleFactor = min(widthScaleFactor, heightScaleFactor)

// Menggunakan skala faktor untuk mengubah ukuran bitmap
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scaleFactor).toInt(), (bitmap.height * scaleFactor).toInt(), true)

// Menampilkan bitmap yang diubah ukurannya
            binding.imageProof.setImageBitmap(scaledBitmap)
            if (paymentMethod == "Digital") {
                binding.btnProof.visibility = VISIBLE
            } else {
                binding.btnProof.visibility = GONE
            }
            binding.btnProof.setOnClickListener {
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.cvProof.visibility = VISIBLE
            }
            binding.imageSwitch.setOnClickListener{
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.cvProof.visibility = GONE
            }

            Log.i("extraData", "ID : $total")
            Log.i("extraData", "ID : $profit")
            binding.tvItemTotalPrice.text = (numberFormat.format(it.totalTransaction!!.toDouble()).toString())
            binding.tvItemTotalProfit.text = (numberFormat.format(it.profitTransaction!!.toDouble()).toString())
            binding.transactionDate.text = it.transactionDate.toString()
            if(it.transactionType == "Service") {
                binding.progressBar.visibility = View.VISIBLE
                setClickable(false)
                lifecycleScope.launch {
                    appDatabase.readTransactionItemService(extraId).collect { cartList ->
                        if (cartList.isNotEmpty()) {
                            binding.progressBar.visibility = View.GONE
                            adapter.submitList(cartList)
                            setClickable(true)
                        }
                    }
                }
                setRecyclerView()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                setClickable(false)
                lifecycleScope.launch {
                    appDatabase.readTransactionItem(extraId).collect { cartList ->
                        if (cartList.isNotEmpty()) {
                            binding.progressBar.visibility = View.GONE
                            adapter.submitList(cartList)
                            setClickable(true)
                        }
                    }
                }
                setRecyclerView()
            }
        })



    }

    private fun setRecyclerView() {
        val rvCart = findViewById<RecyclerView>(R.id.rv_product_cart)
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.setHasFixedSize(true)
        adapter = DetailReportAdapter()
        rvCart.adapter = adapter
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