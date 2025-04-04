package com.kemalkut.projet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R

//user123
//user456
//motdepasse

//https://polyhome.lesmoulinsdudev.com/?houseId=505

/**
 * MainActivity: page d'accueil de l'application.
 * Elle permet à l'utilisateur de choisir de se connecter ou de s'inscrire.
 *
 *
 * github du prof cmeunier-ub
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Lance l'activité de connexion.
     *
     * @param view La vue qui déclenche l'événement de clic (bouton de connexion).
     */
    public fun goToLoginIntent(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Lance l'activité d'inscription.
     *
     * @param view La vue qui déclenche l'événement de clic (bouton d'inscription).
     */
    public fun goToRegisterIntent(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}