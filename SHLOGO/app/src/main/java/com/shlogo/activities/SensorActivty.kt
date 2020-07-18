package com.shlogo.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.shlogo.R

class SensorActivty : AppCompatActivity() {

    private val url = "http://gitathome.dd-dns.de:61999/weatherforecast"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)
        val id = intent.getStringExtra("ID")
        val type = intent.getStringExtra("TYPE")
        val name = intent.getStringExtra("NAME")
        val room = intent.getStringExtra("ROOM")
        val idText = findViewById<TextView>(R.id.idText)
        val typeText = findViewById<TextView>(R.id.typeText)
        val nameText = findViewById<TextView>(R.id.nameText)
        val roomText = findViewById<TextView>(R.id.roomText)
        idText.text = id
        typeText.text = type
        nameText.text = name
        roomText.text = room


        val queue: RequestQueue = Volley.newRequestQueue(this)
        val tempText = findViewById<TextView>(R.id.updateTemp)
        var packagesArray: List<TestClass>? = null
        val thread = Runnable {
            var i = 0
            while (true) {
                Thread.sleep(1000)
                i++
                tempText.post(Runnable {
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
                        tempText.text = packagesArray!![0].temperatureC
                    }
                })
            }
        }
        val myThread = Thread(thread)
        myThread.start()
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