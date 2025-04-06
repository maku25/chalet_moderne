package com.kemalkut.projet.model.device

/**
 * Représente un périphérique connecté dans une maison (volet, lumière et garage).
 *
 * Cette classe est utilisée pour modéliser un device retourné par l'API.
 *
 * @property id Identifiant unique du périphérique (par ex: "Light 1.1")
 * @property type Type du périphérique ("light", "rolling shutter" ou "garage door")
 * @property availableCommands Liste des commandes disponibles pour ce périphérique
 * @property opening (Optionnel) Indicateur d'ouverture pour les volets/garage
 * @property power (Optionnel) État des lumières (0 = éteint, 1 = allumé)
 */
data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int? = null,
    val power: Int? = null
)
