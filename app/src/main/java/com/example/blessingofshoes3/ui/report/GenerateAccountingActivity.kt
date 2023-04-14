package com.example.blessingofshoes3.ui.report

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.MainActivity
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ActivityGenerateAccountingBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.ui.ReportActivity
import com.example.blessingofshoes3.ui.TransactionActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class GenerateAccountingActivity : AppCompatActivity() {

    private lateinit var _activityGenerateAccountingBinding: ActivityGenerateAccountingBinding
    private val binding get() = _activityGenerateAccountingBinding
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private var isClickable = true

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@GenerateAccountingActivity, ReportActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityGenerateAccountingBinding = ActivityGenerateAccountingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()
        sharedPref = Preferences(this)
        val sdf = SimpleDateFormat("MMMM/yyyy")
        val currentDate = sdf.format(Date())
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        viewModel.readDetailMonthlyAccounting(currentDate).observe(this, Observer {
            binding.tvIdFinancialAccounting.text = getString(R.string.accounting_id_title) + it.idAccounting.toString()
            binding.AccountBalanceValue.text = numberFormat.format(it.initDigital!!.toDouble()).toString()
            binding.CashBalanceValue.text = numberFormat.format(it.initCash!!.toDouble()).toString()
            binding.TotalStockValue.text = it.initStock!!.toString() + " " + getString(R.string.product)
            binding.StockWorthValue.text = numberFormat.format(it.initStockWorth!!.toDouble()).toString()
            binding.username.text = it.username!!
            binding.status.text = it.status!!
            binding.capitalInvestmentValue.text = numberFormat.format(it.capitalInvest!!.toDouble()).toString()
            binding.transactionTotal.text = numberFormat.format(it.incomeTransaction!!.toDouble()).toString()
            binding.transactionItemTotal.text = it.transactionItem!!.toString() + " " + getString(R.string.product)
            binding.restockTotal.text = numberFormat.format(it.restockPurchases!!.toDouble()).toString()
            binding.restockItemTotal.text = it.restockItem!!.toString() + " " + getString(R.string.product)
            binding.returnTotal.text = numberFormat.format(it.returnTotal!!.toDouble()).toString()
            binding.returnItemTotal.text = it.returnItem!!.toString() + " " + getString(R.string.product)
            binding.balanceInValue.text = numberFormat.format(it.balanceIn!!.toDouble()).toString()
            binding.balanceOutValue.text = numberFormat.format(it.balanceOut!!.toDouble()).toString()
            binding.profitValue.text = numberFormat.format(it.profitEarned!!.toDouble()).toString()

            binding.AccountBalanceValueFinal.text = numberFormat.format(it.finalDigital!!.toDouble()).toString()
            binding.CashBalanceValueFinal.text = numberFormat.format(it.finalCash!!.toDouble()).toString()
            binding.TotalStockValueFinal.text = it.finalStock.toString()  + " " + getString(R.string.product)
            binding.StockWorthValueFinal.text = numberFormat.format(it.finalWorth!!.toDouble()).toString()
            val sdf = SimpleDateFormat("M/yyyy")
            val currentDate = sdf.format(Date())
            var dateSearch = "%"+currentDate+"%"
            var eId = it.idAccounting
            var eDate = it.dateAccounting
            var eInitDigital = it.initDigital
            var eInitCash = it.initCash
            var eInitStock = it.initStock
            var eInitWorth = it.initStockWorth
            var validateCapital = appDatabase.checkCapital()
            var eCapital = 0
            if (validateCapital!=0){
                eCapital = appDatabase.sumTotalInvest(dateSearch)!!
            }

            var eTransaction : Int? = 0
            var eItemTransaction : Int? = 0
            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
            var validateTransaction = appDatabase.validateTransaction(dateSearch)
            var profitFix = 0
            if (validateTransaction!=0) {
                profitFix = appDatabase.sumTotalProfitAcc(dateSearch)!!
                eTransaction = appDatabase.sumTotalTransactionAcc(dateSearch)!!
                eItemTransaction = appDatabase.readTotalTransactionItem(dateSearch)!!
            }
            var eRestockPurchases : Int? = 0
            var eRestockItem : Int? = 0
            var validateRestock = appDatabase.validateRestock(dateSearch)
            if (validateRestock!=0){
                eRestockPurchases = appDatabase.sumTotalPurchases(dateSearch)
                eRestockItem = appDatabase.sumTotalStockAdded(dateSearch)
            }
            var eReturn : Int? = 0
            var eReturnItem : Int? = 0
            var validateReturn = appDatabase.validateReturn(dateSearch)
            if (validateReturn!=0){
                eReturn = appDatabase.sumTotalRefund(dateSearch)
                eReturnItem = appDatabase.sumTotalRefundItem(dateSearch)
            }
            var eStatus = "complete"
            var eFinalDigital = appDatabase.readDigitalBalance()!!
            var eFinalCash = appDatabase.readCashBalance()!!
            var eStock = appDatabase.readTotalStock()
            var eWorth = appDatabase.readTotalStockWorth()
            var eOtherNeeds = 0
            var balanceIn = eFinalCash + eFinalDigital
            var balanceOut = 0
            var validateBalance = appDatabase.validateCountBalance()
            if (validateBalance != 0) {
                balanceOut = appDatabase.sumTotalBalanceOut(dateSearch)!!
            }
            binding.btnGenerate.setOnClickListener{
                SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText(getString(R.string.update_monthly_accounting_title))
                    .setCustomImage(R.drawable.ic_baseline_info_24)
                    .setConfirmText(getString(R.string.yes))
                    .setCancelText(getString(R.string.no))
                    .setConfirmClickListener { sDialog->
                        val fsdf = SimpleDateFormat("MMMM/yyyy")
                        val fFurrentDate = fsdf.format(Date())
                        sDialog.dismissWithAnimation()
                        binding.progressBar.visibility = View.VISIBLE
                        setClickable(false)
                        lifecycleScope.launch {
                            viewModel.updateMonthlyAccounting(
                                applicationContext, eId!!, fFurrentDate, eInitDigital, eInitCash, eInitStock, eInitWorth,
                                eCapital, eTransaction, eItemTransaction, eRestockPurchases, eRestockItem,
                                eReturn, eReturnItem, eFinalDigital, eFinalCash, eStock, eWorth, 0,
                                username, eStatus, balanceIn!!, balanceOut, profitFix) {

                                binding.tvIdFinancialAccounting.text = eId.toString()
                                binding.dateFinancialAccounting.text = fFurrentDate
                                binding.AccountBalanceValue.text = numberFormat.format(eInitDigital!!.toDouble()).toString()
                                binding.CashBalanceValue.text = numberFormat.format(eInitCash!!.toDouble()).toString()
                                binding.TotalStockValue.text = eInitStock.toString()  + " " + getString(R.string.product)
                                binding.StockWorthValue.text = numberFormat.format(eInitWorth!!.toDouble()).toString()
                                binding.capitalInvestmentValue.text = numberFormat.format(eCapital!!.toDouble()).toString()
                                binding.transactionTotal.text = numberFormat.format(eTransaction!!.toDouble()).toString()
                                binding.transactionItemTotal.text = eItemTransaction!!.toString() + " " + getString(R.string.product)
                                binding.restockTotal.text = numberFormat.format(eRestockPurchases!!.toDouble()).toString()
                                binding.restockItemTotal.text = eRestockItem!!.toString() + " " + getString(R.string.product)
                                binding.returnTotal.text = numberFormat.format(eReturn!!.toDouble()).toString()
                                binding.returnItemTotal.text = eReturnItem!!.toString() + " " + getString(R.string.product)
                                binding.balanceInValue.text = numberFormat.format(balanceIn!!.toDouble()).toString()
                                binding.balanceOutValue.text = numberFormat.format(balanceOut!!.toDouble()).toString()
                                binding.profitValue.text = numberFormat.format(profitFix!!.toDouble()).toString()

                                binding.AccountBalanceValueFinal.text = numberFormat.format(eFinalDigital!!.toDouble()).toString()
                                binding.CashBalanceValueFinal.text = numberFormat.format(eFinalCash!!.toDouble()).toString()
                                binding.TotalStockValueFinal.text = eStock.toString()  + " " + getString(R.string.product)
                                binding.StockWorthValueFinal.text = numberFormat.format(eWorth!!.toDouble()).toString()

                                binding.progressBar.visibility = View.GONE
                                setClickable(true)
                            }

                        }


                    }
                    .show()
            }

        })
        binding.btnWhatsapp.setOnClickListener {
            openWhatsApp(binding.btnGenerate)
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

    fun openWhatsApp(view: View) {
        val pm = packageManager
        try {
            // Data untuk dimasukkan ke dalam pesan
            val message = "Halo, berikut laporan pembukuan per-bulan ${binding.dateFinancialAccounting.text}\n\n" +
                    "${binding.tvIdFinancialAccounting.text}\n" +
                    "Nama Pengguna: ${binding.username.text}\n" +
                    "Saldo Digital: ${binding.AccountBalanceValue.text}\n" +
                    "Saldo Tunai: ${binding.CashBalanceValue.text}\n" +
                    "Total Stok: ${binding.TotalStockValue.text}\n" +
                    "Nilai Stok: ${binding.StockWorthValue.text}\n" +
                    "Investasi Modal: ${binding.capitalInvestmentValue.text}\n" +
                    "Total Transaksi: ${binding.transactionTotal.text}\n" +
                    "Jumlah Item Transaksi: ${binding.transactionItemTotal.text}\n" +
                    "Total Restok: ${binding.restockTotal.text}\n" +
                    "Jumlah Item Restok: ${binding.restockItemTotal.text}\n" +
                    "Total Retur: ${binding.returnTotal.text}\n" +
                    "Jumlah Item Retur: ${binding.returnItemTotal.text}\n" +
                    "Saldo Masuk: ${binding.balanceInValue.text}\n" +
                    "Saldo Keluar: ${binding.balanceOutValue.text}\n" +
                    "Total Keuntungan: ${binding.profitValue.text}\n" +
                    "Saldo Akhir Digital: ${binding.AccountBalanceValueFinal.text}\n" +
                    "Saldo Akhir Tunai: ${binding.CashBalanceValueFinal.text}\n" +
                    "Total Stok Akhir: ${binding.TotalStockValueFinal.text}\n" +
                    "Nilai Stok Akhir: ${binding.StockWorthValueFinal.text}"

            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "text/plain"
            val phone = "628123456789"
            waIntent.putExtra(Intent.EXTRA_TEXT, message)
            waIntent.putExtra("jid", phone + "@s.whatsapp.net")
            waIntent.setPackage("com.whatsapp")
            startActivity(waIntent)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
        }
    }

}