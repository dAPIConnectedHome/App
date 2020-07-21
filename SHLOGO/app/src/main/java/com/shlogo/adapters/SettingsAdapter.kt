package com.shlogo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.shlogo.R
import com.shlogo.classes.Device

/**
 * Adapter of Settings view
 *
 * Create the different holders of the device informations in settings activity
 *
 * @param ct the previous context
 * @param device the current device to change the settings
 */
class SettingsAdapter(
    private var ctx: Context,
    private val device: Device
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * One Information holder
     *
     * contains the topic, and the fill-able text
     *
     * @param itemView view
     */
    class InformationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.settingsInfoMainLayout)
        val textTitle = itemView.findViewById<TextView>(R.id.settingsTitle)
        val textInfoFill = itemView.findViewById<TextInputEditText>(R.id.settingsInfoFill)
    }
    /**
     * Create View holder
     *
     * returns the holder of the one information
     *
     * @param parent view group of the parent
     * @param viewType current viewType to choose from
     *
     * @return holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.settings_info_holder, parent, false)
        return InformationHolder(view)
    }
    /**
     * Fix size of 3 (Name, Room, Group)
     */
    override fun getItemCount(): Int {
        return 3
    }

    /**
     * Fill the holder with information
     *
     * @param holder the holder information
     * @param position the holder position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: InformationHolder = holder as InformationHolder
        when(position){
            0 ->{
                viewHolder.textTitle.text = ctx.getString(R.string.device_name)
                viewHolder.textInfoFill.setText(device.name)
                viewHolder.textInfoFill.tag = "nameTag"
            }
            1 ->{
                viewHolder.textTitle.text = ctx.getString(R.string.room_name)
                viewHolder.textInfoFill.setText(device.room)
                viewHolder.textInfoFill.tag = "roomTag"
            }
            2 ->{
                viewHolder.textTitle.text = ctx.getString(R.string.group_name)
                viewHolder.textInfoFill.setText(device.groupId)
                viewHolder.textInfoFill.tag = "groupTag"
            }
        }

    }

}