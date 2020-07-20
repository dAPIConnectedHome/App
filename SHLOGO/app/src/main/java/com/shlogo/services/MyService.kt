package com.shlogo.services

import android.app.Service

import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.shlogo.fragments.TestClass
import com.shlogo.notif.NotificationWorker

class MyService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private val workManager = WorkManager.getInstance(application)

    val url = "http://gitathome.dd-dns.de:61999/weatherforecast"
    var highestTemp = "0"

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        val ctx = applicationContext
        var packagesArray: List<TestClass>? = null
        val queue: RequestQueue = Volley.newRequestQueue(ctx)
        override fun handleMessage(msg: Message) {
            try {
                while(true) {
                    getTemp()
                    if(highestTemp > "49"){
                        workManager.enqueue(OneTimeWorkRequest.from(NotificationWorker::class.java))
                        Thread.sleep(5000)
                        highestTemp = "0"
                    }
                    Thread.sleep(2000)
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            stopSelf(msg.arg1)
        }
        fun getTemp() {
            val stringRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    val gson = GsonBuilder().create()
                    packagesArray =
                        gson.fromJson(response.toString(), Array<TestClass>::class.java).toList()
                },
                Response.ErrorListener {
                    fun onErrorResponse(error: VolleyError) {
                        Log.e("tag", "Error at sign in : " + error.message)
                    }
                    onErrorResponse(it)
                }
            )
            queue.add(stringRequest)
            if (!packagesArray.isNullOrEmpty()) {
                if (highestTemp < packagesArray!![0].temperatureC) {
                    highestTemp = packagesArray!![0].temperatureC
                }
            }
        }
    }

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        onTaskRemoved(intent);
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }
}