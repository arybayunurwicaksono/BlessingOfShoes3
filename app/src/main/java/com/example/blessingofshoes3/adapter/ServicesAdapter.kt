package com.example.blessingofshoes3.adapter

import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.ReturnListClickListener
import com.example.blessingofshoes3.databinding.ItemProductBinding
import com.example.blessingofshoes3.databinding.ItemReturnBinding
import com.example.blessingofshoes3.databinding.ItemServicesBinding
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.db.Services
import com.example.blessingofshoes3.ui.product.EditProductActivity
import com.example.blessingofshoes3.ui.product.RestockActivity
import com.example.blessingofshoes3.ui.services.EditServicesActivity
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class ServicesAdapter (private val context: Context?, private var servicesItem: List<Services>) : RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder>() {

    inner class ServicesViewHolder (val binding: ItemServicesBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val binding = ItemServicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ServicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        val listServices = servicesItem[position]

        holder.binding.tvProfitTitle.text = "+"
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvServicePrice.text = numberFormat.format(listServices.serviceFinalPrice!!.toDouble()).toString()
        holder.binding.tvServiceProfit.text = numberFormat.format(listServices.serviceProfit!!.toDouble()).toString()
        holder.binding.tvServiceName.text = listServices!!.serviceName
        holder.binding.tvTimeAdded.text = listServices!!.timeAdded
        holder.binding.tvServiceEstimated.text = listServices!!.estimatedTime.toString()
        holder.binding.editIcon.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditServicesActivity::class.java)
            intent.putExtra("DATA_ID", listServices.idServices)
            intent.putExtra("DATA_NAME", listServices!!.serviceName)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = servicesItem.size

    fun setServicesData(postList: List<Services>)
    {

        this.servicesItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Services)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}