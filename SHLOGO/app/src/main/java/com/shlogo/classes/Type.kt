package com.shlogo.classes

import com.google.gson.annotations.SerializedName

/**
 * Type Class
 *
 * Hold the information of one type directly converted from Json Array
 *
 * @param typeId the id of the type
 * @param direction the direction to send information
 * @param mode not used
 * @param rangeMin min value for this type
 * @param rangeMax max value for this type
 */
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