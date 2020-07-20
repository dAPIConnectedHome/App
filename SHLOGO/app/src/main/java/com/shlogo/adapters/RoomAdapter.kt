package com.shlogo.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shlogo.R
import com.shlogo.classes.Group
import com.shlogo.classes.Room

class RoomAdapter(
    c: Context,
    dev: MutableList<Room>,
    myAdapt: MutableList<MyAdapter>,
    grp: MutableList<Group>,
    myGrapt: MutableList<MyAdapter>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ct: Context = c
    private var rooms: MutableList<Room> = dev
    private var myAdapter: MutableList<MyAdapter> = myAdapt
    private var groups: MutableList<Group> = grp
    private var myGrapt: MutableList<MyAdapter> = myGrapt

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
            if(groups.isNotEmpty()){
                return rooms.size + groups.size
            }
            return rooms.size
        } else {
            return 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.roomName)
        val nestedView = itemView.findViewById<RecyclerView>(R.id.nestedView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            1 -> {
                val viewHolder: MyViewHolder = holder as MyViewHolder

                if(position < rooms.size){
                    var rows = 1
                    val params = viewHolder.nestedView.layoutParams
                    params.height = dpToPx(200)
                    if (rooms[position].devices.size > 5) {
                        params.height = dpToPx(400)
                        rows = 2
                    }
                    viewHolder.nestedView.layoutParams = params
                    viewHolder.text.text = rooms[position].name
                    viewHolder.nestedView.adapter = myAdapter[position]
                    viewHolder.nestedView.layoutManager = StaggeredGridLayoutManager(rows, StaggeredGridLayoutManager.HORIZONTAL)
                } else {
                    val index = position - rooms.size
                    var rows = 1
                    val params = viewHolder.nestedView.layoutParams
                    params.height = dpToPx(200)
                    if (groups[index].devices.size > 5) {
                        params.height = dpToPx(400)
                        rows = 2
                    }
                    viewHolder.nestedView.layoutParams = params
                    viewHolder.text.text = groups[index].groupName
                    viewHolder.nestedView.adapter = myGrapt[index]
                    viewHolder.nestedView.layoutManager = StaggeredGridLayoutManager(rows, StaggeredGridLayoutManager.HORIZONTAL)
                }
            }

        }
    }
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }
}