package com.shlogo.classes

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.shlogo.R
import org.json.JSONArray

/**
 * Networking Class
 *
 * Handle all the network stuff. Access the one singelton request queue.
*/
class Networking {

    /**
     * Get information of one device
     *
     * Add a Get request to the queue to get the information of one device.
     * On success call the callback on success function.
     *
     * @param callback the associated callback
     * @param dev the device which is requested
     * @param ctx previous context
     */
    fun getDevice(callback: VolleyCallbackDevice, dev: String, ctx: Context) {
        val url = ctx.getString(R.string.url) + "/$dev"
        val arrayRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val gson = GsonBuilder().create()
                val list = gson.fromJson(response.toString(), Device::class.java)
                callback.onSuccess(list);
            },
            Response.ErrorListener {
                fun onErrorResponse(error: VolleyError) {
                    Log.e("tag", "Error at sign in : " + error.message)
                }
                onErrorResponse(it)
            }
        )
        MySingleton.getInstance(ctx).addToRequestQueue(arrayRequest)
    }
    /**
     * Post request
     *
     * Add a post request to the queue to change the value in the database of the device.
     *
     * @param dev the device which is requested
     * @param value the value of the change request
     * @param ctx previous context
     */
    fun postRequest(dev: String, value: String, context: Context){
        val jsonObject = JSONArray("[$value]")
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
        MySingleton.getInstance(context).addToRequestQueue(request)
    }
    /**
     * Put request
     *
     * Add a put request to the queue to change the device information in the database.
     * On success call the callback on success function.
     *
     * @param callback the associated callback
     * @param dev the device which is requested
     * @param name the name of the device to change to
     * @param room the room of the device to change to
     * @param group the group/s of the device to change to
     * @param ctx previous context
     */
    fun putRequest(callback: VolleyCallbackPut, dev: String, name: String, room: String, group: String, context: Context){
        val jsonObject = JSONArray("[\"$name\", \"$room\", \"$group\"]")
        var url = context.getString(R.string.url)
        url += "/$dev"
        val request = JsonArrayRequest(Request.Method.PUT, url, jsonObject,
            Response.Listener { response ->

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
        callback.onSuccess()
        MySingleton.getInstance(context).addToRequestQueue(request)
    }
    /**
     * Get request
     *
     * Add a Get request to the queue to get the information of all devices.
     * On success call the callback on success function.
     *
     * @param callback the associated callback
     * @param ctx previous context
     */
    fun getDevices(callback: VolleyCallbackDevices, context: Context) {
        val url = context.getString(R.string.url)
        val arrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val gson = GsonBuilder().create()
                val list = gson.fromJson(response.toString(), Array<Device>::class.java).toList()
                callback.onSuccess(list);
            },
            Response.ErrorListener {
                fun onErrorResponse(error: VolleyError) {
                    Log.e("tag", "Error at sign in : " + error.message)
                }
                onErrorResponse(it)
            }
        )
        MySingleton.getInstance(context).addToRequestQueue(arrayRequest)
    }
    /**
     * Get request
     *
     * Add a Get request to the queue to get the information of all types.
     * On success call the callback on success function.
     *
     * @param callback the associated callback
     * @param ctx previous context
     */
    fun getTypes(callback: VolleyCallbackTypes, context: Context){
        val url = context.getString(R.string.urlTypes)
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
        MySingleton.getInstance(context).addToRequestQueue(arrayRequest)
    }


    interface VolleyCallbackDevices {
        fun onSuccess(result: List<Device>?)
    }
    interface VolleyCallbackTypes {
        fun onSuccess(result: List<Type>?)
    }
    interface VolleyCallbackDevice {
        fun onSuccess(result: Device?)
    }
    interface VolleyCallbackPut {
        fun onSuccess(){
        }
    }
}
/**
 * Singelton of the request queue
 *
 * the server can be overloaded when more the one request queue is active -> every
 * request is handled in this singelton queue.
 *
 * @param ctx previous context
 */
class MySingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: MySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MySingleton(context).also {
                    INSTANCE = it
                }
            }
    }
    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}