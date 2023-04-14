package com.example.blessingofshoes3.adapter

import android.content.Context
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
import com.example.blessingofshoes3.databinding.ItemReturnBinding
import com.example.blessingofshoes3.db.Cart
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class ReturnAdapter (private val context: Context?, private var returnItem: List<Cart>, val returnListClickListener: ReturnListClickListener) : RecyclerView.Adapter<ReturnAdapter.ReturnViewHolder>() {

    inner class ReturnViewHolder (val binding: ItemReturnBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel
    var total: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnViewHolder {
        val binding = ItemReturnBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReturnViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ReturnViewHolder, position: Int) {
        val currentItem = returnItem[position]
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


        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.layoutSlideReturn.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.layoutSlideReturn.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.layoutSlideReturn.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }
        holder.binding.btnPlus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value+1

            if(new_value > currentItem.totalItem!!.toInt()){
                SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText(context!!.getString(R.string.empty_item))
                    .show()
            }else{
                holder.binding.txtQty.setText(new_value.toString())
                val subtotal = currentItem.priceItem!!.toInt() * new_value
                holder.binding.tvTotalPrice.setText(numberFormat.format(subtotal.toDouble()).toString())

                total = total + currentItem.priceItem.toString().toInt()
            }

        }

        holder.binding.btnMinus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value-1

            if (new_value>=0){
                holder.binding.txtQty.setText(new_value.toString())
                val subtotal = currentItem.priceItem!!.toInt() * new_value
                holder.binding.tvTotalPrice.setText(numberFormat.format(subtotal.toDouble()).toString())

                total = total - currentItem.priceItem.toString().toInt()
            }
        }

        holder.binding.btnReturn.setOnClickListener{
            var totalItem = holder.binding.txtQty.text.toString().toInt()
            var totalRefund = totalItem * currentItem.priceItem!!
            var returnNote = holder.binding.edtReturnNote.text.toString().trim()
            returnListClickListener.onReturnListClickListener(it,currentItem.idItem!!,
                currentItem.nameItem.toString(),
                currentItem.priceItem!!, totalItem, totalRefund, returnNote, currentItem.itemProfit!!, currentItem.idTransaction
            )
        }

    }

    override fun getItemCount(): Int = returnItem.size

    fun setCartData(postList: List<Cart>)
    {

        this.returnItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Cart)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}