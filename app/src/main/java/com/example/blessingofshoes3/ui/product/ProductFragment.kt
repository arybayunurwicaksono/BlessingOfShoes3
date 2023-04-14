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
import android.widget.*
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
import com.example.blessingofshoes3.ui.ProductActivity
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private var binding: FragmentProductBinding? = null
    private lateinit var productAdapter: ProductAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var productList: ArrayList<Product>
    lateinit var productListData: List<Product>
    private val appDatabase by lazy { AppDb.getDatabase(requireContext()).dbDao() }
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
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
                    .setTitleText(getString(R.string.name_validation))
                    .setConfirmText("Ok")
                    .show()
            } else {
                observeByName(extraName)
            }
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

        var tvStockType = view.findViewById<TextView>(R.id.total_product_value)
        var tvStockWorth = view.findViewById<TextView>(R.id.stock_worth_value)
        var tvStockTotal = view.findViewById<TextView>(R.id.stock_stored_value)
        var tvProductIn = view.findViewById<TextView>(R.id.product_in_value)
        var validateCountProduct2 = appDatabase.validateCountProduct()!!
        if (validateCountProduct2 == 0) {
            tvStockType.text = "0 " + getString(R.string.type)
            tvStockWorth.text = "Rp0,00"
            tvStockTotal.text = "0 " + getString(R.string.product)
            tvProductIn.text = "0 " + getString(R.string.product)
        } else {
            tvStockType.text = validateCountProduct2.toString() +" "  + getString(R.string.type)
            tvStockWorth.text = (numberFormat.format(appDatabase.readTotalStockWorth()!!.toDouble()).toString())
            tvStockTotal.text = appDatabase.readTotalStock()!!.toString() + " " + getString(R.string.product)
            tvProductIn.text = appDatabase.sumTotalRestockAdded().toString() + " " + getString(R.string.product)
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
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
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
    }

    private fun observeAll() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProduct().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeByName(extraName: String?) {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductByName(extraName).observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeTimeDESC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderByTimeDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeTimeASC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderByTimeASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observePriceDESC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderByPriceDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observePriceASC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderByPriceASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeBrand() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderByBrand().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeSize() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderBySize().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeNameASC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderByNameASC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun observeNameDESC() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ProductActivity
        activity.setClickable(false)
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllProductOrderByNameDESC().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    productListData = itemList
                    productAdapter.setProductData(itemList)
                    activity.setClickable(true)
                }
            }
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
            val data = productListData[position]
            val data2 = productListData[position]

            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(getString(R.string.delete)+ data.nameProduct.toString() + "?")
                .setContentText(getString(R.string.event_confirmation))
                .setCustomImage(R.drawable.img_delete)
                .setConfirmText("Ok")
                .setConfirmClickListener { sDialog ->
                    viewModel.deleteProduct(data.idProduct)
                    sDialog.dismissWithAnimation()
                }
                .setCancelText(getString(R.string.cancel))
                .setCancelClickListener { pDialog ->
                    viewModel.insertProduct(data2)
                    observeNotes()
                    pDialog.dismissWithAnimation()
                }
                .show()
        }
    }
}