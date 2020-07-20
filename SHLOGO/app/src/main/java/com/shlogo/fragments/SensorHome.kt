package com.shlogo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.shlogo.R
import com.shlogo.classes.Device
import kotlin.properties.Delegates

class SensorHome : Fragment() {

    lateinit var device: Device

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val idText = requireView().findViewById<TextView>(R.id.idText)
        val typeText = requireView().findViewById<TextView>(R.id.typeText)

        val nameText = requireView().findViewById<TextView>(R.id.nameText)
        val roomText = requireView().findViewById<TextView>(R.id.roomText)
        val valueText = requireView().findViewById<TextView>(R.id.valueText)
        val image = requireView().findViewById<ImageView>(R.id.imageSensorHome)
        idText.text = device.clientId
        typeText.text = device.type
        nameText.text = device.name
        roomText.text = device.room
        valueText.text = device.currentValue.toString()
        when (device.typeid) {
            100 -> {
                image.setImageResource(R.drawable.temp)
            }
            101 -> {
                image.setImageResource(R.drawable.waterdroplet)
            }
            102 -> {
                image.setImageResource(R.drawable.uvsensor)
            }
            else -> {
                image.setImageResource(R.drawable.temp)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sensor_home, container, false)
    }

    companion object {
        fun newInstance(dev: Device) =
            SensorHome().apply {
                this.device = dev
            }
    }
}
