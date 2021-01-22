package com.example.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class MusicService: Service() {
    private  val channelID = "MusicService"
    companion object{
        private var running = false
        var currentSongIndex = -1
        lateinit var _mediaPlayer:MediaPlayer
        lateinit var _handler: Handler
        lateinit var _runnable: Runnable
        fun startService(context: Context, message: String, mediaPlayer: MediaPlayer,
                         handler: Handler, runnable: Runnable, songIndex: Int){
            val startIntent = Intent(context, MusicService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
            running = true
            _mediaPlayer=mediaPlayer
            _handler=handler
            _runnable=runnable
            currentSongIndex = songIndex
            _mediaPlayer.start()
        }
        fun stopService(context: Context){
            val stopIntent = Intent(context, MusicService::class.java)
            _mediaPlayer.stop()
            _mediaPlayer.release()
            _handler.removeCallbacks(_runnable)
            context.stopService(stopIntent)
            running = false

        }
        fun isRunning():Boolean = running
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        Log.d("musicService", input.toString())
        createNotificationChannel()
        val notificationIntent = Intent(this, Player::class.java).apply {
            putExtra("PlayIndex", currentSongIndex)
        }
        Log.d("extras", notificationIntent.extras.toString())
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.ic_music_service)
                .setContentTitle("Playing:")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(channelID, "Music Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}