package com.example.blessingofshoes3.adapter

import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.databinding.ItemProductBinding
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.ui.product.EditProductActivity
import com.example.blessingofshoes3.ui.product.RestockActivity
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class ProductAdapter (private val context: Context?, private var productItem: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder (val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val listProduct = productItem[position]

        holder.binding.tvProfitTitle.text = "+"
        holder.binding.tvStockTitle.text = context!!.getString(R.string.stock_title)
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.binding.tvProductProfit.text = numberFormat.format(listProduct.profitProduct!!.toDouble()).toString()
        holder.binding.tvProductPrice.text = numberFormat.format(listProduct.priceProduct!!.toDouble()).toString()
        holder.binding.tvProductName.text = listProduct!!.nameProduct
        holder.binding.tvProductBrand.text = listProduct!!.brandProduct
        holder.binding.tvProductSize.text = listProduct!!.sizeProduct
        holder.binding.tvProductStock.text = listProduct!!.stockProduct.toString()
        holder.binding.tvTimeAdded.text = listProduct!!.timeAdded
        Glide.with(holder.itemView.context)
            .load(listProduct!!.productPhoto)
            .fitCenter()
            .into(holder.binding.imageView)
        holder.binding.btnInfo.setOnClickListener{
            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(context!!.getString(R.string.information_title)+" "+ listProduct.nameProduct.toString())
                .setContentText(context!!.getString(R.string.stock_available)+listProduct.stockProduct.toString()+" "+context!!
                    .getString(R.string.and)+" "+numberFormat.format(listProduct.totalPurchases!!.toDouble()).toString()+ context!!.getString(R.string.worth))
                .setCustomImage(R.drawable.ic_baseline_info_24)
                .setConfirmText(context!!.getString(R.string.close))
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                }
                .show()
        }
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.imageView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.cardViewStock.visibility = View.VISIBLE
                holder.binding.btnEdit.visibility = View.VISIBLE
                holder.binding.imageView.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.cardViewStock.visibility = View.GONE
                holder.binding.btnEdit.visibility = View.GONE
                holder.binding.imageView.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }

        holder.binding.btnEdit.setOnClickListener {

            SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(listProduct!!.nameProduct.toString())
                .setContentText(context.getString(R.string.please_select_edit_option))
                .setConfirmText(context.getString(R.string.detail))
                .setCustomImage(R.drawable.ic_baseline_info_24)
                .setCancelText(context.getString(R.string.restock))
                .setCancelButtonBackgroundColor(R.color.light_green)
                .setConfirmClickListener { sDialog ->
                    val intent = Intent(holder.itemView.context, EditProductActivity::class.java)
                    intent.putExtra("DATA_ID", listProduct.idProduct)
                    intent.putExtra("DATA_NAME", listProduct!!.nameProduct)
                    holder.itemView.context.startActivity(intent)
                }
                .setCancelClickListener { pDialog ->
                    val intent = Intent(holder.itemView.context, RestockActivity::class.java)
                    intent.putExtra("DATA_ID", listProduct.idProduct)
                    intent.putExtra("DATA_NAME", listProduct!!.nameProduct)
                    holder.itemView.context.startActivity(intent)
                }
                .show()
        }

    }


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