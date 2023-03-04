package com.example.blessingofshoes3.ui.product

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.ProductAdapter
import com.example.blessingofshoes3.databinding.FragmentProductBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private var binding: FragmentProductBinding? = null
    private lateinit var productAdapter: ProductAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var productList: ArrayList<Product>
    lateinit var productListData: List<Product>
    private lateinit var ProductListItem: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }
    private lateinit var recyclerViewProduct: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        var btnAll = view.findViewById<Button>(R.id.btn_all)
        var btnNewest = view.findViewById<Button>(R.id.btn_newest)
        var btnOldest = view.findViewById<Button>(R.id.btn_oldest)
        var btnExpensive = view.findViewById<Button>(R.id.btn_expensive)
        var btnCheap = view.findViewById<Button>(R.id.btn_cheap)
        var btnBrand = view.findViewById<Button>(R.id.btn_brand)
        var btnSize = view.findViewById<Button>(R.id.btn_size)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeAll()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeTimeDESC()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeTimeASC()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observePriceDESC()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observePriceASC()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
        }
        btnBrand.setOnClickListener{
            btnBrand.setBackgroundResource(R.drawable.rounded_primary)
            btnBrand.setTextColor(Color.WHITE)
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
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeBrand()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
        }
        btnSize.setOnClickListener{
            btnSize.setBackgroundResource(R.drawable.rounded_primary)
            btnSize.setTextColor(Color.WHITE)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeSize()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
            observeNameASC()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
            btnSize.setTextColor(view.context.getColor(R.color.light_green))
            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
            observeNameDESC()
            productList = ArrayList()
            rvProduct()
            val pDialog = SweetAlertDialog(view.context, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Loading Data"
            pDialog.setCancelable(true)
            pDialog.show()
            val time: Long = 2500
            Handler().postDelayed({
                pDialog.dismissWithAnimation()
            }, time)
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
                        btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
                        btnBrand.setTextColor(view.context.getColor(R.color.light_green))
                        btnSize.setBackgroundResource(R.drawable.round_transparent_button)
                        btnSize.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                        observeAll()
                        productList = ArrayList()
                        rvProduct()

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
                        btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
                        btnBrand.setTextColor(view.context.getColor(R.color.light_green))
                        btnSize.setBackgroundResource(R.drawable.round_transparent_button)
                        btnSize.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                        observeAll()
                        productList = ArrayList()
                        rvProduct()

                    }
                    else -> {
                        var readName = s.toString()
                        var extraName = "%"+readName+"%"
                        var validateName = appDatabase.validateProductName(extraName)
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
                            btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
                            btnBrand.setTextColor(view.context.getColor(R.color.light_green))
                            btnSize.setBackgroundResource(R.drawable.round_transparent_button)
                            btnSize.setTextColor(view.context.getColor(R.color.light_green))
                            btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                            btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                            btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                            btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                            /*observeAll()
                            productList = ArrayList()
                            rvProduct()*/
                        } else {
                            observeByName(extraName)
                            productList = ArrayList()
                            rvProduct()

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
                        btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
                        btnBrand.setTextColor(view.context.getColor(R.color.light_green))
                        btnSize.setBackgroundResource(R.drawable.round_transparent_button)
                        btnSize.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                        /*observeAll()
                        productList = ArrayList()
                        rvProduct()*/
                    } else ->{
                    var readName = s.toString()
                    var extraName = "%"+readName+"%"
                    var validateName = appDatabase.validateProductName(extraName)
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
                        btnBrand.setBackgroundResource(R.drawable.round_transparent_button)
                        btnBrand.setTextColor(view.context.getColor(R.color.light_green))
                        btnSize.setBackgroundResource(R.drawable.round_transparent_button)
                        btnSize.setTextColor(view.context.getColor(R.color.light_green))
                        btnAsc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnAsc.setTextColor(view.context.getColor(R.color.light_green))
                        btnDesc.setBackgroundResource(R.drawable.round_transparent_button)
                        btnDesc.setTextColor(view.context.getColor(R.color.light_green))
                        /*observeAll()
                        productList = ArrayList()
                        rvProduct()*/
                    } else {
                        observeByName(extraName)
                        productList = ArrayList()
                        rvProduct()
                    }
                }
                }
            }
        })

        btnSearch.setOnClickListener{
            var readNameProduct = edtReadNameProduct.text.toString()
            var validateName = appDatabase.validateProductName(readNameProduct)
            var extraName = "%"+readNameProduct+"%"
            if (validateName == 0) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Name is Incorrect")
                    .setContentText("PLEASE INSERT NAME PRODUCT CORRECTLY")
                    .setConfirmText("OK")
                    .show()
            } else {
                observeByName(extraName)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAll()
        productList = ArrayList()
        rvProduct()
        initAction()

    }

    private fun initAction() {
        val callback = Callback()
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(recyclerViewProduct)
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun rvProduct() {
        recyclerViewProduct = requireView().findViewById(R.id.rv_product)
        productAdapter = ProductAdapter(context, productList)
        recyclerViewProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = productAdapter
        }
        productAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showSelectedItem(data)
                /*val intentToDetail = Intent(context, EditProductActivity::class.java)
                intentToDetail.putExtra("DATA", data)
                intentToDetail.putExtra("DATA_ID", data.idProduct)
                intentToDetail.putExtra("DATA_NAME", data.nameProduct)
//                intentToDetail.putExtra("DATA_PRICE", data.priceProduct)
//                intentToDetail.putExtra("DATA_STOCK", data.stockProduct)
                //intentToDetail.putExtra("DATA_PHOTO", data.productPhoto)
                startActivity(intentToDetail)*/
            }
        })

    }

    private fun observeAll() {
        lifecycleScope.launch {
            viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeByName(extraName: String?) {
        lifecycleScope.launch {
            viewModel.getAllProductByName(extraName).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeTimeDESC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByTimeDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeTimeASC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByTimeASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observePriceDESC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByPriceDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observePriceASC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByPriceASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeBrand() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByBrand().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeSize() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderBySize().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeNameASC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByNameASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeNameDESC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByNameDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun showSelectedItem(item: Product) {
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
            val data = productListData[position]
            val data2 = productListData[position]
            //Toast.makeText(context, "Berhasil Menghapus : " + data.nameProduct, Toast.LENGTH_LONG).show()
            /*Snackbar.make(requireView(), "Deleted " + data.nameProduct, Snackbar.LENGTH_LONG)
                .setAction(
                    "Undo",
                    View.OnClickListener {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.
                        viewModel.insertProduct(data2)
                        viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                            if (itemList != null) {
                                productListData = itemList
                                productAdapter.setProductData(itemList)
                            }
                        }
                        // below line is to notify item is
                        // added to our adapter class.
                        productAdapter.notifyItemInserted(position)

                    }).show()*/


            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Delete this "+ data.nameProduct.toString() + "?")
                .setContentText("You cannot undo this event!")
                .setCustomImage(R.drawable.logo_round)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteProduct(data.idProduct)
                    sDialog.dismissWithAnimation()
                }
                .setCancelText("Cancel")
                .setCancelClickListener { pDialog ->
                    viewModel.insertProduct(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }



    }
}