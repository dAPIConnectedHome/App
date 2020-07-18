package com.shlogo.classes

import com.shlogo.classes.Device

class Room(nam: String) {
    var name = nam

    var devices = mutableListOf<Device>()
}