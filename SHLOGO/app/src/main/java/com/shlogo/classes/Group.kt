package com.shlogo.classes

/**
 * Group Class
 *
 * Hold the name and a list of devices.
 *
 * @param name the group name
 */
class Group(name: String) {
    val groupName = name
    var devices = mutableListOf<Device>()

    private val serialVersionUID = 1L
}