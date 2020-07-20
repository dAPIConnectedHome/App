package com.shlogo.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shlogo.R

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    var builder = NotificationCompat.Builder(ctx, "1")
        .setSmallIcon(R.drawable.logo)
        .setContentTitle("Warning")
        .setContentText("Temperature to high")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    private fun createNotificationChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = R.string.channel_name.toString()
            val descriptionText = R.string.channel_description.toString()
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE)
            val notificationManager: NotificationManager =
                nm as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun doWork(): Result {
        val appContext = applicationContext
        createNotificationChannel(appContext)
        with(NotificationManagerCompat.from(appContext)) {
            notify(1, builder.build())
        }
        return Result.success()
    }
}