package com.example.blessingofshoes3.ui.report

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.starter.WelcomeActivity
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_report, container, false)


        var btnAll = view.findViewById<Button>(R.id.btn_all)
        var btnByMonth = view.findViewById<Button>(R.id.btn_month)
        var btnByDay = view.findViewById<Button>(R.id.btn_day)
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
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAll()
        transactionList = ArrayList()
        rvTransaction()
        val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading Data"
        pDialog.setCancelable(true)
        pDialog.show()
        val time: Long = 2500
        Handler().postDelayed({
            pDialog.dismissWithAnimation()
        }, time)
    }
    private fun observeAll() {
        lifecycleScope.launch {
            viewModel.getAllTransaction().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    transactionListData = itemList
                    transactionReportAdapter.setTransactionData(itemList)
                }
            }
        }
    }

    private fun observeMonth() {
        val sdf = SimpleDateFormat("M/yyyy")
        val currentDate = sdf.format(Date())
        var monthSearch = "%"+currentDate+"%"
        lifecycleScope.launch {
            viewModel.getTransactionByMonth(monthSearch).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    transactionListData = itemList
                    transactionReportAdapter.setTransactionData(itemList)
                }
            }
        }
    }

    private fun observeDay() {
        val sdf2 = SimpleDateFormat("dd/M/yyyy")
        val currentDate2 = sdf2.format(Date())
        var daySearch = "%"+currentDate2+"%"
        lifecycleScope.launch {
            viewModel.getTransactionByDay(daySearch).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    transactionListData = itemList
                    transactionReportAdapter.setTransactionData(itemList)
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
        transactionReportAdapter.setOnItemClickCallback(object : TransactionReportAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Transaction) {
                showSelectedItem(data)
                /*val intentToDetail = Intent(context, EditProductActivity::class.java)
                intentToDetail.putExtra("DATA", data)
                intentToDetail.putExtra("DATA_ID", data.idProduct)
                intentToDetail.putExtra("DATA_NAME", data.nameProduct)
//                intentToDetail.putExtra("DATA_PRICE", data.priceProduct)
//                intentToDetail.putExtra("DATA_STOCK", data.stockProduct)
                //intentToDetail.putExtra("DATA_PHOTO", data.productPhoto)
                startActivity(intentToDetail)*/
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun showSelectedItem(item: Transaction) {
        //Toast.makeText(context, "Kamu memilih " + item.nameProduct, Toast.LENGTH_SHORT).show()
    }
}