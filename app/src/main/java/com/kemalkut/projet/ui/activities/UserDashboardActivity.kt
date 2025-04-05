package com.kemalkut.projet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R
import com.kemalkut.projet.api.Api
import com.kemalkut.projet.model.device.DeviceData
import com.kemalkut.projet.model.device.DevicesResponseData
import com.kemalkut.projet.model.house.HouseData
import com.kemalkut.projet.ui.adapters.HouseAdapter

/**
 * Activité affichant le tableau de bord de l'utilisateur.
 * Elle récupère la liste des maisons associées au token,
 * affiche la maison propriétaire dans une zone dédiée et
 * liste les maisons partagées dans une ListView.
 */
class UserDashboardActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var listViewSharedHouses: ListView

    private lateinit var ownerHouseTitle: TextView
    private lateinit var devices: List<DeviceData>
    private lateinit var deviceCount: TextView

    private var ownerHouse: HouseData? = null
    private val sharedHouses = ArrayList<HouseData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = intent.getStringExtra("TOKEN") ?: ""
        if (token.isEmpty()) {
            Toast.makeText(this, "Token non dispo", Toast.LENGTH_LONG).show()
            finish()
        }

        listViewSharedHouses = findViewById(R.id.listViewHouses)
        ownerHouseTitle = findViewById(R.id.ownerHouseTitle)
        deviceCount = findViewById(R.id.deviceCount)

        fetchHouses()
    }

    private fun fetchHouses() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses"

        Api().get<List<HouseData>>(url, ::fetchHousesSuccess, token)
    }

    private fun fetchHousesSuccess(responseCode: Int, houses: List<HouseData>?) {
        runOnUiThread {
            if (responseCode == 200 && houses != null) {
                sharedHouses.clear()
                ownerHouse = null

                for (house in houses) {
                    if (house.owner) {
                        ownerHouse = house
                        fetchDevicesCount(house.houseId)
                    } else {
                        sharedHouses.add(house)
                    }
                }
                ownerHouseTitle.text = "Maison ${ownerHouse?.houseId}"

                val adapter = HouseAdapter(this, sharedHouses)
                listViewSharedHouses.adapter = adapter
            } else {
                Toast.makeText(this, "Erreur lors du chargement des maisons (code: $responseCode)", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Récupère la liste des périphériques pour une maison donnée et met à jour le TextView correspondant.
     * On compte le nombre d'éléments renvoyés par l'API.
     */
    private fun fetchDevicesCount(houseId: Int) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/${houseId}/devices"

        Api().get<DevicesResponseData>(url, ::fetchDevicesCountSuccess, token)
    }

    private fun fetchDevicesCountSuccess(responseCode: Int, devicesResponse: DevicesResponseData?) {
        runOnUiThread {
            if (responseCode == 200 && devicesResponse != null) {
                devices = devicesResponse.devices
                deviceCount.text = "${devices.size} périphériques connectés."
            } else {
                deviceCount.text = "Veuillez ouvrir la maison dans un navigateur"
                Toast.makeText(this, "La maison n'est pas ouverte dans un navigateur", Toast.LENGTH_LONG).show()
            }
        }
    }

    public fun handleHouse(view: View) {
        if (ownerHouse != null && token.isNotEmpty() && ::devices.isInitialized) {
            val intent = Intent(this, HouseActivity::class.java)
            intent.putExtra("HOUSE_ID", ownerHouse!!.houseId)
            intent.putExtra("TOKEN", token)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Aucune maison propriétaire trouvée. Ouvrez la dans un naviguateur", Toast.LENGTH_SHORT).show()
        }
    }

    public fun refresh(view: View) {
        ownerHouse?.let { fetchDevicesCount(it.houseId) }
    }

    public fun msg(view: View) {
        Toast.makeText(this, "Déconnexion impossible -> fonctionnalité non dispo depuis l'API", Toast.LENGTH_SHORT).show()
    }
}