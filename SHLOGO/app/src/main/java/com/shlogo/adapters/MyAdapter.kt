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
import com.shlogo.activities.SensorActivity
import com.shlogo.activities.ActorActivity
import com.shlogo.classes.Device
import com.shlogo.classes.Networking
import com.shlogo.classes.Type

/**
 * Adapter of one Room
 *
 * Create the different holders of the nested recycler view
 *
 * @param ct the previous context
 * @param devices list of all devices in this room
 * @param types all types of the server request
 */
class MyAdapter(
    private var ct: Context,
    private var devices: List<Device>,
    private var types: List<Type>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var net = Networking()

    /**
     * Actor holder
     *
     * holder with a image and a switch
     *
     * @param itemView view
     */
    class ActorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.bulb_const_lay)
        val text = itemView.findViewById<TextView>(R.id.lamp_txt)
        val img = itemView.findViewById<ImageView>(R.id.lamp_png)
        val switch = itemView.findViewById<Switch>(R.id.lamp_switch)
    }
    /**
     * Sensor holder
     *
     * holder with a image
     *
     * @param itemView view
     */
    class SensorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.mainLayout)
        val text = itemView.findViewById<TextView>(R.id.sensorText)
        val sensor = itemView.findViewById<ImageView>(R.id.image)
    }

    /**
     * Create View holder
     *
     * returns the holder depending on the viewType
     *
     * @param parent view group of the parent
     * @param viewType current viewType to choose from
     *
     * @return holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(ct)
        lateinit var view: View
        when (viewType){
            1 -> {
                view = inflater.inflate(R.layout.sensor, parent, false)
                return SensorHolder(view)
            }
            2 -> {
                view = inflater.inflate(R.layout.actor, parent, false)
                return ActorHolder(view)
            }
        }
        return SensorHolder(view)
    }
    /**
     * Return rooms amount of devices
     */
    override fun getItemCount(): Int {
        if (devices.isNotEmpty()){
            return devices.size
        } else {
            return 0
        }
    }
    /**
     * Returns item view type
     *
     * find the type direction and returns the view type which fits the direction
     *
     * @param position current position of the holder
     */
    override fun getItemViewType(position: Int): Int {
        var i = 0
        loop@while (i < types.size){
            if (devices[position].typeid == types[i].typeId){
                break@loop
            }
            i++
        }
        return when (types[i].direction) {
            "R" -> {
                2
            }
            "S" -> {
                1
            }
            "T" -> {
                2
            }
            else -> {
                1
            }
        }
    }

    /**
     * Functionality of OnClick and the image selection
     *
     * Decide the image from the view type and the holder onClick information. Already give the
     * information of the id of the device and which activity to open then. Switch in the overview
     * sets the range min and range max values of the selected type. Code need cleanup.
     * Groups in the overview are created the same way with an offset in the position.
     *
     * @param holder the holder information
     * @param position the holder position
     */
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
                    val intent = Intent(ct, SensorActivity::class.java)
                    intent.putExtra("ID", devices[pos].clientId)
                    ct.startActivity(intent)
                }
            }
            2 -> {
                val viewHolder: ActorHolder = holder as ActorHolder
                viewHolder.text.text = devices[position].name

                var j = 0
                loop@while(j < types.size){
                    if(types[j].typeId == devices[position].typeid){
                        break@loop
                    }
                    j++
                }
                if(devices[position].currentValue > types[j].rangeMin) {
                    if(devices[position].currentValue < types[j].rangeMax){
                        viewHolder.img.setImageResource(R.drawable.lamp_dim)
                    } else {
                        viewHolder.img.setImageResource(R.drawable.lamp_on)
                    }
                    if(types[j].typeId == 1){
                        viewHolder.img.setImageResource(R.drawable.power)
                    }
                    viewHolder.switch.isChecked = true
                } else {
                    viewHolder.img.setImageResource(R.drawable.lamp)
                    if(types[j].typeId == 1){
                        viewHolder.img.setImageResource(R.drawable.power)
                    }
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
                    val intent = Intent(ct, ActorActivity::class.java)
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
                    if(types[j].typeId == 1){
                        viewHolder.img.setImageResource(R.drawable.power)
                    }
                    val currVal =  devices[position].currentValue.toString()
                    net.postRequest(devices[position].clientId, currVal, viewHolder.itemView.context)
                }
            }
        }
    }
}
