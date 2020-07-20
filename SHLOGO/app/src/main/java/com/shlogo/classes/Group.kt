package com.shlogo.classes


class Group(name: String) {
    val groupName = name
    var devices = mutableListOf<Device>()

    private val serialVersionUID = 1L
}