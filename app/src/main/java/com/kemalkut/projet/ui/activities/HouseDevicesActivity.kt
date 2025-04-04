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
import java.net.URLEncoder

class HouseDevicesActivity : AppCompatActivity() {

    private var houseId: Int = -1
    private lateinit var token: String
    private var garageDoor: DeviceData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_house_devices)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = intent.getStringExtra("TOKEN") ?: ""
        houseId = intent.getIntExtra("HOUSE_ID", -1)

        if (token.isEmpty() || houseId == -1) {
            Toast.makeText(this, "Données manquantes", Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }

    private fun recupererGarage() {
        println("Appel à l'API pour récupérer les périphériques...")
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
        Api().get<DevicesResponseData>(url, ::onDevicesResponse, token)
    }

    private fun onDevicesResponse(code: Int, response: DevicesResponseData?) {
        runOnUiThread {
            println("Réponse de l'API reçue avec code: $code")
            if (code == 200 && response != null) {
                println("Nombre de périphériques récupérés: ${response.devices.size}")
                Toast.makeText(this, "${response.devices.size} périphériques récupérés", Toast.LENGTH_SHORT).show()

                val garage = response.devices.find { it.type == "garage door" }
                if (garage != null) {
                    println("Périphérique garage trouvé: ${garage.id}")
                    Toast.makeText(this, "Garage récupéré", Toast.LENGTH_SHORT).show()
                    garageDoor = garage
                    handleGarageDoor()
                } else {
                    println("Aucun périphérique de type garage door trouvé.")
                    Toast.makeText(this, "Aucun périphérique garage", Toast.LENGTH_SHORT).show()
                }
            } else {
                println("Erreur récupération périphériques: code=$code")
                Toast.makeText(this, "Erreur récupération périphériques (code: $code)", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onGarageButtonClic(view: View) {
        println("Bouton garage cliqué.")
        recupererGarage()
    }

    private fun handleGarageDoor() {
        garageDoor?.let { garage ->
            val opening = garage.opening
            println("État actuel du garage (opening): $opening")

            if (opening != null) {
                val encodedId = URLEncoder.encode(garage.id, "UTF-8")
                val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$encodedId/command"

                if (opening >= 0 && opening <= 0.5) {
                    println("Le garage est entrouvert ou fermé, on tente de l'ouvrir.")
                    val body = CommandData("OPEN")
                    Api().post(url, body, ::onOpenDoor, token)
                } else if (opening > 0.5 && opening <= 1) {
                    println("Le garage est ouvert, on tente de le fermer.")
                    val body = CommandData("CLOSE")
                    Api().post(url, body, ::onCloseDoor, token)
                }
            } else {
                println("L'ouverture du garage est null.")
            }
        } ?: run {
            println("garageDoor est null dans handleGarageDoor")
        }
    }

    private fun onCloseDoor(code: Int) {
        runOnUiThread {
            println("Réponse après commande CLOSE: $code")
            if (code == 200) {
                Toast.makeText(this, "Porte fermée avec succès", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Échec de la fermeture (code: $code)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onOpenDoor(code: Int) {
        runOnUiThread {
            println("Réponse après commande OPEN: $code")
            if (code == 200) {
                Toast.makeText(this, "Porte ouverte avec succès", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Échec de l'ouverture (code: $code)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
