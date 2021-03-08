package net.xblacky.animexstream.utils.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.fragment_video_player.*
import net.xblacky.animexstream.R
import net.xblacky.animexstream.ui.main.player.VideoPlayerActivity

class ForegroundNotificationService: Service() {

    companion object {
        const val TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE"
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
    }

    override fun onBind(intent: Intent?): IBinder? {
//        TODO("Not yet implemented")
        throw UnsupportedOperationException("Not Implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
         val action = it.action
         action?.let {
             when (it) {
                 ACTION_START_FOREGROUND_SERVICE -> startForegroundNotificationService()
                 ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundNotificationService()
                 ACTION_PLAY -> Toast.makeText(this, "PLAYING", Toast.LENGTH_SHORT).show()
                 ACTION_PAUSE -> Toast.makeText(this, "PAUSED", Toast.LENGTH_SHORT).show()
             }
         }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundNotificationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("NOTIFICATION_SERVICE", "NOTIFICATION_BACKGROUND_SERVICE")
        } else {
            val intent = Intent()
            val pi = PendingIntent.getActivity(this, 0, intent, 0)

            // create notification builder
            val builder = NotificationCompat.Builder(this)

            val bigTextStyle = NotificationCompat.BigTextStyle()
            bigTextStyle.setBigContentTitle("Playing Media")
            bigTextStyle.bigText("RUNNING SERVVICE")
            builder.apply {
                setStyle(bigTextStyle)
                setWhen(System.currentTimeMillis())
                setSmallIcon(R.drawable.splash_drawable)
                setPriority(NotificationCompat.PRIORITY_MAX)
                setFullScreenIntent(pi, true)
            }

            val playIntent = Intent(this, ForegroundNotificationService::class.java)
            playIntent.setAction(ACTION_PLAY)
            val piPlay = PendingIntent.getService(this, 0, playIntent, 0)
            val playAction = NotificationCompat.Action(R.drawable.ic_media_play, "PLAY" ,piPlay)
            builder.addAction(playAction)

            val pauseIntent = Intent(this, ForegroundNotificationService::class.java)
            pauseIntent.setAction(ACTION_PAUSE)
            val piPause = PendingIntent.getService(this, 0, pauseIntent, 0)
            val pauseAction = NotificationCompat.Action(R.drawable.ic_media_pause, "PAUSE" ,piPause)
            builder.addAction(pauseAction)

            val notification = builder.build()
            startForeground(1, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String) {
        val resultIntent = Intent(this, VideoPlayerActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.apply {
            addNextIntentWithParentStack(resultIntent)
        }
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        chan.apply {
            lightColor = Color.CYAN
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assert(manager != null)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification: Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.splash_drawable)
                .setContentTitle("APP IS RUNNING IN THE BACKGROUND")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent)
                .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notificationBuilder.build())
        startForeground(1, notification)
    }

    private fun stopForegroundNotificationService() {
        stopForeground(true)
        stopSelf()
    }

}