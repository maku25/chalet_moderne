package com.kemalkut.projet.model.user

/**
 * Réponse de l'API lors de l'authentification d'un utilisateur.
 *
 * @property token Jeton d'authentification à utiliser pour les requêtes
 */
class AuthResponseData(
    val token: String
)