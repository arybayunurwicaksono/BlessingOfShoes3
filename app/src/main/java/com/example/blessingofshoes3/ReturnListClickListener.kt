package com.example.blessingofshoes3

import android.view.View

interface ReturnListClickListener {
    fun onReturnListClickListener(view: View, idItem: Int, nameItem: String, priceItem: Int, totalItem: Int, totalRefund: Int, returnNote: String, profitItem: Int, idTransaction: Int?)
}