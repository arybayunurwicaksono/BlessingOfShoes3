package com.example.blessingofshoes3.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_washing, container, false)
        listener = this
        /*var validateWashingServices = appDatabase.validateWashingServicesItem()
        if (validateWashingServices == 0) {
            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("You have not managed washing services")
                .setContentText("Arrange washing service now?")
                .show()
        }*/



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
        lifecycleScope.launch {
            viewModel.getAllServices().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    serviceListData = itemList
                    servicesAdapter.setServicesData(itemList)
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
            .setTitleText("Item Added")
            .setConfirmText("Ok")
            .show()
    }
}