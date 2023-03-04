package com.example.blessingofshoes3.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.ui.report.BalanceReportFragment
import com.example.blessingofshoes3.ui.report.FinancialAccountingFragment
import com.example.blessingofshoes3.ui.report.ProductReportFragment
import com.example.blessingofshoes3.ui.report.TransactionReportFragment
import com.example.blessingofshoes3.ui.services.AddServicesFragment
import com.example.blessingofshoes3.ui.services.ServicesFragment
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServicesActivity : AppCompatActivity() {

    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }

    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)
        getSupportActionBar()?.hide()
        loadFragment(ServicesFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomServicesNavigationView)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.addServiceFragment -> {
                    loadFragment(AddServicesFragment())
                    true
                }
                R.id.serviceFragment -> {
                    loadFragment(ServicesFragment())
                    true
                }
                else -> {
                    loadFragment(ServicesFragment())
                    true
                }
            }
        }

    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.services_nav_fragment,fragment)
        transaction.commit()
    }

}