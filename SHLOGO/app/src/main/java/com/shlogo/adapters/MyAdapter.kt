package com.shlogo.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.shlogo.R
import com.shlogo.activities.SensorActivty
import com.shlogo.activities.LampActivity
import com.shlogo.classes.Device
import com.shlogo.classes.Networking
import com.shlogo.classes.Type


public class MyAdapter(c: Context, dev: List<Device>, typ: List<Type>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ct: Context = c
    private var devices: List<Device> = dev
    private var types: List<Type> = typ
    private var net = Networking()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(ct)
        lateinit var view: View
        when (viewType){
            1 -> {
                view = inflater.inflate(R.layout.sensor, parent, false)
                return SensorHolder(view)
            }
            2 -> {
                view = inflater.inflate(R.layout.fragment_bulb, parent, false)
                return LampHolder(view)
            }
        }
        return SensorHolder(view)
    }

    override fun getItemCount(): Int {
        if (devices.isNotEmpty()){
            return devices.size
        } else {
            return 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(devices[position].typeid == 100){
            1
        } else if(devices[position].typeid == 3){
            2
        } else {
            1
        }
        /*
        return if(devices[position].typeid == 1){
            1
        } else if(devices[position].typeid == 2){
            2
        } else if(devices[position].typeid == 3){
            3
        } else if(devices[position].typeid == 4){
            4
        } else if(devices[position].typeid == 100){
            5
        } else if(devices[position].typeid == 101){
            6
        } else if(devices[position].typeid == 102){
            7
        }else {
            1
        }
         */
    }
    public class LampHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.bulb_const_lay)
        val text = itemView.findViewById<TextView>(R.id.lamp_txt)
        val img = itemView.findViewById<ImageView>(R.id.lamp_png)
        val switch = itemView.findViewById<Switch>(R.id.lamp_switch)
    }
    public class SensorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.mainLayout)
        val text = itemView.findViewById<TextView>(R.id.sensorText)
        val sensor = itemView.findViewById<ImageView>(R.id.image)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            1 -> {
                val viewHolder: SensorHolder = holder as SensorHolder
                viewHolder.text.text = devices[position].name
                when (devices[position].typeid) {
                    100 -> {
                        viewHolder.sensor.setImageResource(R.drawable.temp)
                    }
                    101 -> {
                        viewHolder.sensor.setImageResource(R.drawable.waterdroplet)
                    }
                    102 -> {
                        viewHolder.sensor.setImageResource(R.drawable.uvsensor)
                    }
                    else -> {
                        viewHolder.sensor.setImageResource(R.drawable.temp)
                    }
                }
                var i = 0
                var pos = 0
                while (i < devices.size){
                    if(devices[position].clientId == devices[i].clientId){
                        pos = i
                    }
                    i++
                }
                viewHolder.mainLayout.setOnClickListener {
                    val intent = Intent(ct, SensorActivty::class.java)
                    intent.putExtra("ID", devices[pos].clientId)
                    ct.startActivity(intent)
                }
            }
            2 -> {
                val viewHolder: LampHolder = holder as LampHolder
                viewHolder.text.text = devices[position].name

                var j = 0
                loop@while(j < types.size){
                    if(types[j].typeId == devices[position].typeid){
                        break@loop
                    }
                    j++
                }
                if(devices[position].currentValue > types[j].rangeMin) {
                    viewHolder.img.setImageResource(R.drawable.lamp_on)
                    viewHolder.switch.isChecked = true
                } else {
                    viewHolder.img.setImageResource(R.drawable.lamp)
                    viewHolder.switch.isChecked = false
                }
                var i = 0
                var pos = 0
                while (i < devices.size){
                    if(devices[position].clientId == devices[i].clientId){
                        pos = i
                    }
                    i++
                }
                viewHolder.mainLayout.setOnClickListener {
                    val intent = Intent(ct, LampActivity::class.java)
                    intent.putExtra("ID", devices[pos].clientId)
                    ct.startActivity(intent)
                }
                viewHolder.switch.setOnCheckedChangeListener { buttonView, isChecked ->
                    var k = 0
                    loop@while(k < types.size){
                        if(types[k].typeId == devices[position].typeid){
                            break@loop
                        }
                        k++
                    }
                    if(isChecked) {
                        devices[position].currentValue = types[k].rangeMax
                        Log.d("Dev", types[k].rangeMax.toString())
                        Log.d("Nam", devices[position].clientId.toString())
                        viewHolder.img.setImageResource(R.drawable.lamp_on)
                    } else {
                        devices[position].currentValue = types[k].rangeMin
                        viewHolder.img.setImageResource(R.drawable.lamp)
                    }
                    val currVal =  devices[position].currentValue.toString()
                    net.postRequest(devices[position].clientId, currVal, viewHolder.itemView.context)
                }
            }
        }
    }
}
