package com.example.quozo

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AppService: Service() {




    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start(intent.getStringExtra("timerValue"))
            Actions.STOP.toString() -> stopSelf()
            Actions.UPDATE.toString() -> update(intent.getStringExtra("updatedValue"))
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(timeValue: String?){
        notification(timeValue)
    }

    private fun notification(timerValue: String?){
        val notification = NotificationCompat.Builder(this, "timer_channel")
            .setSmallIcon(R.drawable.film_icon)
            .setContentTitle(timerValue)
            .setContentText("Seconds left to Answer")

        startForeground(1,notification.build())
    }


    private fun update(timeValue: String?){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //notificationManager.notify(0, notification.build())

    }


    enum class Actions(){
        START,STOP,UPDATE
    }
}