package com.example.blessingofshoes3.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemProductCashierBinding
import com.example.blessingofshoes3.db.DbDao
import com.example.blessingofshoes3.db.Product
import java.text.NumberFormat
import java.util.*

class CartAdapter (private val context: Context?, private var productItem: List<Product>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder (val binding: ItemProductCashierBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var mbDao: DbDao
    var total: Int = 0
    private val TAG = "CartAdapter"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {

        val binding = ItemProductCashierBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val listProduct = productItem[position]
        holder.binding.tvStockTitle.text = "Stock:"
        if (listProduct.stockProduct!!.toInt() > 0){
            holder.binding.tvProductStock.text = "Stock: " + listProduct.stockProduct
        } else{
            holder.binding.tvStockTitle.text = "Stock: "
            holder.binding.tvStockTitle.setTextColor(Color.RED)
            holder.binding.tvProductStock.setTextColor(Color.RED)
        }
        holder.binding.tvTotalPrice.text = "Rp0,00"
        holder.binding.txtQty.text = "0"

        holder.binding.btnAddToCart.text = "Add to Cart"
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvProductProfit.text = numberFormat.format(listProduct.profitProduct!!.toDouble()).toString()
        holder.binding.tvProductPrice.text = numberFormat.format(listProduct.priceProduct!!.toDouble()).toString()

        holder.binding.btnPlus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value+1

            if(new_value > listProduct.stockProduct!!.toInt()){
                Toast.makeText(holder.itemView.context, "Stock Empty", Toast.LENGTH_LONG).show()
            }else{
                holder.binding.txtQty.setText(new_value.toString())
                val subtotal = listProduct.priceProduct!!.toInt() * new_value
                holder.binding.tvTotalPrice.setText(numberFormat.format(subtotal.toDouble()).toString())

                total = total + listProduct.priceProduct.toString().toInt()
            }

        }

        holder.binding.btnMinus.setOnClickListener {
            val old_value = holder.binding.txtQty.text.toString().toInt()
            val new_value = old_value-1

            if (new_value>=0){
                holder.binding.txtQty.setText(new_value.toString())
                val subtotal = listProduct.priceProduct!!.toInt() * new_value
                holder.binding.tvTotalPrice.setText(numberFormat.format(subtotal.toDouble()).toString())

                total = total - listProduct.priceProduct.toString().toInt()
            }
        }
        holder.binding.btnAddToCart.setOnClickListener{
            var idItem = listProduct.idProduct.toString()
            var nameItem = listProduct.nameProduct.toString()
            var totalItem = holder.binding.txtQty.text.toString().toInt()
            var priceItem = listProduct.priceProduct.toString().toInt()
            var profitItem = listProduct.profitProduct!!.toInt() * totalItem
            var totalPayment = (totalItem * priceItem)
            if (holder.binding.txtQty.text == "0") {
                SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Qty Cannot Empty!")
                    .show()
            } else if (holder.binding.txtQty.text.toString().toInt() > 0){
                Log.d(TAG, "onBindView Holder: ${holder.binding.txtQty.text}")
                listener?.onAddClick(listProduct, position, totalItem)


            }
        }
        holder.binding.tvProfitTitle.text = "+"
        holder.binding.tvProductName.text = listProduct!!.nameProduct
        holder.binding.tvProductBrand.text = listProduct!!.brandProduct
        holder.binding.tvProductSize.text = listProduct!!.sizeProduct
        holder.binding.tvProductStock.text = listProduct!!.stockProduct.toString()
        Glide.with(holder.itemView.context)
            .load(listProduct!!.productPhoto)
            .fitCenter()
            .into(holder.binding.imageView)
    }

    var listener: CartClickListener? = null

    override fun getItemCount(): Int = productItem.size

    fun setProductData(postList: List<Product>)
    {

        this.productItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}

interface CartClickListener {
    //    fun onItemRemoveClick(position: Int)
    fun onAddClick(product: Product, position: Int, qty: Int)
}