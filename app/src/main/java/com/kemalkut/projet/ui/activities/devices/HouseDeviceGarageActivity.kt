package com.kemalkut.projet.ui.activities.devices

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R
import com.kemalkut.projet.api.Api
import com.kemalkut.projet.model.device.CommandData
import com.kemalkut.projet.model.device.DeviceData
import com.kemalkut.projet.model.device.DevicesResponseData

/**
 * HouseDeviceGarageActivity — Activité permettant la gestion d'une porte de garage.
 *
 * Cette activité permet :
 * - de récupérer les périphériques disponibles de la maison,
 * - de repérer celui correspondant à une porte de garage (type "garage door"),
 * - d'envoyer des commandes spécifiques :
 *   - "OPEN" : ouverture de la porte,
 *   - "CLOSE" : fermeture de la porte,
 *   - "STOP" : arrêt de l'action en cours.
 */
class HouseDeviceGarageActivity : AppCompatActivity() {

    private var houseId = -1
    private lateinit var token: String
    private var devices: List<DeviceData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_device_garage)

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
            fetchGarage()
        }
    }

    /**
     * Récupère tous les périphériques de la maison.
     */
    private fun fetchGarage() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Api().get<DevicesResponseData>(url, ::onDevicesFetched, token)
    }

    /**
     * Callback appelée après la récupération des périphériques.
     *
     * @param responseCode Code HTTP reçu.
     * @param response Réponse contenant la liste des périphériques.
     */
    private fun onDevicesFetched(responseCode: Int, response: DevicesResponseData?) {
        runOnUiThread {
            if (responseCode == 200 && response != null) {
                devices = response.devices
                Toast.makeText(this, "${devices.size} périphériques récupérés", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Ouvre la porte du garage si disponible.
     */
    fun onButtonUpGarageClic(view: View) {
        val garage = devices.find { it.type == "garage door" }
        if (garage == null) {
            Toast.makeText(this, "Porte garage non trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        sendCommand(garage.id, "OPEN", ::onGarageCommandResult)
    }

    /**
     * Ferme la porte du garage si disponible.
     */
    fun onButtonDownGarageClic(view: View) {
        val garage = devices.find { it.type == "garage door" }
        if (garage == null) {
            Toast.makeText(this, "Porte garage non trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        sendCommand(garage.id, "CLOSE", ::onGarageCommandResult)
    }

    /**
     * Stoppe le mouvement de la porte du garage si disponible.
     */
    fun onButtonStopGarageClic(view: View) {
        val garage = devices.find { it.type == "garage door" }
        if (garage == null) {
            Toast.makeText(this, "Porte garage non trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        sendCommand(garage.id, "STOP", ::onGarageCommandResult)
    }

    /**
     * Envoie une commande à la porte de garage.
     *
     * @param deviceId ID du périphérique.
     * @param command Commande à exécuter.
     * @param callback Fonction appelée après la réponse.
     */
    private fun sendCommand(deviceId: String, command: String, callback: (Int) -> Unit) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        val body = CommandData(command)
        Api().post(url, body, callback, token)
    }

    /**
     * Callback déclenché après envoi d'une commande au garage.
     *
     * @param responseCode Code HTTP de réponse.
     */
    private fun onGarageCommandResult(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Commande envoyée - GARAGE", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de l'envoi de la commande", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun goBack(view: View) {
        finish()
    }
}