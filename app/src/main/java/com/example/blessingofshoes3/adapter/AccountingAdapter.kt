package com.example.blessingofshoes3.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemFinancialAccountingBinding
import com.example.blessingofshoes3.db.Accounting
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class AccountingAdapter (private val context: Context?, private var accountingItem: List<Accounting>) : RecyclerView.Adapter<AccountingAdapter.AccountingViewHolder>() {

    inner class AccountingViewHolder (val binding: ItemFinancialAccountingBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountingViewHolder {
        val binding = ItemFinancialAccountingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AccountingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountingViewHolder, position: Int) {
        val listAccounting = accountingItem[position]

        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvIdFinancialAccounting.text = context!!.getString(R.string.accounting_id_title) + listAccounting.idAccounting.toString()
        holder.binding.dateFinancialAccounting.text = listAccounting.dateAccounting!!
        holder.binding.AccountBalanceValue.text = numberFormat.format(listAccounting.initDigital!!.toDouble()).toString()
        holder.binding.CashBalanceValue.text = numberFormat.format(listAccounting.initCash!!.toDouble()).toString()
        holder.binding.TotalStockValue.text = listAccounting.initCash!!.toString()
        holder.binding.StockWorthValue.text = numberFormat.format(listAccounting.initStockWorth!!.toDouble()).toString()
        holder.binding.username.text = listAccounting.username!!
        holder.binding.status.text = listAccounting.status!!
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.accountingReport.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.accountingReport.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.accountingReport.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }
        holder.binding.capitalInvestmentValue.text = numberFormat.format(listAccounting.capitalInvest!!.toDouble()).toString()
        holder.binding.transactionTotal.text = numberFormat.format(listAccounting.incomeTransaction!!.toDouble()).toString()
        holder.binding.transactionItemTotal.text = listAccounting.transactionItem!!.toString() + context.getString(R.string.product_value)
        holder.binding.restockTotal.text = numberFormat.format(listAccounting.restockPurchases!!.toDouble()).toString()
        holder.binding.restockItemTotal.text = listAccounting.restockItem!!.toString() + context.getString(R.string.product_value)
        holder.binding.returnTotal.text = numberFormat.format(listAccounting.returnTotal!!.toDouble()).toString()
        holder.binding.returnItemTotal.text = listAccounting.returnItem!!.toString() + context.getString(R.string.product_value)
        holder.binding.balanceInValue.text = numberFormat.format(listAccounting.balanceIn!!.toDouble()).toString()
        holder.binding.balanceOutValue.text = numberFormat.format(listAccounting.balanceOut!!.toDouble()).toString()
        holder.binding.profitValue.text = numberFormat.format(listAccounting.profitEarned!!.toDouble()).toString()

        holder.binding.AccountBalanceValueFinal.text = numberFormat.format(listAccounting.finalDigital!!.toDouble()).toString()
        holder.binding.CashBalanceValueFinal.text = numberFormat.format(listAccounting.finalCash!!.toDouble()).toString()
        holder.binding.TotalStockValueFinal.text = listAccounting.finalStock.toString()  + context.getString(R.string.product_value)
        holder.binding.StockWorthValueFinal.text = numberFormat.format(listAccounting.finalWorth!!.toDouble()).toString()
    }

    var listener: AccountingClickListener? = null

    override fun getItemCount(): Int = accountingItem.size

    fun setProductData(postList: List<Accounting>)
    {

        this.accountingItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Accounting)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface AccountingClickListener {
        //    fun onItemRemoveClick(position: Int)
        fun onAddClick(accounting: Accounting, position: Int)
    }
}