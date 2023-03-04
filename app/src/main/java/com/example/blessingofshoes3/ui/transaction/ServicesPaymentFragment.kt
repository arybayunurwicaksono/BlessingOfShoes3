package com.example.blessingofshoes3.ui.transaction

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.PaymentAdapter
import com.example.blessingofshoes3.databinding.FragmentPaymentBinding
import com.example.blessingofshoes3.databinding.FragmentServicesPaymentBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.ui.TransactionActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ServicesPaymentFragment : Fragment() {

    private var binding: FragmentServicesPaymentBinding? = null
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
        binding = FragmentServicesPaymentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = Preferences(requireContext())
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var rvCart = view.findViewById<RecyclerView>(R.id.rv_service_cart)
        var layoutPayment = view.findViewById<ConstraintLayout>(R.id.layoutPayment)
        var layoutBawah = view.findViewById<ConstraintLayout>(R.id.layoutBawah)
        layoutPayment.visibility = GONE
        layoutBawah.visibility = GONE
        var edtReadCustomerName = view.findViewById<EditText>(R.id.edt_read_customer_name)
        var btnSearch = view.findViewById<ImageButton>(R.id.btn_search)
        var customerExtra = edtReadCustomerName.text.toString()
        var tvTotalPayment = view.findViewById<TextView>(R.id.txtTotalBayar)
        edtReadCustomerName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                when {
                    s.isNullOrBlank() -> {
                        layoutPayment.visibility = GONE
                        edtReadCustomerName.error
                        rvCart.setVisibility(View.GONE)
                        layoutBawah.visibility = GONE

                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when {
                    s.isNullOrBlank() -> {
                        layoutPayment.visibility = GONE
                        edtReadCustomerName.error
                        rvCart.setVisibility(View.GONE)
                        layoutBawah.visibility = GONE

                    }
                    else -> {

                        }

                }
            }

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        layoutPayment.visibility = GONE
                        edtReadCustomerName.error
                        rvCart.setVisibility(View.GONE)
                        layoutBawah.visibility = GONE
                    } else ->{

                    }
                }
            }
        })
        btnSearch.setOnClickListener{
            var readCustomer = edtReadCustomerName.text.toString()
            var customerName = "%"+readCustomer+"%"
            var validateCustomer = appDatabase.checkCartWashing(customerName)
            if (validateCustomer == 0) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Name is incorrect!")
                    .setContentText("Please insert Customer Name correctly!")
                    .setConfirmText("OK")
                    .show()
            } else {
                observeNotes(customerName)
                val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
                pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                pDialog.titleText = "Loading Data"
                pDialog.setCancelable(true)
                pDialog.show()
                val time: Long = 1000
                Handler().postDelayed({
                    pDialog.dismissWithAnimation()
                }, time)
                layoutPayment.visibility = VISIBLE
                rvCart.setVisibility(View.VISIBLE)
                layoutBawah.visibility = VISIBLE
                cartList = ArrayList()
                rvCart()
                initAction()
                var tvTotalPayment = view.findViewById<TextView>(R.id.txtTotalBayar)
                var customerExtra = edtReadCustomerName.text.toString()
                var itemTotalPayment : Int = 0
                var itemProfit : Int = 0
                var itemTotal : Int = 0
                var cartTest = appDatabase.checkCartWashing(customerExtra)
                when {
                    cartTest == 0 -> {
                        tvTotalPayment.text = "Rp.000.00"
                    }
                    else ->{
                        tvTotalPayment.text = (numberFormat.format(appDatabase.sumTotalPaymentWashing(customerExtra)!!.toDouble()).toString())
                        itemTotalPayment = appDatabase.sumTotalPaymentWashing(customerExtra)!!
                        itemProfit = appDatabase.sumTotalProfitWashing(customerExtra)!!
                        itemTotal = appDatabase.sumTotalTransactionItemWashing(customerExtra)!!
                    }
                }

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
                var btnPrint = view.findViewById<Button>(R.id.btnPrint)
                btnPrint.setOnClickListener {
                    val intent = Intent(requireContext(), TransactionActivity::class.java)
                    intent.putExtra("DATA_STATUS", "print")
                    intent.putExtra("DATA_CUSTOMER", readCustomer)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                var btnSimpan = view.findViewById<Button>(R.id.btnSimpanBayar)
                btnSimpan.setOnClickListener {
                    val paymentReceive = view.findViewById<EditText>(R.id.diterima).text.toString().trim()
                    when {
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
                            var iName = "%"+customerExtra+"%"
                            var validateCart = appDatabase.checkCartWashing(iName)!!.toInt()
                            when {
                                validateCart == 0 -> {
                                    tvTotalPayment?.text = "Rp.000.00"
                                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cart is Empty")
                                        .setContentText("Please return to cashier menu!")
                                        .show()
                                }
                                else ->{
                                    var totalCartProfit = itemProfit
                                    var status = "complete"
                                    var cartTotal = itemTotalPayment
                                    var moneyReceived = paymentReceive.toString().toInt()
                                    var moneyChange = moneyReceived - cartTotal
                                    val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                                    var onProcess : Boolean
                                    var proofPhoto = activity?.getDrawable(R.drawable.loading_image)?.toBitmap()
                                    var sumTotalItem = itemTotal
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
                                                                    proofPhoto!!, "service"
                                                                )
                                                            )
                                                            var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                            viewModel.updateCartIdTransaction(requireContext(), idTransaction) {
                                                                viewModel.updateCartStatusWashing(requireContext(), customerExtra){
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
                                                                moneyChange, sumTotalItem, username, typePayment, currentDate, proofPhoto!!, "service" )
                                                        )
                                                        var idTransaction = viewModel.readLastTransaction()!!.toInt()
                                                        viewModel.updateCartIdTransaction(requireContext(), idTransaction) {
                                                            viewModel.updateCartStatusWashing(requireContext(), customerExtra){
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

                    var customerExtra = edtReadCustomerName.text.toString()

                    var cartTest2 = appDatabase.checkCartWashing(customerExtra)


                }
                //rvTransaction.setVisibility(View.VISIBLE)
            }
        }
    }

    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvCart)
    }

    private fun rvCart() {
        rvCart = requireView().findViewById(R.id.rv_service_cart)
        paymentAdapter = PaymentAdapter(context, cartList)
        rvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentAdapter
        }

    }
    private fun observeNotes(extraName: String) {
        lifecycleScope.launch {
            viewModel.getAllCartItemServices(extraName).observe(viewLifecycleOwner) { itemList ->
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
            var customerName = cartList[position].username
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
                    observeNotes(customerName!!)
                    pDialog.dismissWithAnimation()
                }
                .show()
        }
    }
}

