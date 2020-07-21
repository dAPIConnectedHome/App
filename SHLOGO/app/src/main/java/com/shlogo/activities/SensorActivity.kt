package com.shlogo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shlogo.R
import com.shlogo.classes.Device
import com.shlogo.classes.Networking
import com.shlogo.fragments.History
import com.shlogo.fragments.SensorHome

/**
 * Sensor Activity
 *
 * Activity which contains a image
 */
class SensorActivity : AppCompatActivity() {

    private lateinit var homeFragment: SensorHome
    private lateinit var histFragment: History
    private lateinit var lastDevice: Device
    private var lastFragOpened: Int = 0
    private var inView: Boolean = true
    private val net = Networking()
    /**
     * Successful Server Request of Device
     *
     * Start selected fragment
     */
    private val volDevice = object : Networking.VolleyCallbackDevice {
        override fun onSuccess(result: Device?) {
            lastDevice = result!!
            val settings = findViewById<ImageButton>(R.id.settingsImageButton)
            settings.setOnClickListener {
                val intent = Intent(this@SensorActivity, DeviceSettingsActivity::class.java)
                intent.putExtra("ID",lastDevice.clientId)
                startActivity(intent)
            }
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
                histFragment.onDestroy()
                lastFragOpened = 0
                net.getDevice(volDevice, lastDevice.clientId, this@SensorActivity)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                homeFragment.onDestroy()
                lastFragOpened = 1
                net.getDevice(volDevice, lastDevice.clientId, this@SensorActivity)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    /**
     * Change the fragment
     *
     * Change the fragments in the container
     *
     * @param fragment the new fragment to be displayed
     */
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
        //Update every 5 seconds
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

