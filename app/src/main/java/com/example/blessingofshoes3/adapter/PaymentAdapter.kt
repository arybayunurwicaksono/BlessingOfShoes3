package com.example.blessingofshoes3.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemCartBinding
import com.example.blessingofshoes3.databinding.ItemProductBinding
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class PaymentAdapter (private val context: Context?, private var cartItem: List<Cart>) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder (val binding: ItemCartBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val currentItem = cartItem[position]
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvTotalPayment.text = numberFormat.format(currentItem.totalpayment!!.toDouble()).toString()
        holder.binding.tvItemName.text = currentItem!!.nameItem
        holder.binding.tvItemTotal.text = "(" + currentItem!!.totalItem.toString() + " x"
        holder.binding.tvProductPrice.text = numberFormat.format(currentItem.priceItem!!.toDouble()).toString() + ")"
        Glide.with(holder.itemView.context)
            .load(currentItem!!.productPhoto)
            .fitCenter()
            .into(holder.binding.imageView)

    }


    override fun getItemCount(): Int = cartItem.size

    fun setProductData(postList: List<Cart>)
    {

        this.cartItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Cart)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}