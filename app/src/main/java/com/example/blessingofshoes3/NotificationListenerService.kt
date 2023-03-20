package com.example.blessingofshoes3

import android.app.Notification
import android.content.Intent
import android.service.notification.StatusBarNotification

class NotificationListenerService : android.service.notification.NotificationListenerService() {

    companion object {
        const val NOTIFICATION_INTENT_ACTION = "com.example.notificationdemo.notification_listener_service"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_TEXT = "extra_text"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val notification = it.notification
            val title = notification.extras?.getString(Notification.EXTRA_TITLE) ?: ""
            val text = notification.extras?.getString(Notification.EXTRA_TEXT) ?: ""
            val intent = Intent(NOTIFICATION_INTENT_ACTION)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_TEXT, text)
            sendBroadcast(intent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}