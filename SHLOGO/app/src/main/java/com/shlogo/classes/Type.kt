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
}