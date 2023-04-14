package com.example.blessingofshoes3.ui.report

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.TransactionReportAdapter
import com.example.blessingofshoes3.databinding.ActivityUserListBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.ui.ReportActivity
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class UserListActivity : AppCompatActivity() {
    private lateinit var _activityUserListBinding: ActivityUserListBinding
    private val binding get() = _activityUserListBinding
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var adapter: TransactionReportAdapter
    lateinit var transactionList: ArrayList<Transaction>
    var total: Int = 0
    private lateinit var rvTransaction: RecyclerView
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    lateinit var transactionListData: List<Transaction>
    var profitItem: Int = 0
    private var isClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityUserListBinding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()
        sharedPref = Preferences(this)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        val eId = intent.getIntExtra("DATA_ID", 0)
        val eName = intent.getStringExtra("DATA_NAME")
        Log.i("extraData", "ID : $eId")
        Log.i("extraData", "Name : $eName")
        var extraId = eId.toString()

        var validateCountTransaction = appDatabase.validateCountTransactionByUser(eName)
        if (validateCountTransaction!=0) {
            viewModel.readUserDetail(eId).observe(this, Observer {
                binding.idTitle.text = "ID"
                binding.usernameTitle.text = getString(R.string.username)
                binding.totalTransactionTitle.text = getString(R.string.total_transaction)

                binding.transactionId.setText("#00"+it.idUser!!.toString())
                binding.username.setText(it.username!!.toString())
                var totalBalanceByUser = appDatabase.sumTotalTransactionByUser(it.username)
                var totalItemByUser = appDatabase.sumTotalTransactionItemByUser(it.username)
                var totalTransactionComplete = appDatabase.sumTotalTransactionCompleteByUser(it.username)
                binding.transactionValue.setText(totalTransactionComplete.toString())
                binding.totalBalanceValue.setText(numberFormat.format(totalBalanceByUser!!.toDouble()).toString())
                binding.totalItemValue.setText(totalItemByUser.toString()+" " + getString(R.string.product))

            })
            var iName = binding.username.text.toString()
            binding.progressBar.visibility = View.VISIBLE
            setClickable(false)
            viewModel.getAllTransactionByUser(eName).observe(this) { itemList ->
                if (itemList != null) {
                    binding.progressBar.visibility = View.GONE
                    setClickable(true)
                    transactionListData = itemList
                    adapter.setTransactionData(itemList)
                }
            }
            transactionList = ArrayList()
            setRecyclerView()
        } else {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(getString(R.string.some_data_is_empty))
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    val i = Intent(this@UserListActivity, ReportActivity::class.java)
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    finish()

                }
                .show()
        }

    }

    private fun setRecyclerView() {
        rvTransaction = findViewById<RecyclerView>(R.id.rv_transaction_by_user)
        rvTransaction.layoutManager = LinearLayoutManager(this)
        rvTransaction.setHasFixedSize(true)
        adapter = TransactionReportAdapter(applicationContext, transactionList)
        rvTransaction.adapter = adapter
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