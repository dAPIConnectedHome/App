package com.shlogo.classes

import com.google.gson.annotations.SerializedName

/**
 * Device Class
 *
 * Hold the information of one device. Is directly converted from Json Array.
 *
 * @param clientId the id of the client get from the broker
 * @param name name set from the user (or default name)
 * @param topic currently just the clientId
 * @param room the name of room (default: -1)
 * @param typeid the id to find the type information
 * @param groupId no id, but actual names of the groups (default: -1)
 * @param type name of type, currently not used
 * @param currentValue the current value from the server
 */
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
