package com.shlogo.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import com.shlogo.R.*
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset


class ControlActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_control)
        val text = findViewById<TextView>(id.sensortest)
        text.text = readTxt()
        drawLineChart(text.text as String)
        val sw = findViewById<Switch>(id.onoff)
        var packagesArray: List<TestClass>
       //val params = Map<String,String>()
       //params["1"] = "test"
       //params["2"] = "test"
        val jsonObject = JSONArray("[tobistinktbrutal]")
        sw.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                // Instantiate the RequestQueue.
                val queue = Volley.newRequestQueue(this)
                val url = "http://gitathome.dd-dns.de:61999/weatherforecast"
                val url2 = "http://gitathome.dd-dns.de:61999/api/Client"
                //61999
                val request = JsonArrayRequest(Request.Method.POST,url2,jsonObject,
                    Response.Listener { response ->
                        // Process the json
                        try {
                            text.text = "Response: $response"
                        }catch (e:Exception){
                            text.text = "Exception: $e"
                        }

                    }, Response.ErrorListener{
                        // Error in request
                        text.text = "Volley error: $it"
                        fun onErrorResponse(error: VolleyError) {
                            Log.e("tag", "Error at sign in : " + error.message)
                        }
                        onErrorResponse(it)
                    })


                // Volley request policy, only one time request to avoid duplicate transaction
                request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    // 0 means no retry
                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )

                // Add the volley post request to the request queue
                queue.add(request)
                // Request a string response from the provided URL.
                val stringRequest = JsonArrayRequest(
                    Request.Method.GET, url, null,
                    Response.Listener { response ->
                        text.text = "Response is: %s" .format(response[0])
                        val gson = GsonBuilder().create()
                        packagesArray = gson.fromJson(response.toString(), Array<TestClass>::class.java).toList()
                        text.text = packagesArray[0].date
                    },
                    Response.ErrorListener {
                        text.text = "That didn't work!"
                        fun onErrorResponse(error: VolleyError) {
                            Log.e("tag", "Error at sign in : " + error.message)
                        }
                        onErrorResponse(it)
                    }
                )
                queue.add(stringRequest)
                //text.text = "Checked"
            } else{
                //text.text = readTxt()
            }
        }
    }

    private fun readTxt(): String {
        val stream = resources.openRawResource(raw.sensor_anwinkeln)
        return stream.readBytes().toString(Charset.defaultCharset())
    }
    private fun drawLineChart(testdata: String) {
        val lineChartView = findViewById<com.github.mikephil.charting.charts.LineChart>(
            id.linechart
        )
        //setContentView(R.layout.)

        val revenueComp1 = arrayListOf(10000, 20000, 30000, 40000)
        val revenueComp2 = arrayListOf(12000, 23000, 35000, 48000)

        val entries1 = mutableListOf<Entry>(Entry(0f, 0f))
        val entries2 = mutableListOf<Entry>(Entry(0f, 0f))
        val entries3 = mutableListOf<Entry>(Entry(0f, 0f))
        val pattern = "(-?[0-9.]+); *(-?[0-9.]+); *(-?[0-9.]+); *(-?[0-9.]+); *(-?[0-9.]+); *(-?[0-9.]+); *(-?[0-9.]+); *(-?[0-9.]+)".toRegex()
        val matches = pattern.findAll(testdata)
        matches.forEach { f ->
            val time = f.groupValues[1]
            val ax = f.groupValues[2]
            val ay = f.groupValues[3]
            val az = f.groupValues[4]
            val temp = f.groupValues[5]
            val gx = f.groupValues[6]
            val gy = f.groupValues[7]
            val gz = f.groupValues[8]
            entries1.add(Entry(time.toFloat(), gx.toFloat()))
            entries1.add(Entry(time.toFloat(), gy.toFloat()))
            entries1.add(Entry(time.toFloat(), gz.toFloat()))
        }


        val lineDataSet1 = LineDataSet(entries1, "X")
        lineDataSet1.color = Color.RED
        lineDataSet1.setDrawValues(false)

        val lineDataSet2 = LineDataSet(entries2, "Y")
        lineDataSet2.color = Color.BLUE
        lineDataSet1.setDrawValues(false)

        val lineDataSet3 = LineDataSet(entries3, "Z")
        lineDataSet2.color = Color.YELLOW
        lineDataSet1.setDrawValues(false)

        val lineDataSets =  LineData(lineDataSet1, lineDataSet2, lineDataSet3)
        lineChartView.data = lineDataSets
        lineChartView.invalidate()

    }

}

data class TestClassa(
    @SerializedName("date")
    var date: String,
    @SerializedName("temperatureC")
    var temperatureC: String,
    @SerializedName("temperatureF")
    var temperatureF: String,
    @SerializedName("summary")
    var summary: String
)
data class Example(
    @SerializedName("array")
    var array: List<TestClass>? = null
)