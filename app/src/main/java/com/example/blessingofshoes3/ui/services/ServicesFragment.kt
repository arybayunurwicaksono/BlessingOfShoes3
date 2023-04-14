package com.example.blessingofshoes3.ui.services

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.PaymentAdapter
import com.example.blessingofshoes3.adapter.ServicesAdapter
import com.example.blessingofshoes3.databinding.FragmentProductReportBinding
import com.example.blessingofshoes3.databinding.FragmentServicesBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Services
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.ui.ServicesActivity
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class ServicesFragment : Fragment() {

    private var binding: FragmentServicesBinding? = null
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var servicesAdapter: ServicesAdapter
    lateinit var serviceList: ArrayList<Services>
    lateinit var serviceListData: List<Services>
    lateinit var listService : ArrayList<Services>
    private lateinit var rvService: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_services, container, false)

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

        var tvServiceType = view.findViewById<TextView>(R.id.service_type_value)
        var validateCountService = appDatabase.validateCountService()!!
        if (validateCountService!=0){
            tvServiceType.text = validateCountService.toString() + " " + getString(R.string.services)
        } else {
            tvServiceType.text = "0 " + getString(R.string.services)
        }
        var btnInformation = view.findViewById<ImageView>(R.id.btn_help)

        btnInformation.setOnClickListener {
            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setCustomImage(R.drawable.ic_baseline_info_24)
                .setTitleText(getString(R.string.information_title))
                .setContentText(getString(R.string.delete_tutorial))
                .setConfirmText(getString(R.string.got_it))
                .show()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceList = ArrayList()
        rvServices()
        observeNotes()
        initAction()
    }

    private fun rvServices() {
        rvService = requireView().findViewById(R.id.rv_services)
        servicesAdapter = ServicesAdapter(context, serviceList)
        rvService.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = servicesAdapter
        }

    }
    private fun observeNotes() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
        val activity = getActivity() as ServicesActivity
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
    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(rvService)
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
            val data = serviceListData[position]
            val data2 = serviceListData[position]

            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(getString(R.string.delete)+" "+ data.serviceName.toString() + "?")
                .setContentText(getString(R.string.event_confirmation))
                .setConfirmText("Ok")
                .setCustomImage(R.drawable.img_delete)
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteServices(data.idServices)
                    sDialog.dismissWithAnimation()
                }
                .setCancelText(getString(R.string.cancel))
                .setCancelClickListener { pDialog ->
                    lifecycleScope.launch {
                        appDatabase.insertServices(
                            data2
                        )
                    }
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }

    }
}