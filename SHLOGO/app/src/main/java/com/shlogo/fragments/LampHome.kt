package com.shlogo.fragments

import android.R.attr.angle
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.shlogo.R
import com.shlogo.classes.Device
import com.shlogo.classes.Type


class LampHome : Fragment() {

    lateinit var device: Device
    lateinit var type: Type

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val idText = requireView().findViewById<TextView>(R.id.idText)
        val typeText = requireView().findViewById<TextView>(R.id.typeText)

        val nameText = requireView().findViewById<TextView>(R.id.nameText)
        val roomText = requireView().findViewById<TextView>(R.id.roomText)
        val valueText = requireView().findViewById<TextView>(R.id.valueText)
        val image = requireView().findViewById<ImageView>(R.id.imageSensorHome)
        idText.text = device.clientId
        typeText.text = device.typeid.toString()
        nameText.text = device.name
        roomText.text = device.room
        valueText.text = device.currentValue.toString()
        if(device.currentValue == type.rangeMin){
            image.setImageResource(R.drawable.lamp)
        } else {
            image.setImageResource(R.drawable.lamp_on)
        }

        val input = requireView().findViewById<ConstraintLayout>(R.id.changeValueContainer)

        val seekBar = SeekBar(input.context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 0)
        seekBar.layoutParams = layoutParams
        seekBar.min = type.rangeMin
        seekBar.max = type.rangeMax
        seekBar.progress = device.currentValue
        input.addView(seekBar)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                value: Int,
                usr: Boolean
            ) {
                device.putRequest(device.clientId, value.toString(), input.context)
                valueText.text = value.toString()
                if(value == type.rangeMin){
                    image.setImageResource(R.drawable.lamp)
                } else {
                    image.setImageResource(R.drawable.lamp_on)
                }
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lamp_home, container, false)
    }

    companion object {
        fun newInstance(dev: Device, typ: Type) =
            LampHome().apply {
                this.device = dev
                this.type = typ
            }
    }
}