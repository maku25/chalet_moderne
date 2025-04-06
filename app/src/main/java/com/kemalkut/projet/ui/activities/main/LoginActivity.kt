package com.kemalkut.projet.ui.activities.main

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
import com.kemalkut.projet.ui.activities.dashboard.UserDashboardActivity

// TODO : Vérifier que le login et le mot de passe contiennent uniquement des chiffres et lettres.

/**
 * `LoginActivity` — Écran de connexion utilisateur.
 *
 * Permet à l'utilisateur d'entrer son login et mot de passe,
 * puis envoie une requête POST à l'API pour obtenir un token d'authentification.
 * En cas de succès, redirige vers le dashboard utilisateur.
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
     * Ferme cette activité et retourne à l'écran précédent.
     *
     */
    fun backHome1(view: View) {
        finish()
    }

    /**
     * Redirige vers l'écran d'inscription (`RegisterActivity`).
     *
     */
    fun goToRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    /**
     * Tente de connecter l'utilisateur avec les identifiants saisis.
     * Vérifie que les champs sont remplis et valides,
     * puis appelle l'API d'authentification.
     *
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
     * Callback appelé en réponse à l'authentification.
     *
     * @param responseCode Code HTTP de la réponse.
     * @param authResponse Objet contenant le token si la réponse est valide.
     */
    fun loginSuccess(responseCode: Int, authResponse: AuthResponseData?) {
        runOnUiThread {
            if (responseCode == 200 && authResponse != null) {
                val token = authResponse.token
                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserDashboardActivity::class.java)
                intent.putExtra("TOKEN", token)
                startActivity(intent)
                finish()
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
