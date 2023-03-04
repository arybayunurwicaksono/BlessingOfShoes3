package com.example.blessingofshoes3.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemCartBinding
import com.example.blessingofshoes3.databinding.ItemRestockBinding
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Restock
import java.text.NumberFormat
import java.util.*

class RestockAdapter (private val context: Context?, private var getItem: List<Restock>) : RecyclerView.Adapter<RestockAdapter.RestockViewHolder>() {

    inner class RestockViewHolder (val binding: ItemRestockBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestockViewHolder {
        val binding = ItemRestockBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RestockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestockViewHolder, position: Int) {
        val currentItem = getItem[position]
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.username.text = currentItem!!.username
        holder.binding.usernameTitle.text = "Username"
        holder.binding.stockTitle.text = "Stock"
        holder.binding.supplierTitle.text = "Supplier"
        holder.binding.tvIdRestock.text = "#00"+ currentItem!!.idRestock.toString()
        holder.binding.tvProductName.text = currentItem!!.nameProduct
        holder.binding.tvSupplier.text = currentItem!!.supplier
        holder.binding.txtTglRestock.text = currentItem!!.restockDate
        holder.binding.txtItemTotalPurchases.text = numberFormat.format(currentItem.totalPurchases!!.toDouble()).toString()
        holder.binding.txtItemTotalRestock.text = currentItem!!.stockAdded.toString() + "Item"
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.txtItemTotalRestock.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.username.visibility = View.VISIBLE
                holder.binding.usernameTitle.visibility = View.VISIBLE
                holder.binding.stockTitle.visibility = View.VISIBLE
                holder.binding.supplierTitle.visibility = View.VISIBLE
                holder.binding.txtItemTotalPurchasesTitle.visibility = View.VISIBLE
                holder.binding.txtItemTotalPurchases.visibility = View.VISIBLE
                holder.binding.txtItemTotalRestock.visibility = View.VISIBLE
                holder.binding.tvSupplier.visibility = View.VISIBLE
                holder.binding.divider.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.username.visibility = View.GONE
                holder.binding.usernameTitle.visibility = View.GONE
                holder.binding.stockTitle.visibility = View.GONE
                holder.binding.supplierTitle.visibility = View.GONE
                holder.binding.txtItemTotalPurchasesTitle.visibility = View.GONE
                holder.binding.txtItemTotalPurchases.visibility = View.GONE
                holder.binding.txtItemTotalRestock.visibility = View.GONE
                holder.binding.tvSupplier.visibility = View.GONE
                holder.binding.divider.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }

    }


    override fun getItemCount(): Int = getItem.size

    fun setProductData(postList: List<Restock>)
    {

        this.getItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Restock)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}