package com.kemalkut.projet.model.house

/**
 * Représente une maison à laquelle un utilisateur a accès.
 *
 * @property houseId Identifiant de la maison
 * @property owner Indique si l'utilisateur est propriétaire de la maison
 */
data class HouseData(
    val houseId: Int,
    val owner: Boolean
)
