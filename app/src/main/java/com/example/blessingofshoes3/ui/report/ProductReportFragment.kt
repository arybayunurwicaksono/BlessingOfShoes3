package com.example.blessingofshoes3.ui.report

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.PaymentAdapter
import com.example.blessingofshoes3.adapter.RestockAdapter
import com.example.blessingofshoes3.adapter.ReturnListAdapter
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Restock
import com.example.blessingofshoes3.db.Return
import com.example.blessingofshoes3.ui.ReportActivity
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class ProductReportFragment : Fragment() {

    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private lateinit var paymentAdapter: PaymentAdapter
    lateinit var cartList: ArrayList<Cart>
    lateinit var cartListData: List<Cart>
    private lateinit var rvCart: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }
    private lateinit var restockAdapter: RestockAdapter
    lateinit var restockList: ArrayList<Restock>
    lateinit var restockListData: List<Restock>
    private lateinit var rvRestock: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var returnListAdapter: ReturnListAdapter
    lateinit var returnList: ArrayList<Return>
    lateinit var returnListData: List<Return>
    private lateinit var rvReturn: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_report, container, false)
        var btnProductIn = view.findViewById<Button>(R.id.btn_product_in)
        var btnProductOut = view.findViewById<Button>(R.id.btn_product_out)
        var tvProductIn = view.findViewById<TextView>(R.id.product_in_value)
        var tvProductOut = view.findViewById<TextView>(R.id.product_out_value)
        var tvProductReturned = view.findViewById<TextView>(R.id.return_value)
        var btnProductReturned = view.findViewById<Button>(R.id.btn_product_returned)
        var tvStockTotal = view.findViewById<TextView>(R.id.stock_stored_value)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var validateCountTransaction = appDatabase.validateTransactionRecord()!!
        var validateCountProduct = appDatabase.validateCountProduct()!!
        var validateCountReturn = appDatabase.validateCountReturn()!!
        if (validateCountProduct!=0) {
            tvProductIn.text = appDatabase.sumTotalRestockAdded().toString() + " " + getString(R.string.product)
            tvStockTotal.text = appDatabase.readTotalStock()!!.toString() + " " + getString(R.string.product)
        } else {
            tvProductIn.text = "0 " + getString(R.string.product)
            tvStockTotal.text = "0 " + getString(R.string.product)
        }
        if (validateCountReturn!=0){
            tvProductReturned.text = appDatabase.sumTotalReturnedItem()!!.toString()+" " + getString(R.string.product)
        } else {
            tvProductReturned.text = "0 " + getString(R.string.product)
        }
        if (validateCountTransaction!=0){
            tvProductOut.text = appDatabase.sumSoldTransaction().toString() + " " + getString(R.string.product)
        } else {
            tvProductOut.text = "0 " + getString(R.string.product)
        }
        btnProductIn.setOnClickListener{
            btnProductIn.setBackgroundResource(R.drawable.rounded_primary)
            btnProductIn.setTextColor(Color.WHITE)
            btnProductOut.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductOut.setTextColor(view.context.getColor(R.color.light_green))
            btnProductReturned.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductReturned.setTextColor(view.context.getColor(R.color.light_green))
            restockList = ArrayList()
            rvRestock()
            observeRestock()
        }

        btnProductOut.setOnClickListener{
            btnProductIn.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductIn.setTextColor(view.context.getColor(R.color.light_green))
            btnProductOut.setBackgroundResource(R.drawable.rounded_primary)
            btnProductOut.setTextColor(Color.WHITE)
            btnProductReturned.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductReturned.setTextColor(view.context.getColor(R.color.light_green))
            cartList = ArrayList()
            rvCart()
            observeNotes()
        }
        btnProductReturned.setOnClickListener{
            btnProductIn.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductIn.setTextColor(view.context.getColor(R.color.light_green))
            btnProductReturned.setBackgroundResource(R.drawable.rounded_primary)
            btnProductReturned.setTextColor(Color.WHITE)
            btnProductOut.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductOut.setTextColor(view.context.getColor(R.color.light_green))
            returnList = ArrayList()
            rvReturn()
            observeReturn()
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btnProductOut = view.findViewById<Button>(R.id.btn_product_out)
        var btnProductIn = view.findViewById<Button>(R.id.btn_product_in)
        var btnProductReturned = view.findViewById<Button>(R.id.btn_product_returned)
        btnProductIn.setBackgroundResource(R.drawable.rounded_primary)
        btnProductIn.setTextColor(Color.WHITE)
        btnProductOut.setBackgroundResource(R.drawable.round_transparent_button)
        btnProductOut.setTextColor(view.context.getColor(R.color.light_green))
        btnProductReturned.setBackgroundResource(R.drawable.round_transparent_button)
        btnProductReturned.setTextColor(view.context.getColor(R.color.light_green))
        restockList = ArrayList()
        rvRestock()
        observeRestock()
    }
    private fun rvCart() {
        rvCart = requireView().findViewById(R.id.rv_report)
        paymentAdapter = PaymentAdapter(context, cartList)
        rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentAdapter
        }

    }
    private fun rvReturn() {
        rvReturn = requireView().findViewById(R.id.rv_report)
        returnListAdapter = ReturnListAdapter(context, returnList)
        rvReturn.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = returnListAdapter
        }

    }
    private fun observeNotes() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllCartReport().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    cartListData = itemList
                    paymentAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun rvRestock() {
        rvRestock = requireView().findViewById(R.id.rv_report)
        restockAdapter = RestockAdapter(context, restockList)
        rvRestock.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = restockAdapter
        }

    }
    private fun observeRestock() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllRestockReport().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    restockListData = itemList
                    restockAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeReturn() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllReturnData().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    returnListData = itemList
                    returnListAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
}