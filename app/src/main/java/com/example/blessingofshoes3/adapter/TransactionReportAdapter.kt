package com.example.blessingofshoes3.adapter

import android.content.Context
import android.content.Intent
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

    inner class TransactionReportViewHolder (val binding: ItemTransactionReportBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionReportViewHolder {
        val binding = ItemTransactionReportBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TransactionReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionReportViewHolder, position: Int) {
        val listTransaction = transactionItem[position]

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