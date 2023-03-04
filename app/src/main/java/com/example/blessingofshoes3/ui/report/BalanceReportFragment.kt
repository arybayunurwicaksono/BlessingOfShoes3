package com.example.blessingofshoes3.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.blessingofshoes3.db.Balance
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BalanceReportFragment : Fragment() {

    private var binding: FragmentBalanceReportBinding? = null
    private lateinit var reportAdapter: BalanceReportAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var reportList: ArrayList<BalanceReport>
    lateinit var reportListData: List<BalanceReport>
    private lateinit var rvTransaction: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_balance_report, container, false)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllBalanceReport().observe(viewLifecycleOwner) { itemList ->
            if (itemList != null) {
                reportListData = itemList
                reportAdapter.setReportData(itemList)
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