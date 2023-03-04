package com.example.blessingofshoes3.ui.transaction

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.CartAdapter
import com.example.blessingofshoes3.adapter.CartClickListener
import com.example.blessingofshoes3.databinding.FragmentCartBinding
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CartFragment : Fragment(), CartClickListener {

    private var binding: FragmentCartBinding? = null
    private lateinit var cartAdapter: CartAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var productList: ArrayList<Product>
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var recyclerViewProduct: RecyclerView
    private val TAG = "CartFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
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
        view.findViewById<TextView>(R.id.btn_add_to_cart)

    }

    override fun onAddClick(product: Product, position: Int, qty: Int) {
        Log.d(TAG, "onItemClick: $product")

        var profitItem = product.profitProduct!!.toInt() * qty
        var totalPayment = (qty * product.priceProduct!!)
        sharedPref = Preferences(requireContext())
        val username = viewModel.readUsername(sharedPref.getString(Constant.PREF_EMAIL))
        var extraRealPrice = (product.realPriceProduct)?.toInt()
        var totalPurchasesFinal = qty * extraRealPrice!!

        viewModel.insertCart(
            Cart(
            0,
            product.idProduct,
            product.nameProduct,
            product.priceProduct,
            qty,
            product.profitProduct,
            profitItem,
            totalPayment,
            username,
            "onProgress",
            0,
            product.productPhoto
        )
        )

        viewModel.sumTotalPurchasesItem(product.idProduct, totalPurchasesFinal)
        viewModel.sumStockItem( product.idProduct, qty)
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Item Added")
            .setConfirmText("Ok")
            .show()
    }


    private fun rvProduct() {
        recyclerViewProduct = requireView().findViewById(R.id.rv_product_cart)
        cartAdapter = CartAdapter(context, productList)
        cartAdapter.listener = this
        recyclerViewProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
        }

    }

    private fun observeAll() {
        lifecycleScope.launch {
            viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeByName(extraName: String?) {
        lifecycleScope.launch {
            viewModel.getAllProductByName(extraName).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeTimeDESC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByTimeDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeTimeASC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByTimeASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observePriceDESC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByPriceDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observePriceASC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByPriceASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeBrand() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByBrand().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeSize() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderBySize().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeNameASC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByNameASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    private fun observeNameDESC() {
        lifecycleScope.launch {
            viewModel.getAllProductOrderByNameDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    cartAdapter.setProductData(itemList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}