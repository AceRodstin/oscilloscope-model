package ru.controllers.signal

enum class SignalTypes(val typeName: String) {
    SINE("Синусоида"), PULSE("Импульсный"), TRIANGLES("Треугольный"),
    SAW("Пила"), NOISE("Шум");
}