package com.kemalkut.projet.model

/**
 * Représente un périphérique d'une maison.
 *
 * @property id L'identifiant du périphérique.
 * @property type Le type du périphérique
 * @property availableCommands La liste des commandes disponibles pour ce périphérique.
 * @property opening Le niveau d'ouverture pour les volets.
 * @property power La puissance pour certains équipements.
 */
data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int? = null,
    val power: Int? = null
) {
    override fun toString(): String {
        return "$type ($id)"
    }
}