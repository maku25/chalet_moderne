package com.kemalkut.projet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R
import com.kemalkut.projet.api.Api
import com.kemalkut.projet.model.user.AuthResponseData
import com.kemalkut.projet.model.user.UserData

// A FAIRE !!!!
// Vérification des caractères -> uniquement des chiffres et des lettres pour le login et le mdp

/**
 * LoginActivity gère la connexion de l'utilisateur.
 *
 * Elle permet à l'utilisateur de saisir ses identifiants (login et mot de passe),
 * de valider les données saisies, puis d'envoyer une requête d'authentification à l'API.
 * En cas de succès, l'utilisateur est redirigé vers l'activité affichant les périphériques (DevicesActivity),
 * et le token d'authentification est transmis.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
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
    fun backHome1(view: View) {
        finish()
    }

    /**
     * Lance l'activité d'inscription (RegisterActivity).
     *
     * @param view La vue déclenchant l'événement (bouton "S'inscrire").
     */
    fun goToRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    /**
     * Tente de connecter l'utilisateur.
     *
     * Récupère les identifiants saisis, vérifie que les champs ne sont pas vides
     * et que le mot de passe respecte la longueur minimale, puis envoie une requête d'authentification via l'API.
     *
     * @param view La vue déclenchant l'événement (bouton "Se connecter").
     */
    fun login(view: View) {
        val loginView = findViewById<TextView>(R.id.editTextLoginLogin)
        val passwordView = findViewById<TextView>(R.id.editTextPasswordLogin)
        val loginText = loginView.text.toString().trim()
        val passwordText = passwordView.text.toString().trim()

        if (loginText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (passwordText.length < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
            return
        }

        val user = UserData(loginText, passwordText)
        Api().post<UserData, AuthResponseData>(
            "https://polyhome.lesmoulinsdudev.com/api/users/auth",
            user,
            ::loginSuccess
        )
    }

    /**
     * Callback appelé suite à la réponse de l'API d'authentification.
     *
     * En fonction du code de réponse, cette méthode :
     * - Si la connexion est réussie (code 200) et l'objet AuthResponse est valide,
     *   elle extrait le token, affiche un message de succès, puis redirige l'utilisateur vers DevicesActivity.
     * - Si le code de réponse est 404, cela signifie que le login est inconnu.
     * - Si le code de réponse est 400, cela signifie que les données envoyées sont incorrectes.
     * - Pour tout autre code, un message d'erreur générique est affiché.
     *
     * @param responseCode Le code HTTP renvoyé par le serveur.
     * @param authResponse L'objet AuthResponseData contenant le token d'authentification, ou null si la réponse est invalide.
     */
    fun loginSuccess(responseCode: Int, authResponse: AuthResponseData?) {
    runOnUiThread {
            if (responseCode == 200) {
                if (authResponse != null) {
                    val token = authResponse.token
                    Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UserDashboardActivity::class.java)
                    intent.putExtra("TOKEN", token)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Erreur lors de la connexion, code: $responseCode", Toast.LENGTH_LONG).show()
                }
            } else if (responseCode == 404) {
                Toast.makeText(this, "Login inconnu", Toast.LENGTH_LONG).show()
            } else if (responseCode == 400) {
                Toast.makeText(this, "Données incorrectes", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Erreur lors de la connexion, code: $responseCode", Toast.LENGTH_LONG).show()
            }
        }
    }
}