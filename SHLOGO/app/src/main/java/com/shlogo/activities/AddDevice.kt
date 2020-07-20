package com.shlogo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.shlogo.R
import org.json.JSONArray
import java.io.*


class AddDevice : AppCompatActivity() {

    private lateinit var listview: ListView
    private lateinit var roomview: ListView
    private lateinit var button: Button
    private var activeGroups = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        groupList()
        roomList()

    }

    private fun roomList(){
        button = findViewById<Button>(R.id.roomListButton)
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
    private fun groupList(){
        button = findViewById<Button>(R.id.group_pop)
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
        val existing = readFromFile(this)
        val regex = Regex("id = ([0-9]+);")
        val matches = regex.findAll(existing)
        var id = mutableListOf<String>()
        matches.forEach { f ->
            id.add(f.groupValues[1])
        }
        var idcheck = 1000
        var x = 0
        while(x < id.size){
            idcheck++
            x++;
        }
        var type = ""
        val typcheck = idcheck % 2
        if(typcheck == 1){
            type = "led"
        } else {
            type = "sensor"
        }
        val idString = idcheck.toString()
        val device = findViewById<EditText>(R.id.deviceNameText).text.toString()
        val room = findViewById<EditText>(R.id.roomNameText).text.toString()
        if(device == "" || room == ""){
            val errorView = findViewById<TextView>(R.id.errorTextView)
            errorView.visibility = View.VISIBLE
            return
        }
        var group = findViewById<EditText>(R.id.groupNameText).text.toString()
        var grouplist: String = ""
        if(group != ""){
            grouplist = "[$group]"
        }
        var i = 0
        while (i < activeGroups.size){
            val gr = activeGroups[i]
            grouplist += "[$gr]"
            i++
        }
        //val text = "id = $idString;type = $type;device = $device;room = $room;group = $grouplist;$existing"
        //writeToFile(text, this)
        //val putString = "[$device, $room]"

        val deviceId = intent.getStringExtra("DEVICEID")
        val jsonObject = JSONArray("[$device, $room, $group]")
        //val jsonObject = JSONArray("[test, Bedroom]")
        val queue = Volley.newRequestQueue(this)
        //val url = "http://gitathome.dd-dns.de:61999/weatherforecast"
        val url2 = "http://gitathome.dd-dns.de:61999/api/Mqttclients/$deviceId"
        //61999
        val request = JsonArrayRequest(Request.Method.PUT, url2, jsonObject,
            Response.Listener { response ->
                try {
                } catch (e: Exception) {
                }

            }, Response.ErrorListener {
                fun onErrorResponse(error: VolleyError) {
                    Log.e("tag", "Error at sign in : " + error.message)
                }
                onErrorResponse(it)
            })
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(request)
        Thread.sleep(500)
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
    }
    fun onCancel(view: View){
        onBackPressed();
    }

    private fun writeToFile(data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput("devices.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }
    private fun readFromFile(context: Context): String {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput("devices.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also({ receiveString = it }) != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }

}