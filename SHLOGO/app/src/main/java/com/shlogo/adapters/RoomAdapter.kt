package com.shlogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shlogo.R
import com.shlogo.classes.Device
import com.shlogo.classes.Room

class RoomAdapter(
    c: Context,
    dev: MutableList<Room>,
    myAdapt: MutableList<MyAdapter>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ct: Context = c
    private var rooms: MutableList<Room> = dev
    private var myAdapter: MutableList<MyAdapter> = myAdapt

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(ct)
        lateinit var view: View
        when (viewType){
            1 -> {
                view = inflater.inflate(R.layout.room_name, parent, false)
                return MyViewHolder(view)
            }
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (rooms.isNotEmpty()){
            return rooms.size
        } else {
            return 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }
    //public class MyViewHolder0(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //    val room = itemView.findViewById<TextView>(R.id.roomName)
    //}
    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.roomName)
        val nestedView = itemView.findViewById<RecyclerView>(R.id.nestedView)
    }
    //public class SensorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //    val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.mainLayout)
    //    val text = itemView.findViewById<TextView>(R.id.sensorText)
    //    val sensor = itemView.findViewById<ImageView>(R.id.image)
    //}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            1 -> {
                val viewHolder: MyViewHolder = holder as MyViewHolder
                viewHolder.text.text = rooms[position].name
                viewHolder.nestedView.adapter = myAdapter[position]
                viewHolder.nestedView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            }
        }
    }
}