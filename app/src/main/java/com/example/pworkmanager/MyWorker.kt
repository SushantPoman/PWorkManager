package com.example.pworkmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date

class MyWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    val channelId = "channelId"

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {

        createNotificationChannel()

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
        val date = simpleDateFormat.format(Date())

        var builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Worker $date")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify((0..100).random(), builder.build())


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "channelName"
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}