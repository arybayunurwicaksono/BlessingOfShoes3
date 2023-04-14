package com.example.blessingofshoes3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.adapter.TransactionReportAdapter
import com.example.blessingofshoes3.databinding.ActivityLoginBinding
import com.example.blessingofshoes3.databinding.ActivityMainBinding
import com.example.blessingofshoes3.db.Accounting
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.ui.*
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var printing : Printing? = null
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }

    private lateinit var adapter: TransactionReportAdapter
    lateinit var transactionList: ArrayList<Transaction>
    lateinit var transactionListData: List<Transaction>
    lateinit var listTransaction : ArrayList<Transaction>
    private lateinit var rvTransaction: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupportActionBar()?.hide()
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        binding.btnProduct.setOnClickListener {
            val intent = Intent(applicationContext, ProductActivity::class.java)

            startActivity(intent)
        }
        binding.btnSetting.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)

            startActivity(intent)
        }
        binding.btnProfile.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)

            startActivity(intent)
        }

        binding.btnServices.setOnClickListener {
            val intentToService = Intent(this, ServicesActivity::class.java)

            startActivity(intentToService)
        }

        binding.btnTransaction.setOnClickListener {
            val intent = Intent(applicationContext, TransactionActivity::class.java)

            startActivity(intent)
        }

        binding.btnReport.setOnClickListener {
            val intent = Intent(applicationContext, ReportActivity::class.java)

            startActivity(intent)
        }

        binding.btnAddBalance.setOnClickListener{
            var typeBalance: String
            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(getString(R.string.balance_method))
                .setConfirmText(getString(R.string.cash))
                .setCancelText(getString(R.string.digital))
                .setCustomImage(R.drawable.ic_baseline_balance_for_payment)
                .setCancelButtonBackgroundColor(R.color.light_green_400)
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    typeBalance = "Cash"
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    binding.edtReadType.setText(typeBalance)
                    binding.updateBalance.visibility = View.VISIBLE
                }
                .setCancelClickListener { pDialog ->
                    pDialog.dismissWithAnimation()
                    typeBalance = "Digital"
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    binding.edtReadType.setText(typeBalance)
                    binding.updateBalance.visibility = View.VISIBLE
                }
                .show()
        }
        binding.withdraw.setOnClickListener{
            var typeBalance: String
            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(getString(R.string.balance_method))
                .setConfirmText(getString(R.string.cash))
                .setCancelText(getString(R.string.digital))
                .setCustomImage(R.drawable.ic_baseline_balance_for_payment)
                .setCancelButtonBackgroundColor(R.color.light_green_400)
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    typeBalance = "Cash"
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    binding.edtReadTypeWithdraw.setText(typeBalance)
                    binding.withdrawBalance.visibility = View.VISIBLE
                }
                .setCancelClickListener { pDialog ->
                    pDialog.dismissWithAnimation()
                    typeBalance = "Digital"
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    binding.edtReadTypeWithdraw.setText(typeBalance)
                    binding.withdrawBalance.visibility = View.VISIBLE
                }
                .show()
        }

        binding.btnAdd.setOnClickListener {
            var total = binding.edtReadBalance.text.toString().toInt()
            var status = "In"
            var typeBalance = binding.edtReadType.text.toString()

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            var note = "Capital"
            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
            appDatabase.insertBalanceReport(
                BalanceReport(
                    0,
                    total,
                    status,
                    typeBalance,
                    note,
                    username,
                    currentDate
                )
            )
            when {
                total.toString().isEmpty() -> {
                    binding.edtReadBalance.error = getString(R.string.fill_balance_form)
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.some_data_is_empty))
                        .show()
                } else -> {

                }

            }
            if (typeBalance == "Cash") {

                SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText(getString(R.string.invest_confirmation))
                    .setCustomImage(R.drawable.ic_baseline_info_24)
                    .setConfirmText(getString(R.string.yes))
                    .setConfirmClickListener { sDialog ->
                        appDatabase.updateCashBalance(total)
                        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                        var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                        binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                        binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                        binding.updateBalance.visibility = View.GONE
                        sDialog.dismissWithAnimation()
                    }
                    .setCancelText(getString(R.string.no))
                    .setCancelClickListener { pDialog ->
                        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                        binding.updateBalance.visibility = View.GONE
                    }
                    .show()

            } else {

                SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText(getString(R.string.invest_confirmation))
                    .setCustomImage(R.drawable.ic_baseline_info_24)
                    .setConfirmText(getString(R.string.yes))
                    .setConfirmClickListener { sDialog ->
                        appDatabase.updateDigitalBalance(total)
                        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                        var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                        binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                        binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                        binding.updateBalance.visibility = View.GONE
                        sDialog.dismissWithAnimation()
                    }
                    .setCancelText(getString(R.string.no))
                    .setCancelClickListener { pDialog ->
                        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                        binding.updateBalance.visibility = View.GONE
                    }
                    .show()



            }
        }
        binding.btnWithdraw.setOnClickListener {
            var total = binding.edtReadWithdrawBalance.text.toString().toInt()
            var status = "Out"
            var typeBalance = binding.edtReadTypeWithdraw.text.toString()

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            var note = "Other"
            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
            appDatabase.insertBalanceReport(
                BalanceReport(
                    0,
                    total,
                    status,
                    typeBalance,
                    note,
                    username,
                    currentDate
                )
            )
            when {
                total.toString().isEmpty() -> {
                    binding.edtReadWithdrawBalance.error = getString(R.string.fill_balance_form)
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(getString(R.string.some_data_is_empty))
                        .show()
                }
                else -> {
                    if (typeBalance == "Cash") {

                        SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setCustomImage(R.drawable.ic_baseline_info_24)
                            .setTitleText(getString(R.string.withdraw_confirmation))
                            .setConfirmText(getString(R.string.yes))
                            .setConfirmClickListener { sDialog ->
                                appDatabase.updateCashBalance(-total)
                                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                                var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                                binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                                binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                                binding.withdrawBalance.visibility = View.GONE
                                sDialog.dismissWithAnimation()
                            }
                            .setCancelText(getString(R.string.no))
                            .setCancelClickListener { pDialog ->
                                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                                binding.withdrawBalance.visibility = View.GONE
                            }
                            .show()

                    } else {

                        SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setCustomImage(R.drawable.ic_baseline_info_24)
                            .setTitleText(getString(R.string.withdraw_confirmation))
                            .setConfirmText(getString(R.string.yes))
                            .setConfirmClickListener { sDialog ->
                                appDatabase.updateDigitalBalance(-total)
                                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                                var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                                binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                                binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                                binding.withdrawBalance.visibility = View.GONE
                                sDialog.dismissWithAnimation()
                            }
                            .setCancelText(getString(R.string.no))
                            .setCancelClickListener { pDialog ->
                                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                                binding.withdrawBalance.visibility = View.GONE
                            }
                            .show()

                    }
                }
            }

        }
        val sdf = SimpleDateFormat("MMMM/yyyy")
        val currentDate = sdf.format(Date())
        val monthPicker = currentDate.toString()
        val validateByMonth = appDatabase.validateAccounting(monthPicker)!!
        var validateCountBalance = appDatabase.validateCountBalance()!!
        var validateCountProduct = appDatabase.validateCountProduct()!!

        sharedPref = Preferences(this)
        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
        //var sumInvest = appDatabase.sumTotalInvest()!!

        if (validateCountBalance == 0) {
            if (validateCountProduct == 0) {
                if (validateByMonth == 0) {
                    viewModel.insertAccounting(
                        Accounting(
                            0,
                            currentDate,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,0,0,0,0,0,0,0,0,0, 0, username,"onProgress", 0,0,0)
                    )
                }
            }
        } else {
            if (validateByMonth == 0) {
                var digitalBalance = appDatabase.readDigitalBalance()!!
                var cashBalance = appDatabase.readCashBalance()!!
                var stockValue = appDatabase.readTotalStock()!!
                var StockWorthValue = appDatabase.readTotalStockWorth()!!
                viewModel.insertAccounting(
                    Accounting(
                        0,
                        currentDate,
                        digitalBalance,
                        cashBalance,
                        stockValue,
                        StockWorthValue,
                        0,
                        0,0,0,0,0,0,0,0,0,0, 0, username,"onProgress", 0,0,0))
            }
        }

        val sdf2 = SimpleDateFormat("dd MMMM yyyy")
        val currentDate2 = sdf2.format(Date())
        val getDate = currentDate2.toString()


        binding.username.text = username
        binding.currentDate.text = getDate

        var validateCountBalance2 = appDatabase.validateCountBalance()!!
        var validateCountProduct2 = appDatabase.validateCountProduct()!!



        if (validateCountBalance2 == 0) {
            binding.balanceTotal.text = "Rp.0,00"
            binding.digitalTotal.text = "Rp.0,00"
            binding.profitTotal.text = "Rp.0,00"
        } else {
            var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
            binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
            binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
            binding.profitTotal.text = (numberFormat.format(appDatabase.readProfitBalance()!!.toDouble()).toString())
        }
        if (validateCountProduct2 == 0) {
            binding.stockTotal.text = "0"
            binding.stockWorthTotal.text = "Rp.0,00"
        } else {
            binding.stockTotal.text = appDatabase.readTotalStock()!!.toString() + " "+ getString(R.string.product)
            binding.stockWorthTotal.text = (numberFormat.format(appDatabase.readTotalStockWorth()!!.toDouble()).toString())
        }


        val sdf3 = SimpleDateFormat("dd/M/yyyy")
        val currentDate3 = sdf3.format(Date())
        var dateSearch = "%"+currentDate3+"%"
        var validateCountBalanceReport = appDatabase.validateBalanceReport(dateSearch)!!

        var eRestockPurchases : Int? = 0
        var eRestockItem : Int? = 0
        var validateRestock = appDatabase.validateRestock(dateSearch)
        if (validateRestock!=0){
            eRestockItem = appDatabase.sumTotalStockAdded(dateSearch)
            binding.stockInValue.text = eRestockItem.toString() + " "+ getString(R.string.product)
        } else {
            binding.stockInValue.text = "0 "+ getString(R.string.product)
        }
        var validateTransaction = appDatabase.validateTransaction(dateSearch)
        var eTransaction = 0
        if (validateTransaction!=0) {
            eTransaction = appDatabase.sumTotalTransactionAcc(dateSearch)!!
            var eTodayProfit = appDatabase.sumTotalTransactionProfit(dateSearch)!!
            var eTotalItem = appDatabase.sumTotalTransactionItemOut(dateSearch)!!
            binding.todayProfitValue.text = (numberFormat.format(eTodayProfit!!.toDouble()).toString())
            binding.todayIncomeTotal.text = (numberFormat.format(eTransaction!!.toDouble()).toString())
            binding.todayBalanceValue.text = (numberFormat.format(eTransaction!!.toDouble()).toString())
            binding.stockOutValue.text = eTotalItem!!.toString() + " "+ getString(R.string.product)
            binding.todayBalance.text = (numberFormat.format(eTodayProfit!!.toDouble()).toString())
            binding.todayBalance.setTextColor(this.getColor(R.color.light_green))
            binding.arrowStats.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)

        } else {
            binding.todayIncomeTotal.text = "Rp.0,00"
            binding.todayProfitValue.text = "Rp.0,00"
            binding.todayBalanceValue.text = "Rp.0,00"
            binding.stockOutValue.text = "0 "+ getString(R.string.product)
            binding.todayBalance.text = "Rp.0,00"
        }
        if (validateCountBalanceReport!=0) {
            if (validateTransaction!=0) {

                var eTodayProfit = appDatabase.sumTotalTransactionProfit(dateSearch)!!
                var eTodayLoss = appDatabase.sumBalanceReportOut(dateSearch)!!
                binding.todayLossValue.text = (numberFormat.format(eTodayLoss!!.toDouble()).toString())
                var eTotalBalance = eTodayProfit!! - eTodayLoss!!
                if (eTotalBalance<0) {
                    binding.todayBalance.text = "-"+(numberFormat.format(eTotalBalance!!.toDouble()).toString())
                    binding.todayBalance.setTextColor(this.getColor(R.color.light_red))
                    binding.arrowStats.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                } else {
                    binding.todayBalance.text = (numberFormat.format(eTotalBalance!!.toDouble()).toString())
                    binding.todayBalance.setTextColor(this.getColor(R.color.light_green))
                    binding.arrowStats.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                }
            }

        } else {
            binding.todayLossValue.text = "Rp.0,00"
        }

    }

}