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
}
