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
                .setTitleText("Balance Method")
                .setConfirmText("Cash")
                .setCancelText("Digital")
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
                .setTitleText("Balance Method")
                .setConfirmText("Cash")
                .setCancelText("Digital")
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
            if (typeBalance == "Cash") {

                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Are you sure to invest?")
                    .setConfirmText("Yes")
                    .setConfirmClickListener { sDialog ->
                        appDatabase.updateCashBalance(total)
                        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                        var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                        binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                        binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                        binding.updateBalance.visibility = View.GONE
                        sDialog.dismissWithAnimation()
                    }
                    .show()

            } else {

                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Are you sure to invest?")
                    .setConfirmText("Yes")
                    .setConfirmClickListener { sDialog ->
                        appDatabase.updateDigitalBalance(total)
                        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                        var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                        binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                        binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                        binding.updateBalance.visibility = View.GONE
                        sDialog.dismissWithAnimation()
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
                    binding.edtReadWithdrawBalance.error = "Fill Balance Form"
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is empty!")
                        .show()
                }
                else -> {
                    if (typeBalance == "Cash") {

                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Are you sure to withdraw?")
                            .setConfirmText("Yes")
                            .setConfirmClickListener { sDialog ->
                                appDatabase.updateCashBalance(-total)
                                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                                var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                                binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                                binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                                binding.withdrawBalance.visibility = View.GONE
                                sDialog.dismissWithAnimation()
                            }
                            .show()

                    } else {

                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Are you sure to withdraw?")
                            .setConfirmText("Yes")
                            .setConfirmClickListener { sDialog ->
                                appDatabase.updateDigitalBalance(-total)
                                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                                var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
                                binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
                                binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
                                binding.withdrawBalance.visibility = View.GONE
                                sDialog.dismissWithAnimation()
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
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Let's get started")
                    .setConfirmText("Ok")
                    .setConfirmClickListener { sDialog ->
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
                        sDialog.dismissWithAnimation()
                    }
                    .show()
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
            binding.balanceTotal.text = "Rp0,00"
            binding.digitalTotal.text = "Rp0,00"
            binding.profitTotal.text = "Rp0,00"
        } else {
            var totalBalance = (appDatabase.readDigitalBalance()!!) + (appDatabase.readCashBalance()!!)
            binding.balanceTotal.text = (numberFormat.format(totalBalance!!.toDouble()).toString())
            binding.digitalTotal.text = (numberFormat.format(appDatabase.readDigitalBalance()!!.toDouble()).toString())
            binding.profitTotal.text = (numberFormat.format(appDatabase.readProfitBalance()!!.toDouble()).toString())
        }
        if (validateCountProduct2 == 0) {
            binding.stockTotal.text = "0"
            binding.stockWorthTotal.text = "Rp0,00"
        } else {
            binding.stockTotal.text = appDatabase.readTotalStock()!!.toString() + " Product"
            binding.stockWorthTotal.text = (numberFormat.format(appDatabase.readTotalStockWorth()!!.toDouble()).toString())
        }

        viewModel.getAllTransaction().observe(this) { itemList ->
            if (itemList != null) {
                transactionListData = itemList
                adapter.setTransactionData(itemList)
            }
        }
        val sdf3 = SimpleDateFormat("dd/M/yyyy")
        val currentDate3 = sdf3.format(Date())
        var dateSearch = "%"+currentDate3+"%"
        var validateTransaction = appDatabase.validateTransaction(dateSearch)
        var eTransaction = 0
        if (validateTransaction!=0) {
            eTransaction = appDatabase.sumTotalTransactionAcc(dateSearch)!!
            binding.todayIncomeTotal.text = (numberFormat.format(eTransaction!!.toDouble()).toString())
        } else {
            binding.todayIncomeTotal.text = "Rp.0,00"
        }
        transactionList = ArrayList()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        rvTransaction = findViewById<RecyclerView>(R.id.rv_transaction_dashboard)
        rvTransaction.layoutManager = LinearLayoutManager(this)
        rvTransaction.setHasFixedSize(true)
        adapter = TransactionReportAdapter(applicationContext, transactionList)
        rvTransaction.adapter = adapter
    }
}