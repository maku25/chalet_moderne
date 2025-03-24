package com.kemalkut.projet.model

/**
 * Représente la réponse de l'API contenant la liste des périphériques.
 *
 * @property devices La liste des périphériques récupérés.
 */
data class DeviceResponse(
    val devices: List<DeviceData>
)
