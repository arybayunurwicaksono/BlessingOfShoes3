package com.example.blessingofshoes3.ui.report

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.TransactionReportAdapter
import com.example.blessingofshoes3.databinding.FragmentTransactionReportBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.starter.WelcomeActivity
import com.example.blessingofshoes3.ui.ReportActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TransactionReportFragment : Fragment() {

    private var binding: FragmentTransactionReportBinding? = null
    private lateinit var transactionReportAdapter: TransactionReportAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var transactionList: ArrayList<Transaction>
    lateinit var transactionListData: List<Transaction>
    private lateinit var rvTransaction: RecyclerView
    lateinit var sharedPref: Preferences
    private lateinit var progressBar: ProgressBar
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_report, container, false)
        sharedPref = Preferences(requireContext())
        var btnAll = view.findViewById<Button>(R.id.btn_all)
        var btnByMonth = view.findViewById<Button>(R.id.btn_month)
        var btnByDay = view.findViewById<Button>(R.id.btn_day)
        var tvIncome = view.findViewById<TextView>(R.id.income_total_value)
        var tvProfit = view.findViewById<TextView>(R.id.profit_total_value)
        var tvSold = view.findViewById<TextView>(R.id.stock_sold_value)
        var tvRecord = view.findViewById<TextView>(R.id.transaction_record_value)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        btnAll.setOnClickListener{
            btnAll.setBackgroundResource(R.drawable.rounded_primary)
            btnAll.setTextColor(Color.WHITE)
            btnByMonth.setBackgroundResource(R.drawable.round_transparent_button)
            btnByMonth.setTextColor(view.context.getColor(R.color.light_green))
            btnByDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnByDay.setTextColor(view.context.getColor(R.color.light_green))
            observeAll()
            transactionList = ArrayList()
            rvTransaction()
            var validateTransactionRecord = appDatabase.validateTransactionRecord()
            if (validateTransactionRecord!=0) {
                var allIncome = appDatabase.sumTotalTransaction()
                var allProfit = appDatabase.sumProfitTransaction()
                var allSold = appDatabase.sumSoldTransaction()
                tvIncome.text = (numberFormat.format(allIncome!!.toDouble()).toString())
                tvProfit.text = (numberFormat.format(allProfit!!.toDouble()).toString())
                tvSold.text = allSold.toString() + " " + getString(R.string.product)
                tvRecord.text = validateTransactionRecord.toString() + " " + getString(R.string.record)
            } else {
                tvIncome.text = "Rp.0,00"
                tvProfit.text = "Rp.0,00"
                tvSold.text = "0 " + getString(R.string.product)
                tvRecord.text = validateTransactionRecord.toString() + " " + getString(R.string.record)
            }
        }

        btnByMonth.setOnClickListener{
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnByMonth.setBackgroundResource(R.drawable.rounded_primary)
            btnByMonth.setTextColor(Color.WHITE)
            btnByDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnByDay.setTextColor(view.context.getColor(R.color.light_green))
            observeMonth()
            transactionList = ArrayList()
            rvTransaction()
            val sdf = SimpleDateFormat("M/yyyy")
            val currentDate = sdf.format(Date())
            var monthSearch = "%"+currentDate+"%"
            var validateMonthTransactionRecord = appDatabase.validateMonthTransactionRecord(monthSearch)
            if (validateMonthTransactionRecord!=0) {
                var allIncome = appDatabase.sumMonthTotalTransaction(monthSearch)
                var allProfit = appDatabase.sumMonthProfitTransaction(monthSearch)
                var allSold = appDatabase.sumMonthSoldTransaction(monthSearch)
                tvIncome.text = (numberFormat.format(allIncome!!.toDouble()).toString())
                tvProfit.text = (numberFormat.format(allProfit!!.toDouble()).toString())
                tvSold.text = allSold.toString() + " " + getString(R.string.product)
                tvRecord.text = validateMonthTransactionRecord.toString() + " " + getString(R.string.record)
            } else {
                tvIncome.text = "Rp.0,00"
                tvProfit.text = "Rp.0,00"
                tvSold.text = "0 " + getString(R.string.product)
                tvRecord.text = validateMonthTransactionRecord.toString() + " " + getString(R.string.record)
            }
        }
        btnByDay.setOnClickListener{
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnByMonth.setBackgroundResource(R.drawable.round_transparent_button)
            btnByMonth.setTextColor(view.context.getColor(R.color.light_green))
            btnByDay.setBackgroundResource(R.drawable.rounded_primary)
            btnByDay.setTextColor(Color.WHITE)
            observeDay()
            transactionList = ArrayList()
            rvTransaction()
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val currentDate = sdf.format(Date())
            var monthSearch = "%"+currentDate+"%"
            var validateMonthTransactionRecord = appDatabase.validateMonthTransactionRecord(monthSearch)
            if (validateMonthTransactionRecord!=0) {
                var allIncome = appDatabase.sumMonthTotalTransaction(monthSearch)
                var allProfit = appDatabase.sumMonthProfitTransaction(monthSearch)
                var allSold = appDatabase.sumMonthSoldTransaction(monthSearch)
                tvIncome.text = (numberFormat.format(allIncome!!.toDouble()).toString())
                tvProfit.text = (numberFormat.format(allProfit!!.toDouble()).toString())
                tvSold.text = allSold.toString() + " " + getString(R.string.product)
                tvRecord.text = validateMonthTransactionRecord.toString() + " " + getString(R.string.record)
            } else {
                tvIncome.text = "Rp.0,00"
                tvProfit.text = "Rp.0,00"
                tvSold.text = "0 " + getString(R.string.product)
                tvRecord.text = validateMonthTransactionRecord.toString() + " " + getString(R.string.record)
            }
        }
        var validateTransactionRecord = appDatabase.validateTransactionRecord()
        if (validateTransactionRecord!=0) {

            var allIncome = appDatabase.sumTotalTransaction()
            var allProfit = appDatabase.sumProfitTransaction()
            var allSold = appDatabase.sumSoldTransaction()
            tvIncome.text = (numberFormat.format(allIncome!!.toDouble()).toString())
            tvProfit.text = (numberFormat.format(allProfit!!.toDouble()).toString())
            tvSold.text = allSold.toString() + " " + getString(R.string.product)
            tvRecord.text = validateTransactionRecord.toString() + " " + getString(R.string.record)
        } else {
            tvIncome.text = "Rp.0,00"
            tvProfit.text = "Rp.0,00"
            tvSold.text = "0 " + getString(R.string.product)
            tvRecord.text = validateTransactionRecord.toString() + " " + getString(R.string.record)
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAll()
        transactionList = ArrayList()
        rvTransaction()
    }

    private fun observeAll() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllTransaction().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    transactionListData = itemList
                    transactionReportAdapter.setTransactionData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeMonth() {
        val sdf = SimpleDateFormat("M/yyyy")
        val currentDate = sdf.format(Date())
        var monthSearch = "%"+currentDate+"%"
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getTransactionByMonth(monthSearch).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    transactionListData = itemList
                    transactionReportAdapter.setTransactionData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeDay() {
        val sdf2 = SimpleDateFormat("dd/M/yyyy")
        val currentDate2 = sdf2.format(Date())
        var daySearch = "%"+currentDate2+"%"
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getTransactionByDay(daySearch).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    transactionListData = itemList
                    transactionReportAdapter.setTransactionData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun rvTransaction() {
        rvTransaction = requireView().findViewById(R.id.rv_transaction)
        transactionReportAdapter = TransactionReportAdapter(context, transactionList)
        rvTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = transactionReportAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}