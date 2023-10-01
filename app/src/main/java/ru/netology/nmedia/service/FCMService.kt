package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        println(Gson().toJson(message))
        message.data[action]?.let {
            try {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(Gson().fromJson(message.data[content], Like::class.java))
                    Action.NEW -> publishedNewPost(Gson().fromJson(message.data[content], NewPost::class.java))
                }
            } catch (e: IllegalArgumentException) {
                println("Enum constant not found")
            }
        }
    }

    private fun handleLike(like: Like) {
        println(like)
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, AppActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(R.string.notification_user_liked, like.userName, like.postAuthor)
            )
            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(Random.nextInt(10_100), notification)
        }

    }

    private fun publishedNewPost(post: NewPost) {
        println(post)
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, AppActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_new_post, post.userName))
            .setContentText(post.postContent)
            .setShowWhen(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(post.postContent))
            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(post.userId.toInt(), notification)
        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }
}

enum class Action {
    LIKE,
    NEW
}

data class Like(
    val userId: String,
    val userName: String,
    val postId: String,
    val postAuthor: String
)

data class NewPost(
    val userId: String,
    val userName: String,
    val postContent: String
)