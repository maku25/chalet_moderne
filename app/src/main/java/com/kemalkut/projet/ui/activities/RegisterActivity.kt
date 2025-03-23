package com.kemalkut.projet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.api.Api
import com.kemalkut.projet.R
import com.kemalkut.projet.model.UserData

// A FAIRE !!!!
// Vérification des caractères -> uniquement des chiffres et des lettres pour le login et le mdp

/**
 * RegisterActivity gère l'inscription des nouveaux utilisateurs.
 * Elle permet de saisir un login et un mot de passe, effectue des vérifications de base,
 * et envoie ensuite les données au serveur via l'API.
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Ferme l'activité actuelle et revient à l'écran précédent.
     *
     * @param view La vue déclenchant cet événement (bouton "Retour").
     */
    public fun backHome2(view: View) {
        finish()
    }

    /**
     * Lance l'activité de connexion (LoginActivity).
     *
     * @param view La vue déclenchant cet événement (bouton "Se connecter").
     */
    public fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Effectue l'inscription d'un nouvel utilisateur.
     * Vérifie que les champs de login et mot de passe sont correctement remplis,
     * puis envoie les données au serveur via l'API.
     *
     * @param view La vue déclenchant cet événement (bouton "S'inscrire").
     */
    public fun register(view: View) {
        // Récupère les champs de saisie
        val loginView = findViewById<EditText>(R.id.editTextLoginRegister)
        val passwordView = findViewById<EditText>(R.id.editTextPasswordRegister)

        // Extrait le texte saisi
        val loginText = loginView.text.toString()
        val passwordText = passwordView.text.toString()

        // Vérifie que les deux champs ne sont pas vides
        if (loginText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }
        // Vérifie que le mot de passe contient au moins 6 caractères
        if (passwordText.length < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
            return
        }

        // Crée un objet UserData avec les informations saisies
        val newUser = UserData(loginText, passwordText)

        // Envoie les données d'inscription à l'API
        Api().post<UserData>(
            "https://polyhome.lesmoulinsdudev.com/api/users/register",
            newUser,
            ::registerSuccess
        )
    }

    /**
     * Callback appelé après la tentative d'inscription.
     * Affiche un message en fonction du code de réponse reçu.
     *
     * @param responseCode Le code de réponse HTTP renvoyé par le serveur.
     */
    private fun registerSuccess(responseCode: Int) {
        runOnUiThread {
            when(responseCode) {
                200 -> {
                    Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                    finish()
                }
                409 -> Toast.makeText(this, "Login déjà utilisé", Toast.LENGTH_LONG).show()
                400 -> Toast.makeText(this, "Données incorrectes", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Erreur lors de l'inscription, code: $responseCode", Toast.LENGTH_LONG).show()
            }
        }
    }
}