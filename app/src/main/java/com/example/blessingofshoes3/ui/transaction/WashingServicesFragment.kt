package com.example.blessingofshoes3.ui.transaction

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.ReturnListClickListener
import com.example.blessingofshoes3.ServicesClickListener
import com.example.blessingofshoes3.adapter.CartClickListener
import com.example.blessingofshoes3.adapter.ServicesAdapter
import com.example.blessingofshoes3.adapter.WashingServicesAdapter
import com.example.blessingofshoes3.databinding.FragmentProductBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.db.Services
import com.example.blessingofshoes3.ui.ServicesActivity
import com.example.blessingofshoes3.ui.TransactionActivity
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class WashingServicesFragment : Fragment(), ServicesClickListener {

    private var binding: FragmentProductBinding? = null
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private lateinit var servicesAdapter: WashingServicesAdapter
    lateinit var serviceList: ArrayList<Services>
    lateinit var serviceListData: List<Services>
    lateinit var listService : ArrayList<Services>
    private lateinit var rvService: RecyclerView
    lateinit var listener: ServicesClickListener
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_washing, container, false)
        listener = this
        var btnAll = view.findViewById<Button>(R.id.btn_all)
        var btnNewest = view.findViewById<Button>(R.id.btn_newest)
        var btnOldest = view.findViewById<Button>(R.id.btn_oldest)
        var btnExpensive = view.findViewById<Button>(R.id.btn_expensive)
        var btnCheap = view.findViewById<Button>(R.id.btn_cheap)
        var btnDay = view.findViewById<Button>(R.id.btn_time)
        var btnAsc = view.findViewById<Button>(R.id.btn_a_to_z)
        var btnDesc = view.findViewById<Button>(R.id.btn_z_to_a)
        var edtReadNameProduct = view.findViewById<EditText>(R.id.edt_read_name_product)
        var btnSearch = view.findViewById<ImageView>(R.id.btn_search)
        btnAll.setOnClickListener{
            btnAll.setBackgroundResource(R.drawable.rounded_primary)
            btnAll.setTextColor(Color.WHITE)
            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnDay.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeAll()
            serviceList = ArrayList()
            rvServices()

        }
        btnNewest.setOnClickListener{
            btnNewest.setBackgroundResource(R.drawable.rounded_primary)
            btnNewest.setTextColor(Color.WHITE)
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnDay.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeTimeDESC()
            serviceList = ArrayList()
            rvServices()

        }
        btnOldest.setOnClickListener{
            btnOldest.setBackgroundResource(R.drawable.rounded_primary)
            btnOldest.setTextColor(Color.WHITE)
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnDay.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeTimeASC()
            serviceList = ArrayList()
            rvServices()

        }
        btnExpensive.setOnClickListener{
            btnExpensive.setBackgroundResource(R.drawable.rounded_primary)
            btnExpensive.setTextColor(Color.WHITE)
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnDay.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observePriceDESC()
            serviceList = ArrayList()
            rvServices()

        }
        btnCheap.setOnClickListener{
            btnCheap.setBackgroundResource(R.drawable.rounded_primary)
            btnCheap.setTextColor(Color.WHITE)
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnDay.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observePriceASC()
            serviceList = ArrayList()
            rvServices()

        }
        btnDay.setOnClickListener{
            btnDay.setBackgroundResource(R.drawable.rounded_primary)
            btnDay.setTextColor(Color.WHITE)
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeDay()
            serviceList = ArrayList()
            rvServices()

        }
        btnAsc.setOnClickListener{
            btnAsc.setBackgroundResource(R.drawable.rounded_primary)
            btnAsc.setTextColor(Color.WHITE)
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnDay.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeNameASC()
            serviceList = ArrayList()
            rvServices()

        }
        btnDesc.setOnClickListener{
            btnDesc.setBackgroundResource(R.drawable.rounded_primary)
            btnDesc.setTextColor(Color.WHITE)
            btnAll.setBackgroundResource(R.drawable.round_transparent_button)
            btnAll.setTextColor(view.context.getColor(R.color.light_green))
            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
            btnDay.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            observeNameDESC()
            serviceList = ArrayList()
            rvServices()

        }
        edtReadNameProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                when {
                    s.isNullOrBlank() -> {
                        edtReadNameProduct.error
                        btnAll.setBackgroundResource(R.drawable.rounded_primary)
                        btnAll.setTextColor(Color.WHITE)
                        btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnNewest.setTextColor(view.context.getColor(R.color.light_green))
                        btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnOldest.setTextColor(view.context.getColor(R.color.light_green))
                        btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
                        btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
                        btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
                        btnCheap.setTextColor(view.context.getColor(R.color.light_green))
                        btnDay.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDay.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                        observeAll()
                        serviceList = ArrayList()
                        rvServices()

                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when {
                    s.isNullOrBlank() -> {
                        edtReadNameProduct.error
                        btnAll.setBackgroundResource(R.drawable.rounded_primary)
                        btnAll.setTextColor(Color.WHITE)
                        btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnNewest.setTextColor(view.context.getColor(R.color.light_green))
                        btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnOldest.setTextColor(view.context.getColor(R.color.light_green))
                        btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
                        btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
                        btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
                        btnCheap.setTextColor(view.context.getColor(R.color.light_green))
                        btnDay.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDay.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                        observeAll()
                        serviceList = ArrayList()
                        rvServices()

                    }
                    else -> {
                        var readName = s.toString()
                        var extraName = "%"+readName+"%"
                        var validateName = appDatabase.validateServiceName(extraName)
                        if (validateName == 0) {
                            btnAll.setBackgroundResource(R.drawable.rounded_primary)
                            btnAll.setTextColor(Color.WHITE)
                            btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
                            btnNewest.setTextColor(view.context.getColor(R.color.light_green))
                            btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
                            btnOldest.setTextColor(view.context.getColor(R.color.light_green))
                            btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
                            btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
                            btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
                            btnCheap.setTextColor(view.context.getColor(R.color.light_green))
                            btnDay.setBackgroundResource(R.drawable.round_transparent_button)
                            btnDay.setTextColor(view.context.getColor(R.color.light_green))
                            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                        } else {
                            observeByName(extraName)
                            serviceList = ArrayList()
                            rvServices()

                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                when {
                    s.isNullOrBlank() -> {
                        edtReadNameProduct.error
                        btnAll.setBackgroundResource(R.drawable.rounded_primary)
                        btnAll.setTextColor(Color.WHITE)
                        btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnNewest.setTextColor(view.context.getColor(R.color.light_green))
                        btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnOldest.setTextColor(view.context.getColor(R.color.light_green))
                        btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
                        btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
                        btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
                        btnCheap.setTextColor(view.context.getColor(R.color.light_green))
                        btnDay.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDay.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                    } else ->{
                    var readName = s.toString()
                    var extraName = "%"+readName+"%"
                    var validateName = appDatabase.validateServiceName(extraName)
                    if (validateName == 0) {
                        btnAll.setBackgroundResource(R.drawable.rounded_primary)
                        btnAll.setTextColor(Color.WHITE)
                        btnNewest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnNewest.setTextColor(view.context.getColor(R.color.light_green))
                        btnOldest.setBackgroundResource(R.drawable.round_transparent_button)
                        btnOldest.setTextColor(view.context.getColor(R.color.light_green))
                        btnExpensive.setBackgroundResource(R.drawable.round_transparent_button)
                        btnExpensive.setTextColor(view.context.getColor(R.color.light_green))
                        btnCheap.setBackgroundResource(R.drawable.round_transparent_button)
                        btnCheap.setTextColor(view.context.getColor(R.color.light_green))
                        btnDay.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDay.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                    } else {
                        observeByName(extraName)
                        serviceList = ArrayList()
                        rvServices()
                    }
                }
                }
            }
        })
        btnSearch.setOnClickListener{
            var readNameService = edtReadNameProduct.text.toString()
            var validateName = appDatabase.validateServiceName(readNameService)
            var extraName = "%"+readNameService+"%"
            if (validateName == 0) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.service_name_validation))
                    .setConfirmText("Ok")
                    .show()
            } else {
                observeByName(extraName)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceList = ArrayList()
        rvServices()
        observeNotes()

    }

    private fun rvServices() {
        rvService = requireView().findViewById(R.id.rv_services)
        servicesAdapter = WashingServicesAdapter(context, serviceList, listener)
        rvService.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = servicesAdapter
        }

    }

    private fun observeNotes() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServices().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeAll() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServices().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeByName(extraName: String?) {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceByName(extraName).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeTimeDESC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceOrderByTimeDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeTimeASC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceOrderByTimeASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observePriceDESC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceOrderByPriceDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observePriceASC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceOrderByPriceASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeDay() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceOrderByDay().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeNameASC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceOrderByNameASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }
    private fun observeNameDESC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as TransactionActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllServiceOrderByNameDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    override fun onServicesClickListener(
        view: View,
        idItem: Int,
        nameItem: String,
        priceItem: Int,
        totalItem: Int,
        profitItem: Int,
        totalProfit: Int,
        totalPrice: Int,
        username: String?
    ) {
        var photoDefault = activity?.getDrawable(R.drawable.loading_image)?.toBitmap()
        viewModel.insertCart(
            Cart(
                0,
                idItem,
                nameItem,
                priceItem,
                totalItem,
                profitItem,
                totalProfit,
                totalPrice,
                username,
                "onWashing",
                0,
                photoDefault!!
            )
        )
        serviceList = ArrayList()
        rvServices()
        observeNotes()
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(getString(R.string.item_added))
            .setConfirmText("Ok")
            .show()
    }
}