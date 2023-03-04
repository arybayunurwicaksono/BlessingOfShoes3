package com.example.blessingofshoes3.ui.services

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.FragmentAddProductBinding
import com.example.blessingofshoes3.databinding.FragmentAddServicesBinding
import com.example.blessingofshoes3.db.*
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.ui.ServicesActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AddServicesFragment : Fragment() {

    private var binding: FragmentAddServicesBinding? = null
    private lateinit var appViewModel: AppViewModel
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    lateinit var sharedPref: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddServicesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = Preferences(requireContext())

        var servicematerialPrice : Int = 0
        var servicePrice : Int = 0
        binding?.apply {
            edtServiceMaterialPrice.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtServiceMaterialPrice.error = "Fill Material Price"
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
                            edtServiceMaterialPrice.error = "Fill Material Price"
                        }
                        else -> {
                            servicePrice = s.toString().toInt()
                            priceValue.text = servicePrice.toString()
                        }
                    }


                }
            })
            edtServicePrice.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtServicePrice.error = "Fill Service Price"
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    when {
                        s.isNullOrBlank() -> {
                            edtServicePrice.error = "Fill Total Purchases"
                        }
                        else -> {
                            servicePrice = s.toString().toInt()
                            servicematerialPrice = edtServiceMaterialPrice.text.toString().toInt()
                            var serviceProfit = servicePrice - servicematerialPrice

                            val localeID = Locale("in", "ID")
                            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                            profitValue.text = serviceProfit.toString()
                        }
                    }

                }
            })
            btnInsertService.setOnClickListener {
                val serviceName = edtServiceName.text.toString().trim()
                val serviceMaterial = edtServiceMaterialPrice.text.toString().trim()
                val servicePrice = edtServicePrice.text.toString().trim()
                var serviceEstimated = edtEstimatedService.text.toString().trim()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                when {
                    serviceName.isEmpty() -> {
                        edtServiceName.error = "Fill Real Price"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    serviceMaterial.isEmpty() -> {
                        edtServiceMaterialPrice.error = "Fill Supplier"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    servicePrice.isEmpty() -> {
                        edtServicePrice.error = "Fill Product Brand"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    serviceEstimated.isEmpty() -> {
                        edtEstimatedService.error = "Fill Price"
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some data is empty!")
                            .show()
                    }
                    else -> {
                        var serviceMaterialFix = serviceMaterial.toInt()
                        var servicePriceFix = servicePrice.toInt()
                        var serviceProfitFix = profitValue.text.toString().toInt()
                        var servoceEstimatedFix = serviceEstimated.toInt()
                        val username = appDatabase.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Service Data is Correct")
                            .setContentText("Insert Data?")
                            .setConfirmText("Yes")
                            .setCancelText("No")
                            .setConfirmClickListener { sDialog ->
                                lifecycleScope.launch {
                                    appDatabase.insertServices(
                                        Services(
                                            0,
                                            serviceName,
                                            serviceMaterialFix,
                                            servicePriceFix,
                                            serviceProfitFix,
                                            servoceEstimatedFix,
                                            username,
                                            currentDate
                                        )
                                    )
                                }
                                sDialog.dismissWithAnimation()
                                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Services Inserting Complete")
                                    .setConfirmText("ok")
                                    .setConfirmClickListener { pDialog ->
                                        finishTask()
                                        pDialog.dismissWithAnimation()
                                    }
                                    .show()
                            }
                            .show()


                    }

                }

            }

        }

    }
    private fun finishTask() {
        val i = Intent(requireContext(), ServicesActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

}