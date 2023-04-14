package com.example.blessingofshoes3.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.BalanceReportAdapter
import com.example.blessingofshoes3.adapter.TransactionReportAdapter
import com.example.blessingofshoes3.databinding.FragmentBalanceReportBinding
import com.example.blessingofshoes3.databinding.FragmentTransactionReportBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Balance
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.ui.ReportActivity
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class BalanceReportFragment : Fragment() {

    private var binding: FragmentBalanceReportBinding? = null
    private lateinit var reportAdapter: BalanceReportAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var reportList: ArrayList<BalanceReport>
    lateinit var reportListData: List<BalanceReport>
    private lateinit var rvTransaction: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_balance_report, container, false)
        var tvInvest = view.findViewById<TextView>(R.id.invest_value)
        var tvTransaction = view.findViewById<TextView>(R.id.transaction_value)
        var tvRestock = view.findViewById<TextView>(R.id.restock_value)
        var tvReturn = view.findViewById<TextView>(R.id.return_value)
        var tvOther = view.findViewById<TextView>(R.id.other_value)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var balanceValue = 0
        var validateCountBalance = appDatabase.validateCountBalance()!!
        var validateCountTransaction = appDatabase.validateTransactionRecord()!!
        var validateCountRestock = appDatabase.validateCountRestock()!!
        var validateCountReturn = appDatabase.validateCountReturn()!!
        var validateCountOther = appDatabase.validateCountBalanceOther()!!
        if (validateCountBalance!=0) {
            tvInvest.text = (numberFormat.format(appDatabase.sumBalanceByTag("Capital")!!.toDouble()).toString())
        } else {
            tvInvest.text = "Rp.0,00"
        }
        if (validateCountTransaction!=0){
            tvTransaction.text = (numberFormat.format(appDatabase.sumBalanceByTag("Transaction")!!.toDouble()).toString())
        } else {
            tvTransaction.text = "Rp.0,00"
        }
        if (validateCountRestock!=0) {
            tvRestock.text = (numberFormat.format(appDatabase.sumBalanceByTag("restock")!!.toDouble()).toString())
        } else {
            tvRestock.text = "Rp.0,00"
        }
        if (validateCountReturn!=0) {
            tvReturn.text = (numberFormat.format(appDatabase.sumBalanceByTag("Return")!!.toDouble()).toString())
        } else {
            tvReturn.text = "Rp.0,00"
        }
        if (validateCountOther!=0){
            tvOther.text = (numberFormat.format(appDatabase.sumBalanceByTag("Other")!!.toDouble()).toString())
        } else {
            tvOther.text = "Rp.0,00"
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        viewModel.getAllBalanceReport().observe(viewLifecycleOwner) { itemList ->
            progressBar.visibility = View.VISIBLE
            if (itemList != null) {
                progressBar.visibility = View.GONE
                reportListData = itemList
                reportAdapter.setReportData(itemList)
                activity.setClickable(true)
            }
        }
        reportList = ArrayList()
        rvTransaction()

    }


    private fun rvTransaction() {
        rvTransaction = requireView().findViewById(R.id.rv_balance_report)
        reportAdapter = BalanceReportAdapter(context, reportList)
        rvTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = reportAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}