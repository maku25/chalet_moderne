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

    private fun fetchHouses() {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses"
        Api().get<List<HouseData>>(url, ::onFetchHousesSuccess, token)
    }

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

    private fun fetchDevicesForHouse(house: HouseData) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/${house.houseId}/devices"
        Api().get<DevicesResponseData>(url, { code, response ->
            onFetchDevicesSuccess(house, code, response)
        }, token)
    }

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

    fun handleHouse(view: View) {
        if (ownerHouse != null && loadedHouseDevices.containsKey(ownerHouse!!.houseId)) {
            openHouseActivity(ownerHouse!!.houseId)
        } else {
            Toast.makeText(this, "Ouvrez-la d'abord dans un navigateur", Toast.LENGTH_SHORT).show()
        }
    }

    fun refresh(view: View) {
        Toast.makeText(this, "Rafraîchissement...", Toast.LENGTH_SHORT).show()
        fetchHouses()
    }


    fun msg(view: View) {
        Toast.makeText(this, "Déconnexion impossible -> fonctionnalité non dispo depuis l'API", Toast.LENGTH_SHORT).show()
    }

    public fun openMainHouse(view: View) {
        val intent = Intent(this, HouseActivity::class.java)
        intent.putExtra("HOUSE_ID", ownerHouse?.houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    private fun openHouseActivity(houseId: Int) {
        val intent = Intent(this, HouseDevicesActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }
}
