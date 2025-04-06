package com.kemalkut.projet.ui.activities.dashboard

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
import com.kemalkut.projet.ui.activities.house.HouseActivity
import com.kemalkut.projet.ui.activities.house.HouseDevicesActivity
import com.kemalkut.projet.ui.adapters.HouseAdapter

/**
 * `UserDashboardActivity` — écran principal après la connexion.
 *
 * Affiche la maison principale de l'user,
 * ainsi que les maisons partagées.
 */
class UserDashboardActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var listViewSharedHouses: ListView
    private lateinit var ownerHouseTitle: TextView
    private lateinit var deviceCount: TextView
    private lateinit var devices: List<DeviceData>

    private var ownerHouse: HouseData? = null
    private val sharedHouses = ArrayList<HouseData>()
    private val loadedHouseDevices = mutableMapOf<Int, List<DeviceData>>()

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

    /**
     * Récupère la liste des maisons de l'utilisateur.
     */
    private fun fetchHouses() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses"
        Api().get<List<HouseData>>(url, ::onFetchHousesSuccess, token)
    }

    /**
     * Gère la réponse de l'API concernant les maisons.
     */
    private fun onFetchHousesSuccess(responseCode: Int, houses: List<HouseData>?) {
        runOnUiThread {
            if (responseCode == 200 && houses != null) {
                sharedHouses.clear()
                ownerHouse = null
                loadedHouseDevices.clear()

                for (house in houses) {
                    fetchDevicesForHouse(house)
                    if (house.owner) {
                        ownerHouse = house
                    } else {
                        sharedHouses.add(house)
                    }
                }

                ownerHouseTitle.text = "Maison ${ownerHouse?.houseId}"

                val adapter = HouseAdapter(this, sharedHouses) { selectedHouse ->
                    if (loadedHouseDevices.containsKey(selectedHouse.houseId)) {
                        openHouseActivity(selectedHouse.houseId)
                    } else {
                        Toast.makeText(this, "Veuillez ouvrir la maison dans un navigateur", Toast.LENGTH_SHORT).show()
                    }
                }

                listViewSharedHouses.adapter = adapter
            } else {
                Toast.makeText(this, "Erreur chargement maisons (code $responseCode)", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Récupère les périphériques associés à une maison.
     */
    private fun fetchDevicesForHouse(house: HouseData) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/${house.houseId}/devices"
        Api().get<DevicesResponseData>(url, { code, response ->
            onFetchDevicesSuccess(house, code, response)
        }, token)
    }

    /**
     * Gère la réponse contenant les périphériques d'une maison.
     */
    private fun onFetchDevicesSuccess(house: HouseData, responseCode: Int, devicesResponse: DevicesResponseData?) {
        runOnUiThread {
            if (responseCode == 200 && devicesResponse != null) {
                loadedHouseDevices[house.houseId] = devicesResponse.devices
                if (house.owner) {
                    devices = devicesResponse.devices
                    deviceCount.text = "${devices.size} périphériques connectés."
                }
            } else {
                if (house.owner) {
                    deviceCount.text = "Veuillez ouvrir la maison dans un navigateur"
                    Toast.makeText(this, "Erreur : maison propriétaire pas accessible", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Lance l'écran de gestion de la maison propriétaire.
     */
    fun handleHouse(view: View) {
        ownerHouse?.let {
            if (canAccessHouse(it.houseId)) {
                openHouseActivity(it.houseId)
            } else {
                Toast.makeText(this, "Ouvrez-la d'abord dans un navigateur", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Rafraîchit la liste des maisons.
     */
    fun refresh(view: View) {
        Toast.makeText(this, "Rafraîchissement...", Toast.LENGTH_SHORT).show()
        fetchHouses()
    }

    /**
     * Affiche un message pour indiquer que la déconnexion n'est pas encore supportée.
     */
    fun msg(view: View) {
        Toast.makeText(this, "Déconnexion impossible -> fonctionnalité non dispo depuis l'API", Toast.LENGTH_SHORT).show()
    }

    /**
     * Lance l'activité de gestion complète de la maison principale.
     */
    fun openMainHouse(view: View) {
        ownerHouse?.let {
            if (canAccessHouse(it.houseId)) {
                val intent = Intent(this, HouseActivity::class.java)
                intent.putExtra("HOUSE_ID", it.houseId)
                intent.putExtra("TOKEN", token)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Veuillez d'abord ouvrir la maison dans un navigateur", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Lance l'activité permettant de gérer les périphériques d'une maison.
     */
    private fun openHouseActivity(houseId: Int) {
        val intent = Intent(this, HouseDevicesActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    /**
     * Vérifie si les périphériques d'une maison ont été chargés (ouverte dans un navigateur).
     */
    private fun canAccessHouse(houseId: Int): Boolean {
        return loadedHouseDevices.containsKey(houseId)
    }

}
