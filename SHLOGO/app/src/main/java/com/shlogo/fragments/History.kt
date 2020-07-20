package com.shlogo.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.annotations.SerializedName
import com.shlogo.R
import com.shlogo.classes.Device
import com.shlogo.classes.Type
import java.io.*

class History : Fragment() {

    lateinit var device: Device
    lateinit var type: Type
    private var inView: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //val queue: RequestQueue = Volley.newRequestQueue(this.requireContext())
        //val url = "http://gitathome.dd-dns.de:62001/weatherforecast"
        //var packagesArray: List<TestClass>? = null
        val deviceNameText = requireActivity().findViewById<TextView>(R.id.histData)
        deviceNameText.text = device.name
        var label = ""
        when (device.typeid) {
            100 -> {
                label = "Temperature"
            }
            101 -> {
                label = "humidity"
            }
            102 -> {
                label = "UV"
            }
        }
        val filename = device.clientId + ".txt"
        val data = readFromFile(filename, requireContext())
        val entries1 = mutableListOf<Entry>()
        val regex = Regex("([0-9]+);")
        val matches = regex.findAll(data)
        //var iString = readFromFile("timestamp.txt", requireContext())
        var i = 0
        //if(iString != ""){
        //    i = iString.toInt()
        //}
        matches.forEach { f ->
            val x = (i * 5000)
            entries1.add(Entry(x.toFloat(), f.groupValues[1].toFloat()))
            i++
        }
        var lineDataSet1 = LineDataSet(entries1, label)
        lineDataSet1.color = Color.RED
        lineDataSet1.setDrawValues(false)
        val lineChartView = requireView().findViewById<com.github.mikephil.charting.charts.LineChart>(
            R.id.linechartHist
        )
        lineChartView.setDrawGridBackground(false)
        lineChartView.setDrawBorders(false)
        lineChartView.setNoDataText(" ")
        lineChartView.description.isEnabled = false
        //var i = 0
        val histText = requireView().findViewById<TextView>(R.id.histData)
        lineChartView.data = LineData(lineDataSet1)
        lineChartView.invalidate()


        //val thread = Runnable {
        //    while (inView) {
        //        histText.post(Runnable {
        //            val stringRequest = JsonArrayRequest(
        //                Request.Method.GET, url, null,
        //                Response.Listener { response ->
        //                    val gson = GsonBuilder().create()
        //                    packagesArray = gson.fromJson(response.toString(), Array<TestClass>::class.java).toList()
        //                },
        //                Response.ErrorListener {
        //                    fun onErrorResponse(error: VolleyError) {
        //                        Log.e("tag", "Error at sign in : " + error.message)
        //                    }
        //                    onErrorResponse(it)
        //                }
        //            )
        //            queue.add(stringRequest)
        //            if(!packagesArray.isNullOrEmpty()){
        //                histText.text =  packagesArray!![0].temperatureC
        //                val time = i.toFloat()
        //                val value = packagesArray!![0].temperatureC.toFloat()
        //                entries1.add(Entry(time, value))
        //                lineDataSet1 = LineDataSet(entries1, "Temperature")
        //                lineDataSet1.color = Color.RED
        //                lineDataSet1.setCircleColor(Color.RED)
        //                lineDataSet1.lineWidth = 14F
        //                lineChartView.data = LineData(lineDataSet1)
        //                lineChartView.notifyDataSetChanged()
        //                lineChartView.invalidate()
        //                i++
        //            }
        //        })
        //        Thread.sleep(1000)
        //    }
        //}
        //val myThread = Thread(thread)
        //myThread.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    companion object {
        fun newInstance(dev: Device) =
            History().apply {
                device = dev
            }
    }

    override fun onStart() {
        inView = true
        super.onStart()
    }
    override fun onStop() {
        inView = false
        super.onStop()
    }
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
}

data class TestClass(
    @SerializedName("date")
    var date: String,
    @SerializedName("temperatureC")
    var temperatureC: String,
    @SerializedName("temperatureF")
    var temperatureF: String,
    @SerializedName("summary")
    var summary: String
)
