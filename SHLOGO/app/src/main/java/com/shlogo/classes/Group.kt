package com.shlogo.classes

import android.os.Parcelable
import java.io.Serializable

class Group(name: String) {
    val groupName = name
    private var devices = mutableListOf<String>()

    private val serialVersionUID = 1L
}