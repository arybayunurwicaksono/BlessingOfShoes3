package com.example.blessingofshoes3.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.ui.product.AddProductFragment
import com.example.blessingofshoes3.ui.product.ProductFragment
import com.example.blessingofshoes3.ui.report.BalanceReportFragment
import com.example.blessingofshoes3.ui.report.FinancialAccountingFragment
import com.example.blessingofshoes3.ui.report.ProductReportFragment
import com.example.blessingofshoes3.ui.report.TransactionReportFragment
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportActivity : AppCompatActivity() {

    private var printing : Printing? = null
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }

    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        getSupportActionBar()?.hide()
        loadFragment(TransactionReportFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomReportNavigationView)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.transactionReportFragment -> {
                    loadFragment(TransactionReportFragment())
                    true
                }
                R.id.financialAccountingFragment -> {
                    loadFragment(FinancialAccountingFragment())
                    true
                }
                R.id.balanceReportFragment -> {
                    loadFragment(BalanceReportFragment())
                    true
                }
                R.id.productReportFragment -> {
                    loadFragment(ProductReportFragment())
                    true
                }
                else -> {
                    loadFragment(TransactionReportFragment())
                    true
                }
            }
        }

    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.report_nav_fragment,fragment)
        transaction.commit()
    }

}