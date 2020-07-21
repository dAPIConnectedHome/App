package com.shlogo.activities


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shlogo.R
import com.shlogo.R.layout
import com.shlogo.adapters.MyAdapter
import com.shlogo.adapters.RoomAdapter
import com.shlogo.classes.*
import java.io.*


/**
 * Main Activity/ Home Overview
 *
 * Show every room and their devices also append groups and their devices
 */
class MainActivity : Activity() {

    private var listOfDevices =  mutableListOf<Device>()
    private var listOfNotAddedDevices = mutableListOf<Device>()
    private var listOfGroups = mutableListOf<Group>()
    private var listOfRooms = mutableListOf<Room>()
    private var listOfTypes = mutableListOf<Type>()
    private val net = Networking()
    lateinit var text: String
    /**
     * Successful Server Request of all Devices
     *
     * Create main View and start simulation of the history
     */
    private val volDevices = object : Networking.VolleyCallbackDevices {
        override fun onSuccess(result: List<Device>?) {
            createMainView(result)
            simulateHistory()
        }
    }
    /**
     * Successful Server Request of Types
     *
     * Start server request of devices
     */
    private val volTypes = object : Networking.VolleyCallbackTypes {
        override fun onSuccess(result: List<Type>?) {
            listOfTypes =  (result as MutableList<Type>?)!!
            net.getDevices(volDevices, this@MainActivity)
        }
    }

    /**
     * Refresh Activity
     *
     * Restarts the Activity and get new device information
     *
     * @param view The View
     */
    fun onRefresh(view: View){
        recreate()
    }

    /**
     * Button Add new Device
     *
     * Open the Activity AddDevice to fill out the information for a new device
     *
     * @param view The View
     */
    fun addDevice(view: View){
        val groups = ArrayList<String>()
        var i = 0
        while (i < listOfGroups.size){
            groups.add(listOfGroups[i].groupName)
            i++
        }
        val rooms = ArrayList<String>()
        i = 0
        while (i < listOfRooms.size){
            rooms.add(listOfRooms[i].name)
            i++
        }
        var deviceId = ""
        if (listOfNotAddedDevices.isNotEmpty()){
            deviceId = listOfNotAddedDevices[0].clientId
        }
        val intent = Intent(this, AddDevice::class.java).apply {
        }
        intent.putExtra("GROUPS", groups)
        intent.putExtra("ROOMS", rooms)
        intent.putExtra("DEVICEID", deviceId)

        startActivity(intent)
    }


    /**
     * Create Lists and Text
     *
     * Is called after successfully getting data from the server.
     * Fill out the lists listOfDevices, listOfNotAddedDevices, listOfGroups, listOfRooms
     * from the result. Create with these information the RecyclerViews and the TextView for not
     * added devices.
     *
     * @param result The Result form the server request
     */
    fun createMainView(result: List<Device>?) {
        val textView = findViewById<TextView>(R.id.mainTextView)
        listOfDevices = (result as MutableList<Device>?)!!
        fillLists()
        createRecViews()
        var i = 0
        var textString = ""
        if(listOfNotAddedDevices.isNotEmpty()) {
            textString = "Not added devices: "
        }
        while (i < listOfNotAddedDevices.size){
            textString += listOfNotAddedDevices[i].clientId + " -- "
            i++
        }
        textView.text = textString
    }
    /**
     * Fill Lists
     *
     * Fill out the lists listOfDevices, listOfNotAddedDevices, listOfGroups, listOfRooms
     * from the result
     */
    private fun fillLists(){
        var i = 0
        while (i < listOfDevices.size) {
            if(listOfDevices[i].room == "-1"){
                listOfNotAddedDevices.add(listOfDevices[i])
                listOfDevices.removeAt(i)
                i--
            }
            i++
        }
        i=0
        while (i < listOfDevices.size){
            listOfDevices[i].type = "ToDo"
            var k = 0
            var roomAdded = false
            while (k < listOfRooms.size){
                if (listOfRooms[k].name == listOfDevices[i].room){
                    listOfRooms[k].devices.add(listOfDevices[i])
                    roomAdded = true
                }
                k++
            }
            if(!roomAdded){
                val newRoom = Room(listOfDevices[i].room)
                newRoom.devices.add(listOfDevices[i])
                listOfRooms.add(newRoom)
            }

            k = 0
            val regex = Regex(",?([^,]+)")
            val matches = regex.findAll(listOfDevices[i].groupId)
            matches.forEach { f ->
                var groupAdded = false
                k = 0
                while (k < listOfGroups.size){
                    if (listOfGroups[k].groupName == f.groupValues[1]){
                        listOfGroups[k].devices.add(listOfDevices[i])
                        groupAdded = true
                    }
                    k++
                }
                if(!groupAdded && listOfDevices[i].groupId != "-1"){
                    val newGroup = Group(f.groupValues[1])
                    newGroup.devices.add(listOfDevices[i])
                    listOfGroups.add(newGroup)
                }
            }
            i++
        }
    }
    /**
     * Create Views
     *
     * Create with previous information the RecyclerViews.
     */
    private fun createRecViews(){
        var i = 0
        val listOfAdapter = mutableListOf<MyAdapter>()
        val listOfAdapterGroup = mutableListOf<MyAdapter>()
        while (i < listOfRooms.size){
            val myAdapter = MyAdapter(
                this@MainActivity,
                listOfRooms[i].devices,
                listOfTypes
            )
            i++
            listOfAdapter.add(myAdapter)
        }
        i = 0
        while (i < listOfGroups.size){
            val myAdapter = MyAdapter(
                this@MainActivity,
                listOfGroups[i].devices,
                listOfTypes
            )
            i++
            listOfAdapterGroup.add(myAdapter)
        }
        val roomAdapter = RoomAdapter(
            this@MainActivity,
            listOfRooms,
            listOfAdapter,
            listOfGroups,
            listOfAdapterGroup
        )
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = roomAdapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
    }

    private val simHis = object : Networking.VolleyCallbackDevice{
        override fun onSuccess(result: Device?) {
            val device = result!!
            val filename = device.clientId + ".txt"
            var data = device.currentValue.toString() + ";"
            val exist = readFromFile(filename, this@MainActivity)
            data = exist + data
            writeToFile(filename, data, this@MainActivity)
        }
    }
    /**
     * Run simulation of History
     *
     * Open up a new thread, which get the current data of the server in a 5 sec interval and store
     * them local in files. These are later read to show the graph.
     */
    private fun simulateHistory(){
        val thread = Runnable {
            while (true) {
                var i = 0
                while(i < listOfDevices.size){
                    net.getDevice(simHis, listOfDevices[i].clientId, this)
                    i++
                }
                Thread.sleep(5000)
            }
        }
        val myThread = Thread(thread)
        myThread.start()
    }

    /**
     * Read a file
     *
     * Read a text file and return its content as string
     *
     * @param fileName the readable filename
     * @param context the current context
     */
    fun readFromFile(fileName: String, context: Context): String {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput(fileName)
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: $e")
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }
    /**
     * Write a file
     *
     * Write to a text file a string
     *
     * @param fileName the readable filename
     * @param data the data which is written
     * @param context the current context
     */
    private fun writeToFile(fileName: String, data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
    }
    override fun onResume() {
        listOfDevices.clear()
        listOfNotAddedDevices.clear()
        listOfGroups.clear()
        listOfRooms.clear()
        listOfTypes.clear()
        net.getTypes(volTypes,this)
        super.onResume()
    }
}




/*-OLD DATA-*/
/*
val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
val existing = readFromFile(this)
val regex = Regex("id = ([0-9]+);type = ([^;]*);device = ([^;]*);room = ([^;]*);group = ([^;]*);")
val matches = regex.findAll(existing)
var id = mutableListOf<String>()
var type = mutableListOf<String>()
var device = mutableListOf<String>()
var room = mutableListOf<String>()
var group = mutableListOf<String>()
var testi = ""
matches.forEach { f ->
    id.add(f.groupValues[1])
    type.add(f.groupValues[2])
    device.add(f.groupValues[3])
    room.add(f.groupValues[4])
    group.add(f.groupValues[5])
    var newRoom = Room(f.groupValues[4])
    var boolRoom = true
    var j = 0
    while (j< listOfRooms.size){
        if(listOfRooms[j].name == newRoom.name){
            boolRoom = false
            newRoom = listOfRooms[j]
        }
        j++
    }
    if(boolRoom){
        listOfRooms.add(newRoom)
    }
    var deviceGroups = mutableListOf<Group>()
    val regexGroup = Regex("\\[([^\\]]*)\\]")
    val matchesGroup = regexGroup.findAll(f.groupValues[5])
    matchesGroup.forEach { g ->
        testi += g.groupValues[1]
        var newGroup = Group(g.groupValues[1])
        var bool = true
        var i = 0
        while (i< listOfGroups.size){
            if(listOfGroups[i].groupName == newGroup.groupName){
                bool = false
                newGroup = listOfGroups[i]
            }
            i++
        }
        if(bool){
            listOfGroups.add(newGroup)
        }
        deviceGroups.add(newGroup)
    }
    val newDevice = Device(
        f.groupValues[1],
        f.groupValues[2],
        newRoom,
        f.groupValues[4],
        deviceGroups
    )
    listOfDevices.add(newDevice)
    var i = 0
    while(i < listOfRooms.size){
        if(newRoom == listOfRooms[i]){
            listOfRooms[i].devices.add(newDevice)
        }
        i++
    }
    i = 0
}
//Bubblesort für Raumnamen
var i = id.size
var j = 0
while(i > 1){
    while(j < id.size-1){
        if(room[j] > room[j+1]){
            val i = id[j]
            id[j] = id[j+1]
            id[j+1] = i
            val t = type[j]
            type[j] = type[j+1]
            type[j+1] = t
            val d = device[j]
            device[j] = device[j+1]
            device[j+1] = d
            val g = group[j]
            group[j] = group[j+1]
            group[j+1] = g
           val r = room[j]
            room[j] = room[j+1]
            room[j+1] = r
        }
        j++
    }
    i--
    j = 0
}
j = 0
var roomcap = " "
var size = room.size
while(j < size){
    if(room[j] != roomcap){
        room.add(j,room[j])
        roomcap = room[j]

        id.add(j,"-1")
        type.add(j,"-1")
        device.add(j,"-1")
        group.add(j,"-1")
        size++
    }
    j++
}

var texto = ""
var x = 0

while(x < listOfDevices.size){
    val i = listOfDevices[x].id
    val t = listOfDevices[x].type
    val d = listOfDevices[x].name
    val r = listOfDevices[x].room.name
    var g = ""
    var it = 0
    while(it < listOfDevices[x].group.size){
        g += listOfDevices[x].group[it].groupName
        it++
    }
    //val g = listOfDevices[x].group[]
    texto += "id $i t $t d $d r $r g $g\n"
    x++;
}
val textView = findViewById<TextView>(R.id.textView2)
val text = texto
textView.text = text

val myAdapter = MyAdapter(
    this,
    id,
    type,
    device,
    room,
    group,
    listOfDevices
)
recyclerView.adapter = myAdapter
recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)*/
/*public final class MainActivity : FragmentActivity() {

    var layoutList = mutableListOf<ConstraintLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        val inflater =layoutInflater
        val view = inflater.inflate(R.layout.actor, mainmain, false)

        mainmain.addView(view)
        //if (savedInstanceState == null) {
        //    myFragment = MyFragment.newInstance()
        //    supportFragmentManager
        //        .beginTransaction()
        //        .add(R.id.my_container, myFragment, MY_FRAGMENT_TAG)
        //        .commit()
        //} else {
        //    myFragment = supportFragmentManager
        //        .findFragmentByTag(MY_FRAGMENT_TAG) as MyFragment?
        //}
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        //var test: String = readFromFile(this)
        //val text = findViewById<TextView>(R.id.textView2)
        //text.text = test
        //val regex = Regex("id = (.+);")
        //val matches = regex.findAll(test)
        //matches.forEach {
        //    addLamp(findViewById(R.id.main_layout))
        //    View(this).invalidate()
        //}
        super.onResume()
    }

    private val fragmentManager = supportFragmentManager
    private var count = 0
    private var lastfragtag = ""
    private var currfragtag = ""
    private var lastconstraint = R.id.fragment_layout
    private var firstconstrow = lastconstraint
    private var idc = 0
    private val constraintList = mutableListOf<Int>()
    //private var lastconstraint = findViewById<ConstraintLayout>(R.id.main_layout)
    val frmg = fragmentManager.registerFragmentLifecycleCallbacks(object: FragmentLifecycleCallbacks(){
        override fun onFragmentActivityCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentActivityCreated(fm, f, savedInstanceState)
            //setConstraints()
            val vg: ViewGroup = findViewById<ConstraintLayout>(R.id.mainmain)


            vg.invalidate()
        }
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            //setConstraints()
            val vg: ViewGroup = findViewById<ConstraintLayout>(R.id.mainmain)
            vg.invalidate()
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)

            val vg: ViewGroup = findViewById<ConstraintLayout>(R.id.mainmain)
            vg.invalidate()

        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            val vg: ViewGroup = findViewById<ConstraintLayout>(R.id.mainmain)
            vg.invalidate()
        }
    }, true)


    fun onBottonClick(view: View) {
        val intent = Intent(this, ControlActivity::class.java).apply {
        }
        startActivity(intent)
    }

    fun addDevice(view: View){
        val intent = Intent(this, AddDevice::class.java).apply {
        }
        startActivity(intent)
        //val fragmentTransaction = fragmentManager.beginTransaction()
        //val fragment = AddDevice.newInstance("test", "test")
        //fragmentTransaction.add(lastconstraint, fragment, currfragtag)
        //fragmentTransaction.commit()
        //view.invalidate()
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

    fun addLamp(view: View) {


        layoutList.add(findViewById(lastconstraint))
        constraintList.add(lastconstraint)
        val text = findViewById<TextView>(R.id.textView2)
        val test = constraintList.toString()
        text.text = test

        //ADD Frag
        val fragmentTransaction = fragmentManager.beginTransaction()
        lastfragtag = currfragtag
        currfragtag = "lamp$count"
        count++
       val fragment = BulbFragment.newInstance()
       fragmentTransaction.add(lastconstraint, fragment, currfragtag)
       fragmentTransaction.commit()
       view.invalidate()
        fragment_layout.invalidate()
        //ADD new Layout
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainmain)
        val frameLayout: ConstraintLayout = ConstraintLayout(this);
        val set = ConstraintSet()
        frameLayout.id = View.generateViewId()
        val clpr = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        frameLayout.layoutParams = clpr
        mainLayout.addView(frameLayout, 0)
        set.clone(mainLayout)


        set.connect(frameLayout.id, ConstraintSet.TOP, lastconstraint, ConstraintSet.TOP, 0)
        set.connect(frameLayout.id, ConstraintSet.LEFT, lastconstraint, ConstraintSet.RIGHT, 80);
        for (x in 0 until constraintList.size) {
            when {
                //constraintList[x] == constraintList[0] -> {
                //    set.connect(
                //        constraintList[x],
                //        ConstraintSet.LEFT,
                //        R.id.main_layout,
                //        ConstraintSet.LEFT,
                //        0
                //    )
                //    if( constraintList[x] != constraintList.last()) {
                //        set.connect(
                //            constraintList[x],
                //            ConstraintSet.RIGHT,
                //            constraintList[x + 1],
                //            ConstraintSet.LEFT,
                //            0
                //        )
                //    }
                //}
                constraintList[x] == constraintList.last() -> {
                    //set.connect(
                    //    constraintList[x],
                    //    ConstraintSet.RIGHT,
                    //    R.id.main_layout,
                    //    ConstraintSet.RIGHT,
                    //    32
                    //)
                    //if(constraintList.first() != constraintList.last()) {
                    //    set.connect(
                    //    constraintList[x],
                    //    ConstraintSet.LEFT,
                    //    constraintList[x - 1],
                    //    ConstraintSet.RIGHT,
                    //    0
                    //    )
                    //    set.connect(
                    //        constraintList[x],
                    //        ConstraintSet.TOP,
                    //        constraintList[x - 1],
                    //        ConstraintSet.TOP,
                    //        0
                    //    )
                    //}
                }
                //else -> {
                //    set.connect(
                //        constraintList[x],
                //        ConstraintSet.RIGHT,
                //        constraintList[x+1],
                //        ConstraintSet.LEFT,
                //        0
                //    )
                //    set.connect(
                //        constraintList[x],
                //        ConstraintSet.LEFT,
                //        constraintList[x-1],
                //        ConstraintSet.RIGHT,
                //        0
                //    )
                //    set.connect(
                //        constraintList[x],
                //        ConstraintSet.TOP,
                //        constraintList[x - 1],
                //        ConstraintSet.TOP,
                //        0
                //    )
                //}
            }
        }
        //set.connect(constraintList[constraintList.size-2], ConstraintSet.RIGHT, R.id.main_layout, ConstraintSet.RIGHT, 0);
        //for(x in 0 until constraintList.size-2){
        //    set.connect(constraintList[x], ConstraintSet.RIGHT, constraintList[x+1], ConstraintSet.LEFT, 0)
        //}
        set.applyTo(mainLayout)


        val isV = isVisible(findViewById(lastconstraint))
        if(!isV) {
            val set = ConstraintSet()
            set.clone(findViewById<ConstraintLayout>(R.id.mainmain))
            constraintList.clear()
            set.connect(lastconstraint, ConstraintSet.TOP, firstconstrow, ConstraintSet.BOTTOM, 0)
            set.connect(lastconstraint, ConstraintSet.LEFT, firstconstrow, ConstraintSet.LEFT, 0)
            set.clear(lastconstraint, ConstraintSet.RIGHT);
            set.applyTo(findViewById<ConstraintLayout>(R.id.mainmain))
            firstconstrow = lastconstraint
        }

        lastconstraint = frameLayout.id
    }

    fun isVisible(view: View?): Boolean {
        if (view == null) {
            return false
        }
        if (!view.isShown) {
            return false
        }
        val screen = Resources.getSystem().displayMetrics.widthPixels
        val outLoc = intArrayOf(0, 0)
        val actualPosition2 = view.getLocationOnScreen(outLoc)
        if(outLoc[0] > (screen - (resources.displayMetrics.density*100))){
            return false
        }
        return true
    }
}*/




