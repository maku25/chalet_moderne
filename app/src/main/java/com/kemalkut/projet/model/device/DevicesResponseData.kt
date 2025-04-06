package com.kemalkut.projet.model.device

/**
 * Réponse de l'API contenant la liste des périphériques d'une maison.
 *
 *
 * @property devices Liste des périphériques connectés à la maison
 */
data class DevicesResponseData(
    val devices: List<DeviceData>
)
