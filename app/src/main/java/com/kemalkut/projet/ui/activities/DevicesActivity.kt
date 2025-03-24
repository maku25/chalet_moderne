package com.kemalkut.projet.ui.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R
import com.kemalkut.projet.model.DeviceData

/**
 * DevicesActivity affiche la liste des périphériques de la maison.
 *
 * Elle récupère le token d'authentification transmis par l'activité de connexion,
 * appelle l'API pour obtenir la liste des périphériques, puis les affiche dans une ListView.
 */
class DevicesActivity : AppCompatActivity() {

    //private val devices: ArrayList<DeviceData> = ArrayList()

    //private lateinit var devicesAdapter: ArrayAdapter<DeviceData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_devices)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //devicesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devices)
        //val listDevices = findViewById<ListView>(R.id.listDevices)

        //listDevices.adapter = devicesAdapter
        //loadDevices()
    }

    /**
     * Charge la liste des périphériques en appelant l'API.
    private fun loadDevices() {
        val token = intent.getStringExtra("TOKEN")
        if (token == null) {
            Toast.makeText(this, "Token introuvable", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/1/devices"
        Api().get<DeviceResponse>(
            url,
            ::loadDevicesSuccess,
            token
        )
    }
     */


    /**
     * Callback appelé suite à la réponse de l'API pour le chargement des périphériques.
     *
     * @param responseCode Le code HTTP de la réponse.
     * @param deviceResponse L'objet DeviceResponse contenant la liste des périphériques, ou null en cas d'erreur.
    fun loadDevicesSuccess(responseCode: Int, deviceResponse: DeviceResponse?) {
        runOnUiThread {
            if (responseCode == 200 && deviceResponse != null) {
                // Mise à jour de la liste des périphériques et de l'adaptateur
                devices.clear()
                devices.addAll(deviceResponse.devices)
                devicesAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Erreur lors du chargement des périphériques (code: $responseCode)", Toast.LENGTH_LONG).show()
            }
        }
    }
     */
}