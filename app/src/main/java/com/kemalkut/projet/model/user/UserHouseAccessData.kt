package com.kemalkut.projet.model.user

/**
 * Informations d'accès d'un utilisateur à une maison.
 *
 * Cette classe est utilisée pour afficher les utilisateurs ayant accès à une maison,
 * ainsi que leur statut (propriétaire ou non).
 *
 * @property userLogin Identifiant (login) de l'utilisateur
 * @property owner Indique si l'utilisateur est le propriétaire de la maison (1 = oui, 0 = non)
 */
data class UserHouseAccessData(
    val userLogin: String,
    val owner: Int
)
