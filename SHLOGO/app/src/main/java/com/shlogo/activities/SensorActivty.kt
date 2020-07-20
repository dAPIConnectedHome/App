package com.shlogo.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.shlogo.R
import com.shlogo.classes.Device
import com.shlogo.fragments.History
import com.shlogo.fragments.SensorHome

class SensorActivty : AppCompatActivity() {

    private val url = "http://gitathome.dd-dns.de:61999/weatherforecast"
    private lateinit var homeFragment: SensorHome
    private lateinit var histFragment: History
    private lateinit var lastDevice: Device
    private var lastFragOpened: Int = 0
    private var inView: Boolean = true

    val volDevice = object : MainActivity.VolleyCallbackDevice {
        override fun onSuccess(result: Device?) {
            lastDevice = result!!
            val idText = findViewById<TextView>(R.id.idText)
            val typeText = findViewById<TextView>(R.id.typeText)
            val nameText = findViewById<TextView>(R.id.nameText)
            val roomText = findViewById<TextView>(R.id.roomText)
            idText.text = lastDevice.toString()
            typeText.text = lastDevice.typeid.toString()
            nameText.text = lastDevice.name
            roomText.text = lastDevice.room

            histFragment = History.newInstance()
            homeFragment = SensorHome.newInstance(lastDevice.clientId, lastDevice.typeid.toString(), lastDevice.name, lastDevice.room, lastDevice.currentValue)
            if(lastFragOpened == 0) {
                openFragment(homeFragment)
            } else{
                openFragment(histFragment)
            }
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                openFragment(homeFragment)
                lastFragOpened = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                openFragment(histFragment)
                lastFragOpened = 1
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private fun openFragment(fragment: Fragment) {
        if(inView){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.changeValueContainer, fragment)
            transaction.commit()
        }
    }

    private fun getDevice(callback: MainActivity.VolleyCallbackDevice, dev: String) {
        val url = getString(R.string.url) + "/$dev"
        val queue = Volley.newRequestQueue(this)
        val arrayRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val gson = GsonBuilder().create()
                val list = gson.fromJson(response.toString(), Device::class.java)
                callback.onSuccess(list);
            },
            Response.ErrorListener {
                fun onErrorResponse(error: VolleyError) {
                    Log.e("tag", "Error at sign in : " + error.message)
                }
                onErrorResponse(it)
            }
        )
        queue.add(arrayRequest)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val id = intent.getStringExtra("ID")



        /*val queue: RequestQueue = Volley.newRequestQueue(this@SensorActivty)
        val tempText = findViewById<TextView>(R.id.valueText)
        var packagesArray: List<TestClass>? = null
        val thread = Runnable {
            var i = 0
            while (inView) {
                i++
                getDevice(volDevice, id!!)
                tempText.post(Runnable {
                    val stringRequest = JsonArrayRequest(
                        Request.Method.GET, url, null,
                        Response.Listener { response ->
                            val gson = GsonBuilder().create()
                            packagesArray = gson.fromJson(response.toString(), Array<TestClass>::class.java).toList()
                        },
                        Response.ErrorListener {
                            fun onErrorResponse(error: VolleyError) {
                                Log.e("tag", "Error at sign in : " + error.message)
                            }
                            onErrorResponse(it)
                        }
                    )
                    queue.add(stringRequest)
                    if(!packagesArray.isNullOrEmpty()){
                        tempText.text = packagesArray!![0].temperatureC
                    }
                })
                Thread.sleep(500)
            }
        }
        val myThread = Thread(thread)
        myThread.start()*/
    }

    override fun onStart() {
        inView = true
        super.onStart()
    }
    override fun onStop() {
        inView = false
        super.onStop()
    }
}

data class TestClass(
    @SerializedName("date")
    var date: String,
    @SerializedName("temperatureC")
    var temperatureC: String,
    @SerializedName("temperatureF")
    var temperatureF: String,
    @SerializedName("summary")
    var summary: String
)