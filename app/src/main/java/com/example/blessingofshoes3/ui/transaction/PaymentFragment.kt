package com.example.blessingofshoes3.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.CartAdapter
import com.example.blessingofshoes3.adapter.PaymentAdapter
import com.example.blessingofshoes3.databinding.ActivityPaymentBinding
import com.example.blessingofshoes3.databinding.FragmentCartBinding
import com.example.blessingofshoes3.databinding.FragmentPaymentBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.ui.TransactionActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private var binding: FragmentPaymentBinding? = null
    private lateinit var paymentAdapter: PaymentAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    lateinit var cartList: ArrayList<Cart>
    lateinit var cartListData: List<Cart>
    lateinit var listCart : ArrayList<Cart>
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var rvCart: RecyclerView
    private var printing : Printing? = null
    lateinit var transactionReceipt : Cart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = Preferences(requireContext())
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var tvTotalPayment = view.findViewById<TextView>(R.id.txtTotalBayar)
        var validateCart = viewModel.checkCart()
        when {
            validateCart == 0 -> {
                tvTotalPayment.text = "Rp.000.00"
            }
            else ->{
                tvTotalPayment.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
            }
        }
        var itemTotalPayment : Int = viewModel.sumTotalPayment()!!
        var itemPayment : Int = 0
        var itemPaymentReturn : Int = 0
        val paymentReceive = view.findViewById<EditText>(R.id.diterima)
        var moneyChange = view.findViewById<TextView>(R.id.txtReturn)
        paymentReceive.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        paymentReceive.error = "Fill Payment"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                when {
                    s.isNullOrBlank() -> {
                        paymentReceive.error = "Fill Payment"
                    }
                    else -> {
                        itemPayment = s.toString().toInt()
                        itemPaymentReturn = itemPayment - itemTotalPayment
                        if(itemPaymentReturn<0){
                            paymentReceive.error = "payment received less"
                        } else {
                            moneyChange.text = itemPaymentReturn.toString()
                        }
                    }
                }

            }
        })

        var btnSimpan = view.findViewById<Button>(R.id.btnSimpanBayar)
        btnSimpan.setOnClickListener {

            val paymentReceive = view.findViewById<EditText>(R.id.diterima).text.toString().trim()
            when {
                validateCart == 0 -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Cart is Empty")
                        .setContentText("Please return to cashier menu!")
                        .show()
                }
                paymentReceive.isEmpty() -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Some data is not correct!")
                        .show()
                }
                paymentReceive.toInt()<0 -> {
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Payment received less then Total Payment")
                        .show()
                }
                else -> {

                    var validateCart = viewModel.checkCart()
                    when {
                        validateCart == 0 -> {
                            tvTotalPayment?.text = "Rp.000.00"
                            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Cart is Empty")
                                .setContentText("Please return to cashier menu!")
                                .show()
                        }
                        else ->{
                            tvTotalPayment?.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
                            var totalProfit = (numberFormat.format(viewModel.sumTotalProfit()!!.toDouble()).toString())
                            var totalCartProfit = viewModel.sumTotalProfit()!!.toInt()
                            var status = "complete"
                            var cartTotal = viewModel.sumTotalPayment()!!.toInt()
                            var moneyReceived = paymentReceive.toString().toInt()
                            var moneyChange = moneyReceived - cartTotal
                            val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                            var onProcess : Boolean
                            var proofPhoto = activity?.getDrawable(R.drawable.loading_image)?.toBitmap()
                            var sumTotalItem = appDatabase.sumTotalTransactionItem()
                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                            val currentDate = sdf.format(Date())
                            SweetAlertDialog(requireContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("Payment")
                                .setContentText("Choose payment method")
                                .setConfirmText("Cash")
                                .setCustomImage(R.drawable.ic_baseline_balance_for_payment)
                                .setConfirmClickListener { sDialog ->
                                    lifecycleScope.launch {
                                        var typePayment = "Cash"
                                        viewModel.updateCashBalance(requireContext(), cartTotal) {
                                            viewModel.updateProfitBalance(requireContext(), totalCartProfit){
                                                onProcess = java.lang.Boolean.TRUE
                                                if(onProcess == java.lang.Boolean.TRUE) {
                                                    viewModel.insertTransaction(
                                                        Transaction(0, cartTotal, totalCartProfit, moneyReceived,
                                                            moneyChange, sumTotalItem, username, typePayment, currentDate,
                                                            proofPhoto!!, "product"
                                                        )
                                                    )
                                                    var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                    viewModel.updateCartIdTransaction(requireContext(), idTransaction) {
                                                        viewModel.updateCartStatus(requireContext(), status){
                                                            viewModel.insertBalanceReport(
                                                                BalanceReport(
                                                                    0,
                                                                    cartTotal,
                                                                    "In",
                                                                    typePayment,
                                                                    "Transaction",
                                                                    username,
                                                                    currentDate
                                                                )
                                                            )
                                                            var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                            val intent = Intent(requireContext(), TransactionActivity::class.java)
                                                            intent.putExtra("DATA_STATUS", "print")
                                                            intent.putExtra("DATA_CUSTOMER", "empty")
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                            startActivity(intent)
                                                            /*setRecyclerView()
                                                            observeNotes()*/

                                                        }
                                                    }

                                                }
                                            }


                                        }
                                    }
                                    sDialog.dismissWithAnimation()
                                }
                                .setCancelText("Digital")
                                .setCancelButtonBackgroundColor(R.color.blue_600)
                                .setCancelClickListener { pDialog ->
                                    var typePayment = "Digital"
                                    viewModel.updateDigitalBalance(requireContext(), cartTotal) {
                                        viewModel.updateProfitBalance(requireContext(), totalCartProfit){
                                            onProcess = java.lang.Boolean.TRUE
                                            if(onProcess == java.lang.Boolean.TRUE) {
                                                viewModel.insertTransaction(
                                                    Transaction(0, cartTotal, totalCartProfit, moneyReceived,
                                                        moneyChange, sumTotalItem, username, typePayment, currentDate, proofPhoto!!, "product")
                                                )
                                                var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                viewModel.updateCartIdTransaction(requireContext(), idTransaction) {
                                                    viewModel.updateCartStatus(requireContext(), status){
                                                        viewModel.insertBalanceReport(
                                                            BalanceReport(
                                                                0,
                                                                cartTotal,
                                                                "In",
                                                                typePayment,
                                                                "Transaction",
                                                                username,
                                                                currentDate
                                                            )
                                                        )
                                                        val intent = Intent(requireContext(), TransactionActivity::class.java)
                                                        intent.putExtra("DATA_STATUS", "print")
                                                        intent.putExtra("DATA_CUSTOMER", "empty")
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                        startActivity(intent)
                                                    }
                                                }
                                            }
                                        }


                                    }
                                    pDialog.dismissWithAnimation()
                                }
                                .show()
                        }
                    }




                }
            }

        }

        observeNotes()
        cartList = ArrayList()
        rvCart()
        initAction()

    }

    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvCart)
    }

    private fun rvCart() {
        rvCart = requireView().findViewById(R.id.rv_product_cart)
        paymentAdapter = PaymentAdapter(context, cartList)
        rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentAdapter
        }

    }
    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllCartItemByStatus().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartListData = itemList
                    paymentAdapter.setProductData(itemList)
                }
            }
        }
    }
    inner class Callback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, ItemTouchHelper.RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val data = cartListData[position]
            val data2 = cartListData[position]
            //Toast.makeText(context, "Berhasil Menghapus : " + data.nameProduct, Toast.LENGTH_LONG).show()
            SweetAlertDialog(requireContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Delete this "+ data.nameItem.toString() + "?")
                .setContentText("You cannot undo this event!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.sumCancelableStockItem(data2.idProduct, data2.totalItem)
                    var tPayment = data2.totalpayment.toString().toInt()
                    var tProfit = data2.totalProfit!!.toString().toInt()
                    var priceFix = tPayment - tProfit
                    viewModel.sumCancelableTotalPurchasesItem(data2.idProduct, priceFix)
                    viewModel.deleteCart(data.idItem)
                    val localeID =  Locale("in", "ID")
                    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                    var tvTotalPayment = view?.findViewById<TextView>(R.id.txtTotalBayar)
                    var validateCart = viewModel.checkCart()
                    when {
                        validateCart == 0 -> {
                            tvTotalPayment?.text = "Rp.000.00"
                        }
                        else ->{
                            tvTotalPayment?.text = (numberFormat.format(viewModel.sumTotalPayment()!!.toDouble()).toString())
                        }
                    }
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Cancel")
                .setCancelClickListener { pDialog ->
                    viewModel.insertCart(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }
    }
}

