package com.shlogo.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.GsonBuilder
import com.shlogo.R
import com.shlogo.activities.TestClass
import com.shlogo.classes.Device
import com.shlogo.classes.Type

class History : Fragment() {

    lateinit var device: Device
    lateinit var type: Type
    private var inView: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val queue: RequestQueue = Volley.newRequestQueue(this.requireContext())
        val url = "http://gitathome.dd-dns.de:62001/weatherforecast"
        var packagesArray: List<TestClass>? = null
        val entries1 = mutableListOf<Entry>()

        var lineDataSet1 = LineDataSet(entries1, "Temperature")
        lineDataSet1.color = Color.RED
        lineDataSet1.setDrawValues(false)

        val lineChartView = requireView().findViewById<com.github.mikephil.charting.charts.LineChart>(
            R.id.linechartHist
        )
        lineChartView.setDrawGridBackground(false)
        lineChartView.setDrawBorders(false)
        lineChartView.setNoDataText(" ")
        lineChartView.description.isEnabled = false
        var i = 0
        val histText = requireView().findViewById<TextView>(R.id.histData)
        val thread = Runnable {
            while (inView) {
                histText.post(Runnable {
                    val stringRequest = JsonArrayRequest(
                        Request.Method.GET, url, null,
                        Response.Listener { response ->
                            val gson = GsonBuilder().create()
                            packagesArray = gson.fromJson(response.toString(), Array<TestClass>::class.java).toList()
                        },
                        Response.ErrorListener {
                            fun onErrorResponse(error: VolleyError) {
                                Log.e("tag", "Error at sign in : " + error.message)
                            }
                            onErrorResponse(it)
                        }
                    )
                    queue.add(stringRequest)
                    if(!packagesArray.isNullOrEmpty()){
                        histText.text =  packagesArray!![0].temperatureC
                        val time = i.toFloat()
                        val value = packagesArray!![0].temperatureC.toFloat()
                        entries1.add(Entry(time, value))
                        lineDataSet1 = LineDataSet(entries1, "Temperature")
                        lineDataSet1.color = Color.RED
                        lineDataSet1.setCircleColor(Color.RED)
                        lineDataSet1.lineWidth = 14F
                        lineChartView.data = LineData(lineDataSet1)
                        lineChartView.notifyDataSetChanged()
                        lineChartView.invalidate()
                        i++
                    }
                })
                Thread.sleep(1000)
            }
        }
        val myThread = Thread(thread)
        myThread.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    companion object {
        fun newInstance() =
            History().apply {
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

}