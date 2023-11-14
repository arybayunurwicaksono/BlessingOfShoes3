package com.example.blessingofshoes3.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemTransactionReportBinding
import com.example.blessingofshoes3.db.Transaction
import com.example.blessingofshoes3.ui.report.DetailReportActivity
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class TransactionReportAdapter (private val context: Context?, private var transactionItem: List<com.example.blessingofshoes3.db.Transaction>) : RecyclerView.Adapter<TransactionReportAdapter.TransactionReportViewHolder>() {

    inner class TransactionReportViewHolder(val binding: ItemTransactionReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Transaction) {
            if(data.isHover){
                binding.layoutItem.setBackgroundResource(R.drawable.bg_shape_green_stroke)
            }else{
                binding.layoutItem.setBackgroundResource(R.drawable.bg_is_unselected)
            }
            binding.root.setOnKeyListener { v, keyCode, event ->
                try {
                    when(keyCode){
                        KeyEvent.KEYCODE_DPAD_UP -> {

                            if(transactionItem.isNotEmpty() && this.adapterPosition ==0 && transactionItem[0].isHover){
                                transactionItem[0].isHover = false
                                transactionItem[this.adapterPosition+1].isHover = false
                            }else{
                                transactionItem[this.adapterPosition].isHover = true
                                transactionItem[this.adapterPosition+1].isHover = false
                            }
                            notifyItemChanged(this.adapterPosition)
                            notifyItemChanged(this.adapterPosition+1)
                        }


                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            transactionItem[this.adapterPosition].isHover = true
                            notifyItemChanged(this.adapterPosition)
                            if(this.adapterPosition != 0){
                                transactionItem[this.adapterPosition-1].isHover = false
                                notifyItemChanged(this.adapterPosition-1)

                            }
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            val intent = Intent(context, DetailReportActivity::class.java)
                            intent.putExtra("DATA_ID", data.idTransaction)
                            context?.startActivity(intent)
                        }
                    }
                    if(event.action == KeyEvent.ACTION_DOWN ) {


                    }
                }catch (e:Exception){ }

                false
            }


        }
    }
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionReportViewHolder {
        val binding = ItemTransactionReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)

          /*  binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = true*/

        return TransactionReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionReportViewHolder, position: Int) {
        val listTransaction = transactionItem[position]
        holder.bind(listTransaction)
        holder.binding.txtPaymentTitle.text = context!!.getString(R.string.total_payment)
        holder.binding.txtProfitTitle.text = context!!.getString(R.string.profit)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.txtItemTotalProfit.text = numberFormat.format(listTransaction.profitTransaction!!.toDouble()).toString()
        holder.binding.txtTotalBayar.text = numberFormat.format(listTransaction.totalTransaction!!.toDouble()).toString()
        holder.binding.btnView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailReportActivity::class.java)
            intent.putExtra("DATA_ID", listTransaction.idTransaction)
            holder.itemView.context.startActivity(intent)
        }
        holder.binding.transactionType.text = listTransaction.transactionType
        holder.binding.paymentType.text = listTransaction.typePayment
        holder.binding.txtTglTransaksi.text = listTransaction.transactionDate
    }


    override fun getItemCount(): Int = transactionItem.size



    fun setTransactionData(postList: List<com.example.blessingofshoes3.db.Transaction>)
    {

        this.transactionItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Transaction)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}