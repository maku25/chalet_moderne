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
 * HouseDeviceShuttersActivity — Activité dédiée à la gestion des volets roulants de la maison.
 *
 * Elle permet :
 * - de récupérer la liste des volets via l’API,
 * - d’envoyer des commandes globales : ouverture, fermeture ou arrêt de tous les volets.
 *
 * L'utilisateur peut interagir via trois boutons :
 * - Monter tous les volets (OPEN)
 * - Descendre tous les volets (CLOSE)
 * - Stopper les volets (STOP)
 */
class HouseDeviceShuttersActivity : AppCompatActivity() {

    private var houseId = -1
    private lateinit var token: String
    private var devices: List<DeviceData> = emptyList()

    /**
     * Méthode de cycle de vie appelée à la création de l’activité.
     * Récupère les données et lance la récupération des volets.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_device_shutters)

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
            fetchShutters()
        }
    }

    /**
     * Récupère tous les périphériques de la maison (dont les volets).
     */
    private fun fetchShutters() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Api().get<DevicesResponseData>(url, ::onDevicesFetched, token)
    }

    /**
     * Callback appelé à la réception des données de périphériques.
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
     * Envoie la commande "OPEN" à tous les volets roulants de la maison.
     */
    fun onButtonShutterUpClic(view: View) {
        val shutters = devices.filter { it.type == "rolling shutter" }
        if (shutters.isEmpty()) {
            Toast.makeText(this, "Aucun volet trouvé", Toast.LENGTH_SHORT).show()
            return
        }
        shutters.forEach {
            sendCommand(it.id, "OPEN", ::onShutterCommandResult)
        }
    }

    /**
     * Envoie la commande "CLOSE" à tous les volets roulants de la maison.
     */
    fun onButtonShutterDownClic(view: View) {
        val shutters = devices.filter { it.type == "rolling shutter" }
        if (shutters.isEmpty()) {
            Toast.makeText(this, "Aucun volet trouvé", Toast.LENGTH_SHORT).show()
            return
        }
        shutters.forEach {
            sendCommand(it.id, "CLOSE", ::onShutterCommandResult)
        }
    }

    /**
     * Envoie la commande "STOP" à tous les volets roulants de la maison.
     */
    fun onButtonShutterStopClic(view: View) {
        val shutters = devices.filter { it.type == "rolling shutter" }
        if (shutters.isEmpty()) {
            Toast.makeText(this, "Aucun volet trouvé", Toast.LENGTH_SHORT).show()
            return
        }
        shutters.forEach {
            sendCommand(it.id, "STOP", ::onShutterCommandResult)
        }
    }

    /**
     * Callback appelée après envoi d'une commande à un volet.
     *
     * @param responseCode Le code HTTP de réponse.
     */
    private fun onShutterCommandResult(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Commande envoyée", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de l'envoi de la commande", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Envoie une commande à un périphérique spécifique.
     *
     * @param deviceId L’identifiant du périphérique.
     * @param command La commande à envoyer (OPEN, CLOSE, STOP).
     * @param callback Fonction de retour après traitement de la requête.
     */
    private fun sendCommand(deviceId: String, command: String, callback: (Int) -> Unit) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        val body = CommandData(command)
        Api().post(url, body, callback, token)
    }

    public fun goBack(view: View) {
        finish()
    }
}
