package com.example.blessingofshoes3.adapter

import android.content.Context
import android.graphics.Color.BLUE
import android.graphics.Color.blue
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemBalanceReportBinding
import com.example.blessingofshoes3.databinding.ItemProductBinding
import com.example.blessingofshoes3.db.BalanceReport
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class BalanceReportAdapter (private val context: Context?, private var reportItem: List<BalanceReport>) : RecyclerView.Adapter<BalanceReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder (val binding: ItemBalanceReportBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemBalanceReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val currentItem = reportItem[position]
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.BalanceTitle.text = currentItem!!.reportTag + " with " + currentItem!!.typePayment + " (" + currentItem!!.status + ")"
        holder.binding.BalanceValue.text = numberFormat.format(currentItem.totalBalance!!.toDouble()).toString()

        if (currentItem!!.reportTag == "Transaction") {
            if (currentItem!!.typePayment == "Cash") {
                holder.binding.balanceLayout.setBackgroundResource(R.color.deep_green)
            } else if (currentItem!!.typePayment == "Digital") {
                holder.binding.balanceLayout.setBackgroundResource(R.color.blue_600)
            }
        } else {
            if (currentItem!!.status == "Out") {
                holder.binding.balanceLayout.setBackgroundResource(R.color.light_red)
            }
        }

    }


    override fun getItemCount(): Int = reportItem.size



    fun setReportData(postList: List<BalanceReport>)
    {

        this.reportItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}