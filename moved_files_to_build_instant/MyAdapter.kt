package com.shlogo

import android.bluetooth.BluetoothClass
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.shlogo.activities.MainActivity
import com.shlogo.activities.SensorActivty
import com.shlogo.classes.Device

public class MyAdapter(c: Context, i: MutableList<String>, t: MutableList<String>, d: MutableList<String>, r: MutableList<String>, g: MutableList<String>, dev: MutableList<Device>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ct: Context = c
    var id: MutableList<String> = i
    var type: MutableList<String> = t
    var device: MutableList<String> = d
    var room: MutableList<String> = r
    //lateinit var roomLast: String
    var group: MutableList<String> = g
    var devices: MutableList<Device> = dev

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(ct)
        lateinit var view: View
        lateinit var view1: View
        when (viewType){
            1 -> {
                view = inflater.inflate(R.layout.room_name, parent, false)
                return MyViewHolder0(view)
            }
            2 -> {
                view = inflater.inflate(R.layout.sensor, parent, false)
                return SensorHolder(view)
            }
            3 -> {
                view = inflater.inflate(R.layout.fragment_bulb, parent, false)
                return MyViewHolder(view)
            }
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (id.isNotEmpty()){
            return id.size
        } else {
            return 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(id[position] == "-1"){
            return 1
        } else {
            if(type[position] == "sensor"){
                return 2
            } else{
                return 3
            }
        }
    }
    public class MyViewHolder0(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val room = itemView.findViewById<TextView>(R.id.roomName)
    }
    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.lamp_txt)
        val img = itemView.findViewById<ImageView>(R.id.lamp_png)
    }
    public class SensorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.mainLayout)
        val text = itemView.findViewById<TextView>(R.id.sensorText)
        val sensor = itemView.findViewById<ImageView>(R.id.image)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            1 -> {
                val viewHolder0: MyViewHolder0 = holder as MyViewHolder0
                viewHolder0.room.text = room[position]
            }
            2 -> {
                val viewHolder: SensorHolder = holder as SensorHolder
                viewHolder.text.text = device[position]
                viewHolder.sensor.setImageResource(R.drawable.temp)
                var i = 0
                var pos = 0
                while (i < devices.size){
                    if(id[position] == devices[i].id){
                        pos = i
                    }
                    i++
                }
                viewHolder.mainLayout.setOnClickListener(View.OnClickListener {
                    val intent = Intent(ct, SensorActivty::class.java)
                    intent.putExtra("ID", devices[pos].id)
                    intent.putExtra("NAME", devices[pos].name)
                    intent.putExtra("TYPE", devices[pos].type)
                    intent.putExtra("ROOM", devices[pos].room.name)
                    ct.startActivity(intent)
                })


                //viewHolder.mainLayout.setOnClickListener() {
                //
                //}
            }
            3 -> {
                val viewHolder: MyViewHolder = holder as MyViewHolder
                viewHolder.text.text = device[position]
                viewHolder.img.setImageResource(R.drawable.lamp)
            }
        }
    }
}
