package com.shlogo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.shlogo.R
import kotlin.properties.Delegates

class SensorHome : Fragment() {

    lateinit var id: String
    lateinit var type: String
    lateinit var name: String
    lateinit var room: String
    var value by Delegates.notNull<Int>()

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
        idText.text = id
        typeText.text = type
        nameText.text = name
        roomText.text = room
        valueText.text = value.toString()
        image.setImageResource(R.drawable.temp).toString()
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
        fun newInstance(id: String, type: String, name:String, room: String, value: Int) =
            SensorHome().apply {
                this.id = id
                this.type = type
                this.name = name
                this.room = room
                this.value = value
            }
    }
}
