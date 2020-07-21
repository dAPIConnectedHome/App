package com.shlogo.classes

/**
 * Room Class
 *
 * Hold the room name and the included devices as list.
 *
 * @param nam the rooms name
 */
class Room(nam: String) {
    var name = nam
    var devices = mutableListOf<Device>()
}