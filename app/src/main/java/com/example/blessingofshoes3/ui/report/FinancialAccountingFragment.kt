package com.example.blessingofshoes3.ui.report

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.AccountingAdapter
import com.example.blessingofshoes3.databinding.FragmentFinancialAccountingBinding
import com.example.blessingofshoes3.db.Accounting
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FinancialAccountingFragment : Fragment(), AccountingAdapter.AccountingClickListener {

    private var binding: FragmentFinancialAccountingBinding? = null
    private lateinit var accountingAdapter: AccountingAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var accountingList: ArrayList<Accounting>
    lateinit var accountingListData: List<Accounting>
    lateinit var sharedPref: Preferences
    private lateinit var accountingListItem: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var rvAccounting: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_financial_accounting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeNotes()
        sharedPref = Preferences(requireContext())

        accountingList = ArrayList()
        val sdf = SimpleDateFormat("MMMM/yyyy")
        val currentDate = sdf.format(Date())
        var btnGenerate : Button = requireView().findViewById(R.id.btn_generate)
        btnGenerate.setOnClickListener {
            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Generating Data")
                .setContentText("Do you wan't to generate financial accounting for "+currentDate+"?")
                .setConfirmText("Yes")
                .setCustomImage(R.drawable.logo_round)
                .setCancelText("No")
                .setConfirmClickListener { sDialog ->
                    val intent = Intent(context, GenerateAccountingActivity::class.java)
                    startActivity(intent)
                    sDialog.dismissWithAnimation()
                }
                .show()
        }
        binding?.apply {

        }
        rvAccounting()
        initAction()

    }
    override fun onAddClick(accounting: Accounting, position: Int) {

    }
    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvAccounting)
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllAccounting().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    accountingListData = itemList
                    accountingAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun rvAccounting() {
        rvAccounting = requireView().findViewById(R.id.rv_accounting)
        accountingAdapter = AccountingAdapter(context, accountingList)
        rvAccounting.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = accountingAdapter
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun showSelectedItem(item: Accounting) {
        //Toast.makeText(context, "Kamu memilih " + item.nameProduct, Toast.LENGTH_SHORT).show()
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
            val data = accountingListData[position]
            val data2 = accountingListData[position]


            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Delete this "+ data.idAccounting.toString() + "?")
                .setContentText("You cannot undo this event!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteAccounting(data.idAccounting)
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Cancel")
                .setCancelClickListener { pDialog ->
                    viewModel.insertAccounting(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }



    }
}