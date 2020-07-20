package com.shlogo.notif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shlogo.services.MyService

class StartReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_ON) {
            val i = Intent(context, MyService::class.java)
            context.startService(i)
        }
    }
}
