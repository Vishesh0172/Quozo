package com.example.quozo

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


    enum class Actions(){
        START,STOP
    }
}