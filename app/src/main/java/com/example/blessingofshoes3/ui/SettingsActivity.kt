package com.example.blessingofshoes3.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ActivityMainBinding
import com.example.blessingofshoes3.databinding.ActivitySettingsBinding
import com.mazenrashed.printooth.ui.ScanningActivity
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Get the switch view from layout
        getSupportActionBar()?.hide()
        /*val darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)

        // Set the switch to its last state from preferences
        val preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        darkModeSwitch.isChecked = preferences.getBoolean("isDarkModeOn", false)

        // Set on click listener for switch to toggle theme
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the switch to preferences
            val editor = preferences.edit()
            editor.putBoolean("isDarkModeOn", isChecked)
            editor.apply()

            // Change the theme of the app
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Recreate the activity to apply the new theme
            recreate()
        }*/

        binding.btnLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.btnPrinters.setOnClickListener {
            resultLauncher.launch(
                Intent(
                    this@SettingsActivity,
                    ScanningActivity::class.java
                ),
            )
        }
        binding.btnWhatsapp.setOnClickListener {
            openWhatsApp(binding.btnWhatsapp)
        }
    }

    fun openWhatsApp(view: View) {
        val pm = packageManager
        try {
            // Intent untuk membuka aplikasi WhatsApp
            val waIntent = Intent(Intent.ACTION_VIEW)
            val message = "Halo Developers Blessing Of Shoes App :)\nSaya ingin bercakapan dengan Anda!"
            waIntent.data = Uri.parse("https://wa.me/6282132378091/?text=${URLEncoder.encode(message, "UTF-8")}")
            startActivity(waIntent)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
        }
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER &&  result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
//            val intent = result.data

        }
    }

}