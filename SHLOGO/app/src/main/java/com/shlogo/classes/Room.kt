package com.shlogo.classes


class Room(nam: String) {
    var name = nam
    var devices = mutableListOf<Device>()
}