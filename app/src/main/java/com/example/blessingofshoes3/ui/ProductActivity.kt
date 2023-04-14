package com.example.blessingofshoes3.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.ui.product.AddProductFragment
import com.example.blessingofshoes3.ui.product.ProductFragment
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*
@AndroidEntryPoint
class ProductActivity : AppCompatActivity() {

    private var printing : Printing? = null
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private var isClickable = true
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        getSupportActionBar()?.hide()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomProductNavigationView)
        val navController = findNavController(R.id.product_nav_fragment)
        bottomNavigationView.setupWithNavController(navController)
        loadFragment(ProductFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomProductNavigationView)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.productFragment -> {
                    loadFragment(ProductFragment())
                    true
                }
                R.id.addProductFragment -> {
                    loadFragment(AddProductFragment())
                    true
                }
                else -> {
                    loadFragment(AddProductFragment())
                    true
                }
            }
        }

    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.product_nav_fragment,fragment)
        transaction.commit()
    }

    fun setClickable(clickable: Boolean) {
        isClickable = clickable
        if (clickable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }
}