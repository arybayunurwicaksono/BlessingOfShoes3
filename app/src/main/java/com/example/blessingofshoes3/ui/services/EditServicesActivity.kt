package com.example.blessingofshoes3.ui.services

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ActivityEditProductBinding
import com.example.blessingofshoes3.databinding.ActivityEditServicesBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Services
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.ui.ServicesActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditServicesActivity : AppCompatActivity() {
    private lateinit var _activityEditServicesBinding: ActivityEditServicesBinding
    private val binding get() = _activityEditServicesBinding
    private val viewModel by viewModels<AppViewModel>()
    private var getFile: File? = null
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private var idServices : Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityEditServicesBinding = ActivityEditServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val eName = intent.getStringExtra("DATA_NAME")
        val eId = intent.getIntExtra("DATA_ID", 0)
        var username : String
        Log.i("extraId", "ID : $eId")
        var servicematerialPrice : Int = 0
        var servicePrice : Int = 0
        viewModel.readServicesItem(eId).observe(this, Observer {
            binding.profitValue.text = it.serviceProfit.toString()
            binding.edtServiceName.setText(it.serviceName.toString())
            binding.edtServicePrice.setText(it.serviceFinalPrice.toString())
            binding.edtServiceMaterialPrice.setText(it.serviceMaterialPrice.toString())
            binding.edtEstimatedService.setText(it.estimatedTime.toString())
            var fPrice = it.serviceFinalPrice!!
            var mPrice = it.serviceMaterialPrice!!
            var oPrice = fPrice - mPrice
            username = it.username.toString()
            binding.edtServiceMaterialPrice.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
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
                            binding.edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
                        }
                        else -> {
                            servicematerialPrice = s.toString().toInt()
                        }
                    }

                }
            })

            binding.edtServicePrice.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    when {
                        s.isNullOrBlank() -> {
                            binding.edtServicePrice.error = getString(R.string.fill_service_price)
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
                            binding.edtServicePrice.error = getString(R.string.fill_service_price)
                        }
                        else -> {
                            servicePrice = s.toString().toInt()
                            if (servicematerialPrice!=0) {
                                servicematerialPrice = binding.edtServiceMaterialPrice.text.toString().toInt()
                                var serviceProfit = servicePrice - servicematerialPrice

                                val localeID = Locale("in", "ID")
                                val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                                binding.profitValue.text = serviceProfit.toString()
                            } else {
                                SweetAlertDialog(applicationContext, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(getString(R.string.some_data_is_empty))
                                    .show()
                                binding.edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
                                binding.edtServicePrice.setText("")
                            }


                        }
                    }

                }
            })
            binding.btnInsertService.setOnClickListener {
                val serviceName = binding.edtServiceName.text.toString().trim()
                val serviceMaterial = binding.edtServiceMaterialPrice.text.toString().trim()
                val servicePrice = binding.edtServicePrice.text.toString().trim()
                var serviceEstimated = binding.edtEstimatedService.text.toString().trim()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                when {
                    serviceName.isEmpty() -> {
                        binding.edtServiceName.error = getString(R.string.fill_service_name)
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    serviceMaterial.isEmpty() -> {
                        binding.edtServiceMaterialPrice.error = getString(R.string.fill_service_material_price)
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    servicePrice.isEmpty() -> {
                        binding.edtServicePrice.error = getString(R.string.fill_service_price)
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    serviceEstimated.isEmpty() -> {
                        binding.edtEstimatedService.error = getString(R.string.fill_estimated_time)
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(getString(R.string.some_data_is_empty))
                            .show()
                    }
                    else -> {
                        var serviceMaterialFix = serviceMaterial.toInt()
                        var servicePriceFix = servicePrice.toInt()
                        var serviceProfitFix = binding.profitValue.text.toString().toInt()
                        var servoceEstimatedFix = serviceEstimated.toInt()
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getString(R.string.data_is_correct))
                            .setContentText(getString(R.string.update_data))
                            .setConfirmText(getString(R.string.save))
                            .setCancelText(getString(R.string.cancel))
                            .setConfirmClickListener { sDialog ->
                                viewModel.updateServiceItem(applicationContext, eId,
                                    serviceName,
                                    serviceMaterialFix,
                                    servicePriceFix,
                                    serviceProfitFix,
                                    servoceEstimatedFix,
                                    username,
                                    currentDate) {
                                    sDialog.dismissWithAnimation()
                                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(getString(R.string.service_updated))
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener { pDialog ->

                                            pDialog.dismissWithAnimation()
                                            val intent = Intent(this, ServicesActivity::class.java)
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .show()
                                }

                            }
                            .show()


                    }

                }

            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@EditServicesActivity, ServicesActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()

    }

}