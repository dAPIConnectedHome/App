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
import com.shlogo.classes.Type
import com.shlogo.fragments.History
import com.shlogo.fragments.ActorHome

/**
 * Actor Activity
 *
 * Activity which contains a image and seekbar
 */
class ActorActivity : AppCompatActivity() {
    private lateinit var homeFragment: ActorHome
    private lateinit var histFragment: History
    private lateinit var lastDevice: Device
    private lateinit var listTypes: List<Type>
    private var lastFragOpened: Int = 0
    private var inView: Boolean = true
    private var findTypeCnt = 0
    private val net = Networking()
    private var type: Type = Type(0, "", "", 0,0)

    /**
     * Successful Server Request of Types
     *
     * Refresh every 2 min to display current data
     */
    private val volTypes = object : Networking.VolleyCallbackTypes {
        override fun onSuccess(result: List<Type>?) {
            listTypes = result!!
            val id = intent.getStringExtra("ID")
            //Update every 2min
            val thread = Runnable {
                while (inView) {
                    net.getDevice(volDevice, id!!, this@ActorActivity)
                    Thread.sleep(120000)
                }
            }
            val myThread = Thread(thread)
            myThread.start()
        }
    }
    /**
     * Successful Server Request of Device
     *
     * Start selected fragment
     */
    private val volDevice = object : Networking.VolleyCallbackDevice {
        override fun onSuccess(result: Device?) {
            lastDevice = result!!
            while (findTypeCnt < listTypes.size){
                if (listTypes[findTypeCnt].typeId == lastDevice.typeid){
                    type = listTypes[findTypeCnt]
                }
                findTypeCnt++
            }
            val settings = findViewById<ImageButton>(R.id.settingsImageButton)
            settings.setOnClickListener {
                val intent = Intent(this@ActorActivity, DeviceSettingsActivity::class.java)
                intent.putExtra("ID",lastDevice.clientId)
                startActivity(intent)
            }
            homeFragment = ActorHome.newInstance(lastDevice, type)
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
                net.getDevice(volDevice, lastDevice.clientId, this@ActorActivity)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                homeFragment.onDestroy()
                lastFragOpened = 1
                net.getDevice(volDevice, lastDevice.clientId, this@ActorActivity)
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
        net.getTypes(volTypes, this)
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