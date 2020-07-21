package com.shlogo.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.shlogo.R
import com.shlogo.classes.Networking

/**
 * AddDevice Activity
 *
 * Activity which let user add a new device to the app
 */
class AddDevice : AppCompatActivity() {

    private lateinit var listview: ListView
    private lateinit var roomview: ListView
    private lateinit var buttonRoom: ImageButton
    private lateinit var buttonGroup: ImageButton
    private var activeGroups = mutableListOf<String>()

    /**
     * Successful Server Request of Put Request
     *
     * Go back to the overview activity
     */
    private val changesApplied = object : Networking.VolleyCallbackPut {
        override fun onSuccess() {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        groupList()
        roomList()

    }

    /**
     * Room list Popup functionality
     *
     * Configure the functionality of the room popup view.
     */
    private fun roomList(){
        buttonRoom = findViewById<ImageButton>(R.id.roomListButton)
        roomview = findViewById<ListView>(R.id.roomList)
        roomview.choiceMode = ListView.CHOICE_MODE_SINGLE
        roomview.setBackgroundColor(0xDFDFDFDF.toInt())
        val rooms = intent.getStringArrayListExtra("ROOMS")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rooms)
        roomview.adapter = adapter
        roomview.visibility = View.INVISIBLE
        roomview.onItemClickListener = OnItemClickListener { _, view, position, _ ->
            hidePopups(view)
            val room = roomview.getItemAtPosition(position) as String
            val editText = findViewById<EditText>(R.id.roomNameText)
            editText.setText(room)
            roomview.visibility = View.INVISIBLE
        }
    }
    /**
     * Group list Popup functionality
     *
     * Configure the functionality of the group popup view.
     */
    private fun groupList(){
        buttonGroup = findViewById<ImageButton>(R.id.group_pop)
        listview = findViewById<ListView>(R.id.groupList)
        listview.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listview.setBackgroundColor(0xDFDFDFDF.toInt())
        val groups = intent.getStringArrayListExtra("GROUPS")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, groups)
        listview.adapter = adapter
        listview.visibility = View.INVISIBLE

        listview.setOnItemClickListener { _, view, position, _ ->
            val v = view as CheckedTextView
            val currentCheck = v.isChecked
            val group = listview.getItemAtPosition(position) as String
            if(currentCheck){
                activeGroups.add(group)
            } else{
                activeGroups.remove(group)
            }
            val groupEdit = findViewById<TextView>(R.id.activeGroups)
            groupEdit.text = activeGroups.toString()
        }
    }

    /**
     * Popups visibility
     *
     * Set the visibilities of the popups on user inputs
     */
    fun hidePopups(view: View){
        if(listview.visibility == View.VISIBLE){
            listview.visibility = View.INVISIBLE
        }
        if(roomview.visibility == View.VISIBLE){
            roomview.visibility = View.INVISIBLE
        }
    }
    fun onGroupPopup(view: View){
        if(listview.visibility == View.INVISIBLE){
            listview.visibility = View.VISIBLE
        } else {
            listview.visibility = View.INVISIBLE
        }
    }
    fun onRoomPopup(view: View){
        if(roomview.visibility == View.INVISIBLE){
            roomview.visibility = View.VISIBLE
        } else {
            roomview.visibility = View.INVISIBLE
        }
    }

    fun onFinish(view: View){

        val device = findViewById<EditText>(R.id.deviceNameText).text.toString()
        val room = findViewById<EditText>(R.id.roomNameText).text.toString()
        if(device == "" || room == "" || room == "-1"){
            val errorView = findViewById<TextView>(R.id.errorTextView)
            errorView.visibility = View.VISIBLE
            return
        }
        val group = findViewById<EditText>(R.id.groupNameText).text.toString()
        var grouplist: String = ""
        if(group != ""){
            grouplist = group
        }
        var i = 0
        while (i < activeGroups.size){
            val gr = activeGroups[i]
            grouplist += ",$gr"
            i++
        }
        val deviceId = intent.getStringExtra("DEVICEID")!!

        val net = Networking()
        net.putRequest(changesApplied, deviceId, device, room, grouplist, this)
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
    }
    fun onCancel(view: View){
        onBackPressed();
    }
}




//INIT TEST WITHOUT SERVER

//val existing = readFromFile(this)
//val regex = Regex("id = ([0-9]+);")
//val matches = regex.findAll(existing)
//var id = mutableListOf<String>()
//matches.forEach { f ->
//    id.add(f.groupValues[1])
//}
//var idcheck = 1000
//var x = 0
//while(x < id.size){
//    idcheck++
//    x++;
//}
//var type = ""
//val typcheck = idcheck % 2
//if(typcheck == 1){
//    type = "led"
//} else {
//    type = "sensor"
//}
//val idString = idcheck.toString()



//val text = "id = $idString;type = $type;device = $device;room = $room;group = $grouplist;$existing"
//writeToFile(text, this)
//val putString = "[$device, $room]"