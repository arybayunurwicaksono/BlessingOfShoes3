package com.example.blessingofshoes3.ui.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.PaymentAdapter
import com.example.blessingofshoes3.adapter.ServicesAdapter
import com.example.blessingofshoes3.databinding.FragmentProductReportBinding
import com.example.blessingofshoes3.databinding.FragmentServicesBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Services
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_services, container, false)


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
        servicesAdapter = ServicesAdapter(context, serviceList)
        rvService.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = servicesAdapter
        }

    }
    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllServices().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
                }
            }
        }
    }
}