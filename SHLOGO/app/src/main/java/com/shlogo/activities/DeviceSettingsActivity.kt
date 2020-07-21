package com.shlogo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.shlogo.R
import com.shlogo.adapters.SettingsAdapter
import com.shlogo.classes.Device
import com.shlogo.classes.Networking

/**
 * Device settings Activity
 *
 * Let user change the name, room or/and group of a existing device
 */
class DeviceSettingsActivity : AppCompatActivity() {

    lateinit var device: Device
    /**
     * Successful Server Request of Put Request
     *
     * Go back to the overview activity
     */
    private val changesApplied = object : Networking.VolleyCallbackPut {
        override fun onSuccess() {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
    /**
     * Successful Server Request of Getting Device
     *
     * Load the current information of the device in the EditTextViews
     */
    private val volDevice = object : Networking.VolleyCallbackDevice {
        override fun onSuccess(result: Device?) {
            device = result!!
            val recyclerView = findViewById<RecyclerView>(R.id.settingsListView)
            val settingsAdapter = SettingsAdapter(
                this@DeviceSettingsActivity,
                device
            )
            recyclerView.adapter = settingsAdapter
            recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        }
    }
    /**
     * Make Put request
     *
     * Get the current values in the TextInputEditText fields and out them on the server
     *
     * @param view: current view
     */
    fun applyChanges(view: View){
        val v = findViewById<RecyclerView>(R.id.settingsListView)
        val name = v.findViewWithTag<TextInputEditText>("nameTag")
        val group = v.findViewWithTag<TextInputEditText>("groupTag")
        val room = v.findViewWithTag<TextInputEditText>("roomTag")
        val net = Networking()
        net.putRequest(changesApplied, device.clientId, name.text.toString(), room.text.toString(), group.text.toString(), this)
    }
    /**
     * Button remove device
     *
     * Set the room value to -1 which indicates that the device is no longer in the app
     */
    fun removeDevice(view: View){
        val net = Networking()
        val v = findViewById<RecyclerView>(R.id.settingsListView)
        val name = v.findViewWithTag<TextInputEditText>("nameTag")
        net.putRequest(changesApplied, device.clientId, name.text.toString(), "-1", "-1", this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_settings)
        val id = intent.getStringExtra("ID")!!
        val network = Networking()
        network.getDevice(volDevice, id, this)
    }
}