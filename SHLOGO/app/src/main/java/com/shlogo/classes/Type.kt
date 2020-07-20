package com.shlogo.classes

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.shlogo.activities.MainActivity

class Type(
    @SerializedName("typeId")
    var typeId: Int,
    @SerializedName("direction")
    var direction: String,
    @SerializedName("mode")
    var mode: String,
    @SerializedName("rangeMin")
    var rangeMin: Int,
    @SerializedName("rangeMax")
    var rangeMax: Int
){
    fun getTypes(url: String, callback: MainActivity.VolleyCallbackTypes, context: Context){
        val queue = Volley.newRequestQueue(context)
        val arrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val gson = GsonBuilder().create()
                val list = gson.fromJson(response.toString(), Array<Type>::class.java).toList()
                callback.onSuccess(list);
            },
            Response.ErrorListener {
                fun onErrorResponse(error: VolleyError) {
                    Log.e("tag", "Error at sign in : " + error.message)
                }
                onErrorResponse(it)
            }
        )
        queue.add(arrayRequest)
    }
}