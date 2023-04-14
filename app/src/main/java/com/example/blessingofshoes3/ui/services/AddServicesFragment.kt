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
                            edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
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
                            edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
                        }
                        else -> {
                            servicematerialPrice = s.toString().toInt()
                        }
                    }

                }
            })

            edtServicePrice.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            edtServicePrice.error = getString(R.string.fill_service_price)
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
                            edtServicePrice.error = getString(R.string.fill_service_price)
                        }
                        else -> {
                            servicePrice = s.toString().toInt()
                            if (servicematerialPrice!=0) {
                                servicematerialPrice = edtServiceMaterialPrice.text.toString().toInt()
                                var serviceProfit = servicePrice - servicematerialPrice

                                val localeID = Locale("in", "ID")
                                val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                                profitValue.text = serviceProfit.toString()
                            } else {
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(getString(R.string.some_data_is_empty))
                                    .show()
                                edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
                                edtServicePrice.setText("")
                            }


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
                        edtServiceName.error = getString(R.string.fill_service_name)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    serviceMaterial.isEmpty() -> {
                        edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    servicePrice.isEmpty() -> {
                        edtServicePrice.error = getString(R.string.fill_service_price)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    serviceEstimated.isEmpty() -> {
                        edtEstimatedService.error = getString(R.string.fill_estimated_time)
                        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    else -> {
                        var serviceMaterialFix = serviceMaterial.toInt()
                        var servicePriceFix = servicePrice.toInt()
                        var serviceProfitFix = profitValue.text.toString().toInt()
                        var servoceEstimatedFix = serviceEstimated.toInt()
                        val username = appDatabase.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
                        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getString(R.string.data_is_correct))
                            .setContentText(getString(R.string.insert_data))
                            .setConfirmText(getString(R.string.save))
                            .setCancelText(getString(R.string.cancel))
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
                                    .setTitleText(getString(R.string.service_inserted))
                                    .setConfirmText("Ok")
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