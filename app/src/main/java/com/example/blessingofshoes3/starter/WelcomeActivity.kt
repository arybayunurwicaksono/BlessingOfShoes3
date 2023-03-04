package com.example.blessingofshoes3.starter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager.widget.ViewPager
import com.example.blessingofshoes3.MainActivity
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.SliderAdapter
import com.example.blessingofshoes3.ui.LoginActivity
import com.example.blessingofshoes3.utils.Constant
import com.example.blessingofshoes3.utils.Preferences

class WelcomeActivity : AppCompatActivity() {

    // on below line we are creating a
    // variable for our view pager
    lateinit var viewPager: ViewPager
    lateinit var sharedPref: Preferences
    // on below line we are creating a variable
    // for our slider adapter and slider list
    lateinit var sliderAdapter: SliderAdapter
    lateinit var sliderList: ArrayList<SliderData>

    // on below line we are creating a variable for our
    // skip button, slider indicator text view for 3 dots
    lateinit var skipBtn: Button
    lateinit var indicatorSlideOneTV: TextView
    lateinit var indicatorSlideTwoTV: TextView
    lateinit var indicatorSlideThreeTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        getSupportActionBar()?.hide()
        // on below line we are initializing all
        // our variables with their ids.
        val splashScreen = installSplashScreen()
        viewPager = findViewById(R.id.idViewPager)
        skipBtn = findViewById(R.id.idBtnSkip)
        indicatorSlideOneTV = findViewById(R.id.idTVSlideOne)
        indicatorSlideTwoTV = findViewById(R.id.idTVSlideTwo)
        indicatorSlideThreeTV = findViewById(R.id.idTVSlideThree)
        sharedPref = Preferences(this)
        // on below line we are adding click listener for our skip button
        skipBtn.setOnClickListener {
            // on below line we are opening a new activity
            val i = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(i)
        }

        // on below line we are initializing our slider list.
        sliderList = ArrayList()

        // on below line we are adding data to our list
        sliderList.add(
            SliderData(
                "Product",
                "Manage your product for the better income",
                R.drawable.img_shoes
            )
        )

        sliderList.add(
            SliderData(
                "Transaction",
                "Make a transaction with customer easily",
                R.drawable.img_cashier
            )
        )

        sliderList.add(
            SliderData(
                "Report",
                "Manage your transaction & balance report",
                R.drawable.img_report
            )
        )

        // on below line we are adding slider list
        // to our adapter class.
        sliderAdapter = SliderAdapter(this, sliderList)

        // on below line we are setting adapter
        // for our view pager on below line.
        viewPager.adapter = sliderAdapter

        // on below line we are adding page change
        // listener for our view pager.
        viewPager.addOnPageChangeListener(viewListener)

    }

    // creating a method for view pager for on page change listener.
    var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            // we are calling our dots method to
            // change the position of selected dots.

            // on below line we are checking position and updating text view text color.
            if (position == 0) {
                indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.light_green_400))
                indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.light_green_400))
                indicatorSlideOneTV.setTextColor(resources.getColor(R.color.white))

            } else if (position == 1) {
                indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.white))
                indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.light_green_400))
                indicatorSlideOneTV.setTextColor(resources.getColor(R.color.light_green_400))
            } else {
                indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.light_green_400))
                indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.white))
                indicatorSlideOneTV.setTextColor(resources.getColor(R.color.light_green_400))
            }
        }

        // below method is use to check scroll state.
        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onStart() {
        super.onStart()
        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}