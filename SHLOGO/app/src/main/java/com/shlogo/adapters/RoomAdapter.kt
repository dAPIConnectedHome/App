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

/**
 * Adapter of one Room
 *
 * Create the different holders of the nested recycler view
 *
 * @param ct the previous context
 * @param rooms list of all rooms
 * @param myAdapter list of all room adapter
 * @param groups list of all groups
 * @param myGrapt list of all group adapter
 */
class RoomAdapter(
    private var ct: Context,
    private var rooms: MutableList<Room>,
    private var myAdapter: MutableList<MyAdapter>,
    private var groups: MutableList<Group>,
    private var myGrapt: MutableList<MyAdapter>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Room/Group holder
     *
     * holder with the nested recycler view
     *
     * @param itemView view
     */
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.roomName)
        val nestedView = itemView.findViewById<RecyclerView>(R.id.nestedView)
    }

    /**
     * Create View holder
     *
     * returns the holder of the room name
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
                view = inflater.inflate(R.layout.room_name, parent, false)
                return MyViewHolder(view)
            }
        }
        return MyViewHolder(view)
    }

    /**
     * Return amount of room/group
     */
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

    /**
     * Functionality of resize
     *
     * Include the right nested recycler view and depending on the amount if devices resize the
     * nested view to 2 rows. Known bug: change of item count at runtime cause crash.
     *
     * @param holder the holder information
     * @param position the holder position
     */
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

    /**
     * Calculate the px of the smartphone
     */
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}