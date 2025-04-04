package com.kemalkut.projet.model.device

data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int? = null,
    val power: Int? = null
)
