package com.tmvlg.factorcapgame.services

import android.app.NotificationChannel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tmvlg.factorcapgame.R
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider

import android.app.PendingIntent

import android.content.Intent
import android.graphics.Color
import android.os.Build

import com.tmvlg.factorcapgame.ui.MainActivity


class FCMService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("1", "token = $s")

        val preferenceProvider = PreferenceProvider(this)
        preferenceProvider.setRegistrationToken(s)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val title = remoteMessage.notification!!.title
        val message = remoteMessage.notification!!.body
        Log.i("1", "invite: title : $title")
        Log.i("1", "invite: message : $message")
        Log.i("1", "invite: imageUrl : ${data["image"]}")
        Log.i("1", "invite: action : ${data["action"]}")
//        val notification = NotificationCompat.Builder(this)
//            .setTicker("Notification")
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(R.drawable.circle_background)
//            .build()
//        notification.flags = Notification.FLAG_AUTO_CANCEL
//        val nManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        nManager.notify(0, notification)

        val copyData = HashMap(data)

        sendNotification(message!!, title!!, copyData)
    }


    private fun sendNotification(
        messageBody: String,
        messageTitle: String,
        data: HashMap<String, String>
    ) {

        val intent = Intent(this, MainActivity::class.java)
        if (!data.isEmpty()) {
            val lobbyId: String = data.get("lobbyId") ?: ""
            intent.putExtra("lobbyIdForActivity", lobbyId)
        }



        // after from here are the generally same. tricks above
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )



        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_INVITATIONS)
                .setSmallIcon(R.drawable.circle_background)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        val nManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Invitations"
            val descriptionText = "Notification to accept invite to lobby"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_INVITATIONS, name, importance)
            mChannel.description = descriptionText
            mChannel.vibrationPattern = VIBRATION_PATTERN
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.lightColor = LIGHT_COLOR_ARGB
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            nManager.createNotificationChannel(mChannel)
        }

        nManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val NOTIFICATION_ID = 4
        private const val CHANNEL_INVITATIONS = "invitations"
        private val VIBRATION_PATTERN = longArrayOf(0, 100, 50, 100)
        private const val LIGHT_COLOR_ARGB = Color.GREEN
    }
}



