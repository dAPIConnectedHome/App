package com.shlogo.fragments

import android.graphics.PorterDuff
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
import com.shlogo.classes.Networking
import com.shlogo.classes.Type

/**
 * Actor Home Fragment
 *
 * The Fragment which shows the main information of the selected actor.
 */
@Suppress("DEPRECATION")
class ActorHome : Fragment() {

    lateinit var device: Device
    lateinit var type: Type
    val net = Networking()
    private var valu = 0
    /**
     * View creation
     *
     * Load the values of the actor in the views and handle the seekbar functionality.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val nameText = requireView().findViewById<TextView>(R.id.nameText)
        val roomText = requireView().findViewById<TextView>(R.id.roomText)
        val valueText = requireView().findViewById<TextView>(R.id.valueText)
        val image = requireView().findViewById<ImageView>(R.id.imageSensorHome)
        nameText.text = device.name
        roomText.text = device.room
        valueText.text = device.currentValue.toString()
        valu = device.currentValue
        if(device.currentValue == type.rangeMin){
            image.setImageResource(R.drawable.lamp)
        } else {
            if(valu < type.rangeMax){
                image.setImageResource(R.drawable.lamp_dim)
            } else {
                image.setImageResource(R.drawable.lamp_on)
            }
        }
        if(type.typeId == 1){
            image.setImageResource(R.drawable.power)
        }
        val input = requireView().findViewById<ConstraintLayout>(R.id.changeValueContainer)

        val seekBar = SeekBar(input.context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 0)
        seekBar.layoutParams = layoutParams
        seekBar.progressDrawable.setColorFilter(0xFF000000.toInt(), PorterDuff.Mode.MULTIPLY);
        seekBar.thumb.setColorFilter(0xFF00FF00.toInt(), PorterDuff.Mode.MULTIPLY);
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
                valu = value
                valueText.text = valu.toString()
                if(valu == type.rangeMin){
                    image.setImageResource(R.drawable.lamp)
                } else {
                    if(valu < type.rangeMax){
                        image.setImageResource(R.drawable.lamp_dim)
                    } else {
                        image.setImageResource(R.drawable.lamp_on)
                    }
                }
                if(type.typeId == 1){
                    image.setImageResource(R.drawable.power)
                }
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                net.postRequest(device.clientId, valu.toString(), input.context)
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_actor_home, container, false)
    }

    companion object {
        fun newInstance(dev: Device, typ: Type) =
            ActorHome().apply {
                this.device = dev
                this.type = typ
            }
    }
}