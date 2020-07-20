package com.shlogo.classes


class Group(name: String) {
    val groupName = name
    private var devices = mutableListOf<String>()

    private val serialVersionUID = 1L
}