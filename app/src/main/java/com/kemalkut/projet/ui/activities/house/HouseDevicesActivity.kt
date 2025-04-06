package com.kemalkut.projet.ui.activities.house

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R
import com.kemalkut.projet.api.Api
import com.kemalkut.projet.model.device.CommandData
import com.kemalkut.projet.model.device.DeviceData
import com.kemalkut.projet.model.device.DevicesResponseData
import com.kemalkut.projet.ui.activities.devices.HouseDeviceGarageActivity
import com.kemalkut.projet.ui.activities.devices.HouseDeviceLightsActivity
import com.kemalkut.projet.ui.activities.devices.HouseDeviceShuttersActivity

/**
 * HouseDevicesActivity — Gère les interactions avec les périphériques (lumières, volets, garage).
 *
 * Cette activité permet de :
 * - Accéder à des sous-activités spécifiques : lumières, volets roulants, garage.
 * - Exécuter des modes globaux (jour, nuit, fun).
 * - Gérer les commandes groupées par étage.
 *
 * L'activité récupère la liste des périphériques via une requête API.
 */
class HouseDevicesActivity : AppCompatActivity() {

    private var houseId = -1
    private lateinit var token: String
    private var devices: List<DeviceData> = emptyList()

    /**
     * Initialise l'activité et déclenche le chargement des périphériques.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_devices)

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
            fetchDevices()
        }
    }

    public fun goBack(view: View) {
        finish()
    }

    /**
     * Envoie une requête à l’API pour récupérer les périphériques de la maison.
     */
    private fun fetchDevices() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Api().get<DevicesResponseData>(url, ::onDevicesFetched, token)
    }

    /**
     * Callback : succès ou erreur après récupération des périphériques.
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
     * Ouvre la sous-activité dédiée au garage.
     */
    fun onButtonGarageClic(view: View) {
        val intent = Intent(this, HouseDeviceGarageActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    /**
     * Ouvre la sous-activité dédiée aux lumières.
     */
    fun onButtonLightsClic(view: View) {
        val intent = Intent(this, HouseDeviceLightsActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    /**
     * Ouvre la sous-activité dédiée aux volets roulants.
     */
    fun onButtonShuttersClic(view: View) {
        val intent = Intent(this, HouseDeviceShuttersActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    /**
     * Active le mode jour : ouvre tous les volets + allume toutes les lumières.
     */
    fun onButtonModeJour(view: View) {
        for (device in devices) {
            if (device.type == "rolling shutter" && device.availableCommands.contains("OPEN")) {
                sendCommand(device, "OPEN")
            }
            if (device.type == "light" && device.availableCommands.contains("TURN ON")) {
                sendCommand(device, "TURN ON")
            }
        }
        Toast.makeText(this, "Mode jour activé", Toast.LENGTH_SHORT).show()
    }

    /**
     * Active le mode nuit : ferme tous les volets + éteint toutes les lumières.
     */
    fun onButtonModeNuit(view: View) {
        for (device in devices) {
            if (device.type == "rolling shutter" && device.availableCommands.contains("CLOSE")) {
                sendCommand(device, "CLOSE")
            }
            if (device.type == "light" && device.availableCommands.contains("TURN OFF")) {
                sendCommand(device, "TURN OFF")
            }
        }
        Toast.makeText(this, "Mode nuit activé", Toast.LENGTH_SHORT).show()
    }

    /**
     * Ouvre volets + lumières de l'étage 1.
     */
    fun onOuvrirEtage1(view: View) {
        gererEtage(1, allumer = true, ouvrir = true)
    }

    /**
     * Ferme volets + lumières de l'étage 1.
     */
    fun onFermerEtage1(view: View) {
        gererEtage(1, allumer = false, ouvrir = false)
    }

    /**
     * Ouvre volets + lumières de l'étage 2.
     */
    fun onOuvrirEtage2(view: View) {
        gererEtage(2, allumer = true, ouvrir = true)
    }

    /**
     * Ferme volets + lumières de l'étage 2.
     */
    fun onFermerEtage2(view: View) {
        gererEtage(2, allumer = false, ouvrir = false)
    }

    /**
     * Applique une commande d’ouverture/fermeture + allumage/extinction pour un étage donné.
     */
    private fun gererEtage(etage: Int, allumer: Boolean, ouvrir: Boolean) {
        val lights = devices.filter { it.type == "light" && it.id.contains("$etage.") }
        val shutters = devices.filter { it.type == "rolling shutter" && it.id.contains("$etage.") }

        val cmdLum = if (allumer) "TURN ON" else "TURN OFF"
        val cmdVolet = if (ouvrir) "OPEN" else "CLOSE"

        for (light in lights) {
            if (light.availableCommands.contains(cmdLum)) {
                sendCommand(light, cmdLum)
            }
        }

        for (shutter in shutters) {
            if (shutter.availableCommands.contains(cmdVolet)) {
                sendCommand(shutter, cmdVolet)
            }
        }

        Toast.makeText(this, "Commande envoyée pour l’étage $etage", Toast.LENGTH_SHORT).show()
    }

    /**
     * Envoie une commande API à un périphérique spécifique.
     */
    private fun sendCommand(device: DeviceData, command: String) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/${device.id}/command"
        val body = CommandData(command)
        Api().post(url, body, { code -> onCommandResponse(code, device.id, command) }, token)
    }

    /**
     * Callback d’erreur si une commande a échoué.
     */
    private fun onCommandResponse(code: Int, deviceId: String, command: String) {
        runOnUiThread {
            if (code != 200) {
                Toast.makeText(this, "Erreur $command sur $deviceId", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Active le mode fun : animations de volets/lumières pendant 10 secondes.
     */
    fun onButtonFunModeClic(view: View) {
        val allDevices = devices.filter {
            it.type == "light" || it.type == "rolling shutter"
        }

        if (allDevices.isEmpty()) {
            Toast.makeText(this, "Aucun périphérique pour le mode fun", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Mode Fun activé pendant 10 secondes !", Toast.LENGTH_SHORT).show()

        Thread {
            repeat(5) {
                runOnUiThread {
                    for (device in allDevices) {
                        val command = if (device.type == "light" && device.availableCommands.contains("TURN ON")) {
                            "TURN ON"
                        } else if (device.type == "rolling shutter" && device.availableCommands.contains("OPEN")) {
                            "OPEN"
                        } else null

                        if (command != null) {
                            sendCommand(device, command)
                        }
                    }
                }
                Thread.sleep(1000)

                runOnUiThread {
                    for (device in allDevices) {
                        val command = if (device.type == "light" && device.availableCommands.contains("TURN OFF")) {
                            "TURN OFF"
                        } else if (device.type == "rolling shutter" && device.availableCommands.contains("CLOSE")) {
                            "CLOSE"
                        } else null

                        if (command != null) {
                            sendCommand(device, command)
                        }
                    }
                }
                Thread.sleep(1000)
            }
        }.start()
    }
}
