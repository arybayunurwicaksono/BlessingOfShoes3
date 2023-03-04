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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.PaymentAdapter
import com.example.blessingofshoes3.adapter.RestockAdapter
import com.example.blessingofshoes3.databinding.ActivityPaymentBinding
import com.example.blessingofshoes3.databinding.FragmentPaymentBinding
import com.example.blessingofshoes3.databinding.FragmentProductReportBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Restock
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class ProductReportFragment : Fragment() {

    private var binding: FragmentProductReportBinding? = null
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var paymentAdapter: PaymentAdapter
    lateinit var cartList: ArrayList<Cart>
    lateinit var cartListData: List<Cart>
    lateinit var listCart : ArrayList<Cart>
    private lateinit var rvCart: RecyclerView

    private lateinit var restockAdapter: RestockAdapter
    lateinit var restockList: ArrayList<Restock>
    lateinit var restockListData: List<Restock>
    lateinit var listRestock : ArrayList<Restock>
    private lateinit var rvRestock: RecyclerView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_report, container, false)
        var btnProductIn = view.findViewById<Button>(R.id.btn_product_in)
        var btnProductOut = view.findViewById<Button>(R.id.btn_product_out)
        btnProductIn.setOnClickListener{
            btnProductIn.setBackgroundResource(R.drawable.rounded_primary)
            btnProductIn.setTextColor(Color.WHITE)
            btnProductOut.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductOut.setTextColor(view.context.getColor(R.color.light_green))
            restockList = ArrayList()
            rvRestock()
            observeRestock()
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


        btnProductOut.setOnClickListener{
            btnProductIn.setBackgroundResource(R.drawable.round_transparent_button)
            btnProductIn.setTextColor(view.context.getColor(R.color.light_green))
            btnProductOut.setBackgroundResource(R.drawable.rounded_primary)
            btnProductOut.setTextColor(Color.WHITE)
            cartList = ArrayList()
            rvCart()
            observeNotes()
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
        var btnProductOut = view.findViewById<Button>(R.id.btn_product_in)
        var btnProductIn = view.findViewById<Button>(R.id.btn_product_in)
        btnProductIn.setBackgroundResource(R.drawable.rounded_primary)
        btnProductIn.setTextColor(Color.WHITE)
        btnProductOut.setBackgroundResource(R.drawable.round_transparent_button)
        btnProductOut.setTextColor(view.context.getColor(R.color.light_green))
        restockList = ArrayList()
        rvRestock()
        observeRestock()
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
    private fun rvCart() {
        rvCart = requireView().findViewById(R.id.rv_report)
        paymentAdapter = PaymentAdapter(context, cartList)
        rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentAdapter
        }

    }
    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllCartReport().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartListData = itemList
                    paymentAdapter.setProductData(itemList)
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
        lifecycleScope.launch {
            viewModel.getAllRestockReport().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    restockListData = itemList
                    restockAdapter.setProductData(itemList)
                }
            }
        }
    }
}