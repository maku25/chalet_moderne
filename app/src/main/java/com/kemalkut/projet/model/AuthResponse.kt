package com.kemalkut.projet.model

/**
 * Représente la réponse d'authentification renvoyée par le serveur.
 *
 * @property token le token d'authentification.
 */
data class AuthResponse(
    val token: String
)
