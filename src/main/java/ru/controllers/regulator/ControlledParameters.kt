package ru.controllers.regulator

enum class ControlledParameters(val parameterName: String) {
    AMPLITUDE("Амплитуде"), DC("Статике"), FREQUENCY("Частоте")
}