package com.example.blessingofshoes3.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.ReturnListClickListener
import com.example.blessingofshoes3.ServicesClickListener
import com.example.blessingofshoes3.databinding.ItemServicesBinding
import com.example.blessingofshoes3.databinding.ItemServicesCashierBinding
import com.example.blessingofshoes3.db.Services
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class WashingServicesAdapter (private val context: Context?, private var servicesItem: List<Services>, val servicesClickListener: ServicesClickListener) : RecyclerView.Adapter<WashingServicesAdapter.ServicesViewHolder>() {

    inner class ServicesViewHolder (val binding: ItemServicesCashierBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel
    var total: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val binding = ItemServicesCashierBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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
        holder.binding.tvServiceTotalPrice.setText("Rp.0,00")
        holder.binding.btnPlus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value+1

            holder.binding.txtQty.setText(new_value.toString())
            val subtotal = listServices.serviceFinalPrice!!.toInt() * new_value
            holder.binding.tvServiceTotalPrice.setText(numberFormat.format(subtotal.toDouble()).toString())

            total += listServices.serviceFinalPrice!!.toString().toInt()
            if (holder.binding.txtQty.text.toString().toInt()>0) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.customerName.visibility = VISIBLE
            }

        }

        holder.binding.btnMinus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value-1

            if (new_value>=0){
                holder.binding.txtQty.setText(new_value.toString())
                val subtotal = listServices.serviceFinalPrice!!.toInt() * new_value
                holder.binding.tvServiceTotalPrice.setText(numberFormat.format(subtotal.toDouble()).toString())

                total -= listServices.serviceFinalPrice!!.toString().toInt()
            }
            if (holder.binding.txtQty.text.toString().toInt()==0) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.customerName.visibility = GONE
            }
        }
        holder.binding.btnAdd.setOnClickListener{
            var totalItem = holder.binding.txtQty.text.toString().toInt()
            var totalProfit = totalItem * listServices.serviceProfit!!
            var totalPrice = totalItem * listServices.serviceFinalPrice!!
            var customer = holder.binding.edtReadCustomer.text.toString()
            if (holder.binding.edtReadCustomer.text.toString() == "") {
                SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText(context!!.getString(R.string.incorrect_customer_name))
                    .show()
            } else {
                servicesClickListener.onServicesClickListener(it,listServices.idServices!!,
                    listServices.serviceName.toString(),
                    listServices.serviceFinalPrice!!,
                    totalItem,
                    listServices.serviceProfit!!,
                    totalProfit,
                    totalPrice,
                    customer
                )
            }
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