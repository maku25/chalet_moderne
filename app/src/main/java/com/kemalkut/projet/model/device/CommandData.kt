package com.kemalkut.projet.model.device

/**
 * Représente une commande à envoyer à un périphérique (lumière, volet ou garage).
 *
 * Cette classe est utilisée pour construire le corps JSON d'une requête POST
 * vers l'API lors de l'envoi d'une commande à un device.
 *
 * @property command La commande à exécuter (par ex "OPEN" pour le garage)
 */
data class CommandData(
    val command: String,
)
