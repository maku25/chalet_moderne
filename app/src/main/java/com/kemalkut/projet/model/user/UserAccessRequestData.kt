package com.kemalkut.projet.model.user

/**
 * Requête envoyée à l'API pour donner l'accès à une maison à un utilisateur.
 *
 * Utilisée dans le corps d'une requête POST vers `/api/houses/{houseId}/users`.
 *
 * @property userLogin Identifiant (login) de l'utilisateur à qui on souhaite donner l'accès
 */
data class UserAccessRequestData(
    val userLogin: String
)
