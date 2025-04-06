package com.kemalkut.projet.ui.activities.house

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R
import com.kemalkut.projet.api.Api
import com.kemalkut.projet.model.user.UserAccessRequestData
import com.kemalkut.projet.model.user.UserHouseAccessData
import com.kemalkut.projet.ui.adapters.UsersAdapter

/**
 * HouseUsersActivity — Gère les utilisateurs ayant accès à une maison spécifique.
 *
 * Fonctionnalités :
 * - Affiche les utilisateurs actuels d’une maison.
 * - Permet d’ajouter un utilisateur à la maison via un champ de recherche.
 * - Permet de retirer l’accès à un utilisateur (hors propriétaire).
 */
class HouseUsersActivity : AppCompatActivity() {

    private var houseId: Int = -1
    private lateinit var token: String

    private lateinit var textViewTitle: TextView
    private lateinit var listViewUsers: ListView
    private lateinit var autoCompleteUserSearch: AutoCompleteTextView
    private lateinit var buttonAddUser: Button

    private val users: MutableList<UserHouseAccessData> = mutableListOf()
    private lateinit var usersAdapter: UsersAdapter
    private var allUserLogins: List<String> = emptyList()

    /**
     * Initialisation de l’activité, vérifie les données et configure les vues.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_users)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = intent.getStringExtra("TOKEN") ?: ""
        houseId = intent.getIntExtra("HOUSE_ID", -1)

        if (houseId == -1 || token.isEmpty()) {
            Toast.makeText(this, "Données manquantes", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            initViews()
        }

    }

    /**
     * Initialise les composants de l’interface.
     */
    private fun initViews() {
        textViewTitle = findViewById(R.id.textViewUsersTitle)
        listViewUsers = findViewById(R.id.listViewUsers)
        autoCompleteUserSearch = findViewById(R.id.autoCompleteUserSearch)
        buttonAddUser = findViewById(R.id.buttonAddUser)

        textViewTitle.text = "Utilisateurs de la maison $houseId"

        fetchUsersForHouse()
        fetchAllUsers()
    }

    /**
     * Récupère les utilisateurs ayant accès à la maison.
     */
    private fun fetchUsersForHouse() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"
        Api().get<List<UserHouseAccessData>>(url, ::fetchUsersForHouseSuccess, token)
    }

    /**
     * Callback après récupération des utilisateurs de la maison.
     */
    private fun fetchUsersForHouseSuccess(responseCode: Int, userList: List<UserHouseAccessData>?) {
        runOnUiThread {
            if (responseCode == 200 && userList != null) {
                users.clear()
                users.addAll(userList)
                usersAdapter = UsersAdapter(this, users, ::confirmUserRemoval)
                listViewUsers.adapter = usersAdapter
            } else {
                Toast.makeText(this, "Erreur chargement utilisateurs (code: $responseCode)", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Récupère tous les utilisateurs existants (pour l'autocomplétion).
     */
    private fun fetchAllUsers() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/users"
        Api().get<List<Map<String, String>>>(url, ::fetchAllUsersSuccess, token)
    }

    /**
     * Callback après récupération des logins de tous les utilisateurs.
     */
    private fun fetchAllUsersSuccess(code: Int, response: List<Map<String, String>>?) {
        runOnUiThread {
            if (code == 200 && response != null) {
                allUserLogins = response.mapNotNull { it["login"] }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, allUserLogins)
                autoCompleteUserSearch.setAdapter(adapter)
                autoCompleteUserSearch.threshold = 1
            } else {
                Toast.makeText(this, "Erreur chargement des utilisateurs disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Donne l’accès à un utilisateur à la maison.
     *
     * @param login Login de l'utilisateur à ajouter.
     */
    private fun giveUserAccess(login: String) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"
        val data = UserAccessRequestData(login)

        Api().post(url, data, ::giveUserAccessSuccess, token)
    }

    /**
     * Callback : succès ou erreur lors de l'ajout d’un utilisateur à la maison.
     */
    private fun giveUserAccessSuccess(responseCode: Int) {
        runOnUiThread {
            when (responseCode) {
                200 -> {
                    autoCompleteUserSearch.setText("")
                    fetchUsersForHouse()
                    Toast.makeText(this, "Accès donné", Toast.LENGTH_SHORT).show()
                }
                400 -> Toast.makeText(this, "L'utilisateur entré n'existe pas", Toast.LENGTH_SHORT).show()
                404 -> Toast.makeText(this, "Non autorisé", Toast.LENGTH_SHORT).show()
                409 -> Toast.makeText(this, "L'utilisateur entré a déjà accès", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Affiche un dialogue de confirmation avant suppression d’un utilisateur.
     */
    private fun confirmUserRemoval(userLogin: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Supprimer l'utilisateur $userLogin de cette maison ?")
            .setPositiveButton("Oui") { _, _ -> removeUserAccess(userLogin) }
            .setNegativeButton("Non", null)
            .show()
    }

    /**
     * Supprime l’accès d’un utilisateur à la maison (via DELETE avec corps).
     */
    private fun deleteUserAccessWithBody(login: String, callback: (Int) -> Unit) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"
        val body = UserAccessRequestData(login)
        Api().request(url, "DELETE", callback, body, token)
    }

    /**
     * Gère la suppression d’accès d’un utilisateur.
     */
    private fun removeUserAccess(userLogin: String) {
        if (userLogin.isBlank()) {
            Toast.makeText(this, "Login invalide", Toast.LENGTH_SHORT).show()
            return
        }

        deleteUserAccessWithBody(userLogin, ::removeUserAccessSuccess)
    }

    /**
     * Callback après suppression d’un accès utilisateur.
     */
    private fun removeUserAccessSuccess(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                fetchUsersForHouse()
                Toast.makeText(this, "Accès supprimé", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur serveur (code $responseCode)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Action du bouton "Ajouter" : envoie l’invitation si un login est entré.
     */
    fun onAddUserClick(view: View) {
        val login = autoCompleteUserSearch.text.toString().trim()
        if (login.isNotEmpty()) {
            giveUserAccess(login)
        } else {
            Toast.makeText(this, "Veuillez saisir un login", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Gère le bouton retour (ImageButton).
     */
    fun btnbackkk() {
        finish()
    }
}