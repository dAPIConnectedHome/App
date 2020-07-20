package com.shlogo.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import com.shlogo.R
import com.shlogo.classes.Device
import com.shlogo.classes.Type
import com.shlogo.fragments.History
import com.shlogo.fragments.LampHome

class LampActivity : AppCompatActivity() {
    private lateinit var homeFragment: LampHome
    private lateinit var histFragment: History
    private lateinit var lastDevice: Device
    private lateinit var listTypes: List<Type>
    private var lastFragOpened: Int = 0
    private var inView: Boolean = true
    private var findTypeCnt = 0


    var type: Type = Type(0, "", "", 0,0)

    private val volTypes = object : MainActivity.VolleyCallbackTypes {
        override fun onSuccess(result: List<Type>?) {
            listTypes = result!!
            val id = intent.getStringExtra("ID")
            val thread = Runnable {
                while (inView) {
                    getDevice(volDevice, id!!)
                    Thread.sleep(120000)
                }
            }
            val myThread = Thread(thread)
            myThread.start()
        }
    }

    val volDevice = object : MainActivity.VolleyCallbackDevice {
        override fun onSuccess(result: Device?) {
            lastDevice = result!!
            while (findTypeCnt < listTypes.size){
                if (listTypes[findTypeCnt].typeId == lastDevice.typeid){
                    type = listTypes[findTypeCnt]
                }
                findTypeCnt++
            }
            val idText = findViewById<TextView>(R.id.idText)
            val typeText = findViewById<TextView>(R.id.typeText)
            val nameText = findViewById<TextView>(R.id.nameText)
            val roomText = findViewById<TextView>(R.id.roomText)
            idText.text = lastDevice.toString()
            typeText.text = lastDevice.typeid.toString()
            nameText.text = lastDevice.name
            roomText.text = lastDevice.room
            homeFragment = LampHome.newInstance(lastDevice, type)
            histFragment = History.newInstance()
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

        type.getTypes(getString(R.string.urlTypes), volTypes, this)
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