package com.example.blessingofshoes3.ui.report

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
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
import com.example.blessingofshoes3.ui.ReportActivity
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
    private lateinit var progressBar: ProgressBar

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
        val sdf2 = SimpleDateFormat("MM/yyyy")
        val currentDate2 = sdf2.format(Date())
        val time = "%"+currentDate2+"%"
        var validateCountBalance = appDatabase.validateCountBalanceByMonth(time)!!
        btnGenerate.setOnClickListener {
            if (validateCountBalance!=0) {
                SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText(getString(R.string.some_data_is_empty))
                    .setConfirmText("Ok")
                    .show()
            } else {
                SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText(getString(R.string.generate_data))
                    .setContentText(getString(R.string.generate_confirmation)+currentDate+"?")
                    .setConfirmText(getString(R.string.yes))
                    .setCustomImage(R.drawable.ic_baseline_info_24)
                    .setCancelText(getString(R.string.no))
                    .setConfirmClickListener { sDialog ->
                        val intent = Intent(context, GenerateAccountingActivity::class.java)
                        startActivity(intent)
                        sDialog.dismissWithAnimation()
                    }
                    .show()
            }

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
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllAccounting().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    accountingListData = itemList
                    accountingAdapter.setProductData(itemList)
                    activity.setClickable(true)
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
                .setTitleText(getString(R.string.delete)+ data.idAccounting.toString() + "?")
                .setContentText(getString(R.string.event_confirmation))
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteAccounting(data.idAccounting)
                    sDialog.dismissWithAnimation()
                }
                .setCancelText(getString(R.string.cancel))
                .setCancelClickListener { pDialog ->
                    viewModel.insertAccounting(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }
    }
}