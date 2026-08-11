package com.example.cryptoandroidapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.cryptoandroidapp.R
import com.example.cryptoandroidapp.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Yeni FCM Token Üretildi: $token")
        // İleride kullanıcı token'ını Firestore veya Backend sunucusuna kaydetmek için burası kullanılır
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Mesajı Alındı. Gönderen: ${remoteMessage.from}")

        // 1. Notification Payload (Başlık ve Gövde)
        val title = remoteMessage.notification?.title 
            ?: remoteMessage.data["title"] 
            ?: "Kripto Fiyat Uyarısı"
        
        val message = remoteMessage.notification?.body 
            ?: remoteMessage.data["body"] 
            ?: "Takip ettiğiniz kripto para hareketlendi!"

        // 2. Data Payload (Özel Veriler: örn. coinId)
        val coinId = remoteMessage.data["coinId"]

        showNotification(title, message, coinId)
    }

    private fun showNotification(title: String, message: String, coinId: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 (API level 26) ve üzeri için Bildirim Kanalı zorunludur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kripto fiyat uyarıları ve piyasa gelişmeleri bildirimi"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirime tıklandığında MainActivity'e gidip hem intent extra hem de deep link URI aktarıyoruz
        val intent = Intent(
            Intent.ACTION_VIEW,
            android.net.Uri.parse("cryptoapp://detail/${coinId ?: "bitcoin"}"),
            this,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            coinId?.let { putExtra("coinId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    companion object {
        private const val TAG = "FCM_SERVICE"
        const val CHANNEL_ID = "crypto_notifications_channel"
        const val CHANNEL_NAME = "Kripto Piyasaları ve Bildirimler"
        private const val NOTIFICATION_ID = 1001
    }
}
