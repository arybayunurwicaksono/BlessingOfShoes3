package com.example.blessingofshoes3.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.ui.product.AddProductFragment
import com.example.blessingofshoes3.ui.product.ProductFragment
import com.example.blessingofshoes3.ui.report.*
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportActivity : AppCompatActivity() {

    private var printing : Printing? = null
    private val viewModel by viewModels<AppViewModel>()
    lateinit var sharedPref: Preferences
    private val appDatabase by lazy { AppDb.getDatabase(this).dbDao() }
    private var isClickable = true
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        getSupportActionBar()?.hide()
        loadFragment(TransactionReportFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomReportNavigationView)
        var btnByUsers = findViewById<Button>(R.id.btn_users)
        sharedPref = Preferences(this)
        val validateRole = viewModel.getUserRole(sharedPref.getString(Constant.PREF_EMAIL)!!)
        if (validateRole == "Admin") {
            btnByUsers.visibility = VISIBLE
        } else {
            btnByUsers.visibility = GONE
        }
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.transactionReportFragment -> {
                    loadFragment(TransactionReportFragment())
                    if (validateRole == "Admin") {
                        btnByUsers.visibility = VISIBLE
                    } else {
                        btnByUsers.visibility = GONE
                    }
                    true
                }
                R.id.financialAccountingFragment -> {
                    loadFragment(FinancialAccountingFragment())
                    btnByUsers.visibility = GONE
                    true
                }
                R.id.balanceReportFragment -> {
                    loadFragment(BalanceReportFragment())
                    btnByUsers.visibility = GONE
                    true
                }
                R.id.productReportFragment -> {
                    loadFragment(ProductReportFragment())
                    btnByUsers.visibility = GONE
                    true
                }
                else -> {
                    loadFragment(TransactionReportFragment())
                    if (validateRole == "Admin") {
                        btnByUsers.visibility = VISIBLE
                    } else {
                        btnByUsers.visibility = GONE
                    }
                    true
                }
            }
        }
        btnByUsers.setOnClickListener {
            loadFragment(UserListFragment())
        }

    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.report_nav_fragment,fragment)
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