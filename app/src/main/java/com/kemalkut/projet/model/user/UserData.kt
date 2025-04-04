package com.kemalkut.projet.model.user

/**
 * Représente les informations d'un utilisateur utilisées pour l'inscription ou la connexion.
 *
 * @property login l'identifiant de l'utilisateur.
 * @property password le mot de passe de l'utilisateur.
 */
data class UserData(
    val login: String,
    val password: String
)
