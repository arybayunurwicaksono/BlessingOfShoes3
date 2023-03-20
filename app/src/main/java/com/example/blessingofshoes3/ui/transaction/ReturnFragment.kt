package com.example.blessingofshoes3.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.MainActivity
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.ReturnListClickListener
import com.example.blessingofshoes3.adapter.ReturnAdapter
import com.example.blessingofshoes3.databinding.FragmentReturnBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Return
import com.example.blessingofshoes3.ui.TransactionActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ReturnFragment : Fragment(), ReturnListClickListener {

    private var binding: FragmentReturnBinding? = null
    private lateinit var returnAdapter: ReturnAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var returnList: ArrayList<Cart>
    lateinit var returnListData: List<Cart>
    lateinit var sharedPref: Preferences
    private lateinit var returnListItem: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var rvReturn: RecyclerView
    lateinit var listener: ReturnListClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReturnBinding.inflate(inflater, container, false)
        listener = this

        sharedPref = Preferences(requireContext())

        returnList = ArrayList()
        val sdf = SimpleDateFormat("MMMM/yyyy")
        val currentDate = sdf.format(Date())


        var rvTransaction = binding!!.rvTransaction
        var edtReadIdTransaction = binding!!.edtReadId
        var btnSearch = binding!!.btnSearch
        btnSearch.setOnClickListener{
            var readIdTransaction = edtReadIdTransaction.text.toString().toInt()
            var validateId = appDatabase.validateReturnIdTransaction(readIdTransaction)
            if (validateId == 0) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("ID is incorrect!")
                    .setContentText("PLEASE INSERT AN ID CORRECTLY")
                    .setConfirmText("OK")
                    .show()
            } else {
                observeNotes(readIdTransaction.toString())
                rvTransaction.setVisibility(View.VISIBLE)
            }
        }
        edtReadIdTransaction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                when {
                    s.isNullOrBlank() -> {
                        edtReadIdTransaction.error
                        rvTransaction.setVisibility(View.GONE)

                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when {
                    s.isNullOrBlank() -> {
                        edtReadIdTransaction.error
                        rvTransaction.setVisibility(View.GONE)

                    }
                    else -> {
                        var readId = s.toString()
                        var extraId = readId
                        var validateId = appDatabase.validateReturnIdTransaction(readId.toInt())
                        if (validateId == 0) {
                            rvTransaction.setVisibility(View.GONE)
                        } else {
                            observeNotes(extraId)
                            rvTransaction.setVisibility(View.VISIBLE)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        edtReadIdTransaction.error
                        rvTransaction.setVisibility(View.GONE)
                    } else ->{
                    var readId = s.toString()
                    var extraId = readId
                    var validateId = appDatabase.validateReturnIdTransaction(readId.toInt())
                    if (validateId == 0) {
                        rvTransaction.setVisibility(View.GONE)
                    } else {
                        observeNotes(extraId)
                        rvTransaction.setVisibility(View.VISIBLE)
                    }
                }
                }
            }
        })
        rvAccounting()

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun observeNotes(extraId : String) {
        lifecycleScope.launch {
            appDatabase.readTransactionItem(extraId).collect { cartList ->
                if (cartList.isNotEmpty()) {
                    rvAccounting()
                    returnAdapter.setCartData(cartList)

                }
            }
        }
    }

    private fun rvAccounting() {
        rvReturn = binding!!.rvTransaction
        returnAdapter = ReturnAdapter(context, returnList, listener)
        rvReturn.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = returnAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onReturnListClickListener(view: View, idItem: Int, nameItem: String, priceItem: Int,
                                           totalItem: Int, totalRefund: Int, returnNote: String, profitItem: Int, idTransaction: Int?) {
/*        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Month  " + idItem!!.toString())
            .setConfirmText("OK")
            .show()*/
        val bundle = bundleOf("DATA_ID" to idItem.toString().trim())
        val fragment = ReturnFragment()


        //findNavController(R.id.bottom_nav).navigate(R.id.action)
        sharedPref = Preferences(requireContext())
        val username = appDatabase.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        var profitFix = profitItem * totalItem
        appDatabase.insertReturn(
            Return(
                0,
                idItem,
                nameItem,
                totalItem,
                totalRefund,
                returnNote,
                username,
                currentDate
            )
        )
        appDatabase.updateCartTotalPaymentOnReturn(totalRefund,idItem)
        appDatabase.updateCartTotalProfitOnReturn(profitFix,idItem)
        appDatabase.updateCartTotalItemOnReturn(totalItem,idItem)

        appDatabase.updateTransactionTotalItemOnReturn(totalItem,idTransaction)
        appDatabase.updateTransactionTotalProfitOnReturn(profitFix,idTransaction)
        appDatabase.updateTransactionTotalPaymentOnReturn(totalRefund, idTransaction)
        appDatabase.updateTransactionChangeOnReturn(totalRefund, idTransaction)

        appDatabase.updateCashOutBalance(totalRefund)
        appDatabase.insertBalanceReport(
            BalanceReport(
                0,
                totalRefund,
                "Out",
                "Cash",
                "Return",
                username,
                currentDate
            )
        )
        var vReturn = appDatabase.validateItemOnReturn(idItem)
        if (vReturn != null) {
            if (vReturn < 0)
                appDatabase.deleteCart(idItem)
        }
        val intent = Intent(requireContext(), TransactionActivity::class.java)
        intent.putExtra("DATA_STATUS", "return")
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

}