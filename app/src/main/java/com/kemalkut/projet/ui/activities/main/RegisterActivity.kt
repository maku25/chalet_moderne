package com.kemalkut.projet.ui.activities.main

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
import com.kemalkut.projet.model.user.UserData

// TODO : Ajouter une vérification des caractères (uniquement chiffres et lettres) pour le login et le mot de passe

/**
 * `RegisterActivity` — Écran d'inscription pour les nouveaux utilisateurs.
 *
 * Permet à l'utilisateur de créer un compte en saisissant un login et un mot de passe,
 * puis envoie les données au backend via une requête POST.
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
     * Ferme l'écran actuel et retourne à la page précédente (MainActivity).
     *
     */
    fun backHome2(view: View) {
        finish()
    }

    /**
     * Redirige l'utilisateur vers l'écran de connexion.
     *
     */
    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Gère le clic sur le bouton d'inscription.
     *
     * Vérifie les champs saisis (login & mot de passe), puis envoie la requête d'inscription à l'API.
     *
     */
    fun register(view: View) {
        val loginView = findViewById<EditText>(R.id.editTextLoginRegister)
        val passwordView = findViewById<EditText>(R.id.editTextPasswordRegister)

        val loginText = loginView.text.toString()
        val passwordText = passwordView.text.toString()

        if (loginText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (passwordText.length < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
            return
        }

        val newUser = UserData(loginText, passwordText)

        Api().post("https://polyhome.lesmoulinsdudev.com/api/users/register", newUser, ::registerSuccess)
    }

    /**
     * Callback déclenché après la tentative d'inscription.
     *
     * Gère les cas d'erreur classiques : conflit (409), mauvaise requête (400), erreur serveur (500).
     *
     * @param responseCode Code HTTP de réponse renvoyé par le serveur.
     */
    private fun registerSuccess(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                finish()
            } else if (responseCode == 409) {
                Toast.makeText(this, "Login déjà utilisé", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 400) {
                Toast.makeText(this, "Données incorrectes", Toast.LENGTH_LONG).show()
            } else if (responseCode == 500) {
                Toast.makeText(
                    this, "Une erreur s'est produite au niveau du serveur", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Erreur lors de l'inscription, code: $responseCode", Toast.LENGTH_LONG).show()
            }
        }
    }
}
