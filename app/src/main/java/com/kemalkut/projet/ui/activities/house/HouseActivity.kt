package com.kemalkut.projet.ui.activities.house

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

/**
 * HouseActivity — Activité permettant de gérer une maison spécifique.
 *
 * Cette activité affiche un menu avec deux options :
 * - Gérer les utilisateurs de la maison.
 * - Gérer les périphériques de la maison.
 *
 */
class HouseActivity : AppCompatActivity() {

    private var houseId: Int = -1
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        houseId = intent.getIntExtra("HOUSE_ID", -1)
        token = intent.getStringExtra("TOKEN") ?: ""

        if (houseId == -1 || token.isEmpty()) {
            Toast.makeText(this, "Données manquantes", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            setupHouseTextView()
        }
    }

    /**
     * Met à jour le titre de la maison affiché à l'écran.
     */
    private fun setupHouseTextView() {
        findViewById<TextView>(R.id.houseTitle).text = "Maison $houseId"
    }

    /**
     * Lance l'activité de gestion des utilisateurs de la maison.
     *
     * @param view La vue qui déclenche l'action (bouton "Gérer les utilisateurs").
     */
    fun onManageUsersClick(view: View) {
        val intent = Intent(this, HouseUsersActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    /**
     * Lance l'activité de gestion des périphériques de la maison.
     *
     * @param view La vue qui déclenche l'action (bouton "Gérer les périphériques").
     */
    fun onManageDevicesClick(view: View) {
        val intent = Intent(this, HouseDevicesActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    /**
     * Ferme cette activité et retourne à l'écran précédent.
     *
     * @param view La vue déclenchant l'action (bouton retour).
     */
    fun btnBack3(view: View) {
        finish()
    }
}
