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

    lateinit var viewPager: ViewPager
    lateinit var sharedPref: Preferences
    lateinit var sliderAdapter: SliderAdapter
    lateinit var sliderList: ArrayList<SliderData>

    lateinit var skipBtn: Button
    lateinit var indicatorSlideOneTV: TextView
    lateinit var indicatorSlideTwoTV: TextView
    lateinit var indicatorSlideThreeTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        getSupportActionBar()?.hide()
        val splashScreen = installSplashScreen()
        viewPager = findViewById(R.id.idViewPager)
        skipBtn = findViewById(R.id.idBtnSkip)
        indicatorSlideOneTV = findViewById(R.id.idTVSlideOne)
        indicatorSlideTwoTV = findViewById(R.id.idTVSlideTwo)
        indicatorSlideThreeTV = findViewById(R.id.idTVSlideThree)
        sharedPref = Preferences(this)

        skipBtn.setOnClickListener {

            val i = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(i)
        }

        sliderList = ArrayList()

        sliderList.add(
            SliderData(
                getString(R.string.product),
                getString(R.string.slide_1),
                R.drawable.img_shoes
            )
        )

        sliderList.add(
            SliderData(
                getString(R.string.transaction),
                getString(R.string.slide_2),
                R.drawable.img_cashier
            )
        )

        sliderList.add(
            SliderData(
                getString(R.string.report),
                getString(R.string.slide_3),
                R.drawable.img_report
            )
        )

        sliderAdapter = SliderAdapter(this, sliderList)

        viewPager.adapter = sliderAdapter

        viewPager.addOnPageChangeListener(viewListener)

    }

    var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {

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