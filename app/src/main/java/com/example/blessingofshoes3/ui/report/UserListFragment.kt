package com.example.blessingofshoes3.ui.report

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.AccountingAdapter
import com.example.blessingofshoes3.adapter.UsersAdapter
import com.example.blessingofshoes3.databinding.FragmentFinancialAccountingBinding
import com.example.blessingofshoes3.databinding.FragmentUserListBinding
import com.example.blessingofshoes3.db.Accounting
import com.example.blessingofshoes3.db.AppDb
import com.example.blessingofshoes3.db.Users
import com.example.blessingofshoes3.ui.ReportActivity
import com.example.blessingofshoes3.utils.Preferences
import com.example.blessingofshoes3.viewModel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class UserListFragment : Fragment() {

    private var binding: FragmentUserListBinding? = null
    private lateinit var usersAdapter: UsersAdapter
    private val viewModel by viewModels<AppViewModel>()
    lateinit var usersList: ArrayList<Users>
    lateinit var usersListData: List<Users>
    lateinit var sharedPref: Preferences
    private lateinit var usersListItem: RecyclerView
    private val appDatabase by lazy { AppDb.getDatabase(context!!).dbDao() }
    private lateinit var rvUsers: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeNotes()
        sharedPref = Preferences(requireContext())

        usersList = ArrayList()
        rvUsers()

    }

    private fun observeNotes() {
        progressBar = requireView().findViewById(R.id.progress_bar)
        val activity = getActivity() as ReportActivity
        activity.setClickable(false)
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllUsers().observe(viewLifecycleOwner) { itemList ->
                if (itemList != null) {
                    progressBar.visibility = View.GONE
                    usersListData = itemList
                    usersAdapter.setUsersData(itemList)
                    activity.setClickable(true)
                }
            }
        }
    }

    private fun rvUsers() {
        rvUsers = requireView().findViewById(R.id.rv_users)
        usersAdapter = UsersAdapter(context, usersList)
        rvUsers.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = usersAdapter
        }

    }
}