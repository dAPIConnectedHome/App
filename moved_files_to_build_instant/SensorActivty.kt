package com.shlogo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.annotations.SerializedName
import com.shlogo.R
import com.shlogo.classes.Device
import com.shlogo.classes.Networking
import com.shlogo.fragments.History
import com.shlogo.fragments.SensorHome


class SensorActivty : AppCompatActivity() {

    private lateinit var homeFragment: SensorHome
    private lateinit var histFragment: History
    private lateinit var lastDevice: Device
    private var lastFragOpened: Int = 0
    private var inView: Boolean = true
    private val net = Networking()

    val volDevice = object : Networking.VolleyCallbackDevice {
        override fun onSuccess(result: Device?) {
            lastDevice = result!!
            val idText = findViewById<TextView>(R.id.idText)
            val typeText = findViewById<TextView>(R.id.typeText)
            val nameText = findViewById<TextView>(R.id.nameText)
            val roomText = findViewById<TextView>(R.id.roomText)
            val settings = findViewById<ImageButton>(R.id.settingsImageButton)
            settings.setOnClickListener {
                val intent = Intent(this@SensorActivty, DeviceSettingsActivity::class.java)
                intent.putExtra("ID",lastDevice.clientId)
                startActivity(intent)
            }

            idText.text = lastDevice.toString()
            typeText.text = lastDevice.typeid.toString()
            nameText.text = lastDevice.name
            roomText.text = lastDevice.room

            homeFragment = SensorHome.newInstance(lastDevice)
            histFragment = History.newInstance(lastDevice)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val id = intent.getStringExtra("ID")!!
        val thread = Runnable {
            while (inView) {
                net.getDevice(volDevice, id, this)
                Thread.sleep(5000)
            }
        }
        val myThread = Thread(thread)
        myThread.start()
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

