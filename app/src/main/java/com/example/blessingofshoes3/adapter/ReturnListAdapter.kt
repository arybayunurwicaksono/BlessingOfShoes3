package com.example.blessingofshoes3.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemRestockBinding
import com.example.blessingofshoes3.databinding.ItemReturnedBinding
import com.example.blessingofshoes3.db.Return
import java.text.NumberFormat
import java.util.*

class ReturnListAdapter (private val context: Context?, private var getItem: List<Return>) : RecyclerView.Adapter<ReturnListAdapter.ReturnViewHolder>() {

    inner class ReturnViewHolder (val binding: ItemReturnedBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnViewHolder {
        val binding = ItemReturnedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReturnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReturnViewHolder, position: Int) {
        val currentItem = getItem[position]
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.username.text = currentItem!!.username
        holder.binding.usernameTitle.text = context!!.getString(R.string.username)
        holder.binding.totalItemTitle.text = context!!.getString(R.string.total_item)
        holder.binding.tvProductName.text = currentItem!!.nameItem
        holder.binding.tvIdReturn.text = "#"+ currentItem!!.idReturn.toString()
        holder.binding.txtTglReturn.text = currentItem!!.returnDate
        holder.binding.refundTotalValue.text = numberFormat.format(currentItem.totalRefund!!.toDouble()).toString()
        holder.binding.totalItemValue.text = currentItem!!.totalItem.toString() + " Item"
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.content.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.content.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.content.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }

    }


    override fun getItemCount(): Int = getItem.size

    fun setProductData(postList: List<Return>)
    {

        this.getItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Return)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}