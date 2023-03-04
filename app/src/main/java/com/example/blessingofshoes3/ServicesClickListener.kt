package com.example.blessingofshoes3

import android.view.View

interface ServicesClickListener {
    fun onServicesClickListener(view: View, idItem: Int, nameItem: String, priceItem: Int, totalItem: Int, profitItem: Int, totalProfit: Int, totalPrice: Int, username: String?)
}
