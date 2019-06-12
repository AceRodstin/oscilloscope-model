package ru.models

class GraphModel {
    val lowerBound
        get() = if (valueName == "В") -scale * 5 else 0.0
    val tickUnit
        get() = scale
    val upperBound
        get() = if (valueName == "В") scale * 5 else scale * 10
    var scale = 0.0
    var valueName = "В"

    fun parse(selectedScale: String) {
        val digits = selectedScale.split(" ")[0].toDouble()
        val splitScale = selectedScale.split("/дел")[0]

        if (selectedScale.split(" ")[1].substring(0, 1) == "м") {
            scale = digits * 0.001
            valueName = splitScale.substring(splitScale.length - 1)
        } else {
            scale = digits
            valueName = splitScale.split(" ")[1]
        }
    }
}