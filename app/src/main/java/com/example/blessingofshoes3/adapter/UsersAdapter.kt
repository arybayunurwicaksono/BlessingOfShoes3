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
import com.example.blessingofshoes3.databinding.ItemProfileBinding
import com.example.blessingofshoes3.db.Product
import com.example.blessingofshoes3.db.Users
import com.example.blessingofshoes3.ui.product.EditProductActivity
import com.example.blessingofshoes3.ui.product.RestockActivity
import com.example.blessingofshoes3.ui.report.DetailReportActivity
import com.example.blessingofshoes3.ui.report.UserListActivity
import com.example.blessingofshoes3.viewModel.AppViewModel
import java.text.NumberFormat
import java.util.*

class UsersAdapter (private val context: Context?, private var usersItem: List<Users>) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    inner class UsersViewHolder (val binding: ItemProfileBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var viewModel: AppViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val listUsers = usersItem[position]

        holder.binding.fullnameTitle.text = context!!.getString(R.string.fullname)
        holder.binding.fullnameValue.text = listUsers!!.fullname
        holder.binding.tvUsernameTitle.text = listUsers!!.username
        Glide.with(holder.itemView.context)
            .load(listUsers!!.photoUser)
            .fitCenter()
            .into(holder.binding.imageView)
        holder.binding.imageSwitch.setOnClickListener {
            if (holder.binding.imageView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.imageView.visibility = View.VISIBLE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.imageView.visibility = View.GONE
                holder.binding.imageSwitch.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
            }
        }
        holder.binding.reportIcon.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserListActivity::class.java)
            intent.putExtra("DATA_ID", listUsers!!.idUser)
            intent.putExtra("DATA_NAME", listUsers!!.username)
            holder.itemView.context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int = usersItem.size



    fun setUsersData(postList: List<Users>)
    {

        this.usersItem = postList
        notifyDataSetChanged()

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}