package com.shlogo.classes

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.annotations.SerializedName
import com.shlogo.R
import org.json.JSONArray


class Device(
     @SerializedName("clientId")
     var clientId: String,
     @SerializedName("name")
     var name: String,
     @SerializedName("topic")
     var topic: String,
     @SerializedName("room")
     var room: String,
     @SerializedName("typeid")
     var typeid: Int,
     @SerializedName("groupId")
     var groupId: String,
     @SerializedName("type")
     var type: String,
     @SerializedName("currentValue")
     var currentValue: Int
){
     public fun putRequest(dev: String, value: String, context: Context){
          val jsonObject = JSONArray("[$value]")
          val queue = Volley.newRequestQueue(context)
          var url = context.getString(R.string.url)
          url += "/$dev"
          val request = JsonArrayRequest(Request.Method.POST,url,jsonObject,
               Response.Listener { response ->
                    // Process the json
                    try {
                    }catch (e:Exception){
                    }
               }, Response.ErrorListener{
                    // Error in request
                    fun onErrorResponse(error: VolleyError) {
                         Log.e("tag", "Error at sign in : " + error.message)
                    }
                    onErrorResponse(it)
               })
          request.retryPolicy = DefaultRetryPolicy(
               DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
               0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
               1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
          )
          queue.add(request)
     }
}

/*class Device(i: String, na: String, ro: Room, ty: String, gr: List<Group>) {
     var id = i
     var name = na
     var room = ro
     var type = ty
     var group = gr

}*/