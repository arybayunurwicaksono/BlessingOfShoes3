package com.example.blessingofshoes3.ui.transaction

import android.app.Notification
import android.app.NotificationManager
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.blessingofshoes3.NotificationListenerService
import com.example.blessingofshoes3.R
import com.example.blessingofshoes3.adapter.NotificationAdapter
import com.example.blessingofshoes3.model.NotificationData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var notifications: ArrayList<NotificationData>

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == NotificationListenerService.NOTIFICATION_INTENT_ACTION) {
                    val title = it.getStringExtra(NotificationListenerService.EXTRA_TITLE) ?: ""
                    val text = it.getStringExtra(NotificationListenerService.EXTRA_TEXT) ?: ""
                    val notification = NotificationData(title, text)
                    notifications.add(notification)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Inisialisasi RecyclerView dan adapter
        recyclerView = findViewById(R.id.notificationRecyclerView)
        notifications = ArrayList()
        adapter = NotificationAdapter(this, notifications)
        recyclerView.adapter = adapter

        // Mendaftarkan BroadcastReceiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationListenerService.NOTIFICATION_INTENT_ACTION)
        registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}