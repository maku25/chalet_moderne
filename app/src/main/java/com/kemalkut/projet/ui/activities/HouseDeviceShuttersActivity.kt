package com.kemalkut.projet.ui.activities

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

class HouseDeviceShuttersActivity : AppCompatActivity() {

    private var houseId = -1
    private lateinit var token: String
    private var devices: List<DeviceData> = emptyList()

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

    private fun fetchShutters() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Api().get<DevicesResponseData>(url, ::onDevicesFetched, token)
    }

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

    public fun onButtonShutterUpClic(view: View) {
        val lights = devices.filter { it.type == "rolling shutter" }
        if (lights.isEmpty()) {
            Toast.makeText(this, "Aucune lumière trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        lights.forEach {
            val command = "OPEN"
            sendCommand(it.id, command, ::onShutterCommandResult)
        }
    }

    public fun onButtonShutterDownClic(view: View) {
        val lights = devices.filter { it.type == "rolling shutter" }
        if (lights.isEmpty()) {
            Toast.makeText(this, "Aucune lumière trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        lights.forEach {
            val command = "CLOSE"
            sendCommand(it.id, command, ::onShutterCommandResult)
        }
    }

    public fun onButtonShutterStopClic(view: View) {
        val lights = devices.filter { it.type == "rolling shutter" }
        if (lights.isEmpty()) {
            Toast.makeText(this, "Aucune lumière trouvée", Toast.LENGTH_SHORT).show()
            return
        }
        lights.forEach {
            val command = "STOP"
            sendCommand(it.id, command, ::onShutterCommandResult)
        }
    }

    private fun onShutterCommandResult(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Commande envoyée - LUMIÈRES", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de l'envoi de la commande", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendCommand(deviceId: String, command: String, callback: (Int) -> Unit) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        val body = CommandData(command)
        Api().post(url, body, callback, token)
    }
}