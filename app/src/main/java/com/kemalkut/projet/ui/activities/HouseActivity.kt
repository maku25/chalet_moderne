package com.kemalkut.projet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kemalkut.projet.R

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
        
        btnbackk()
    }

    private fun setupHouseTextView() {
        findViewById<TextView>(R.id.houseTitle).text = "Maison $houseId"
    }

    public fun onManageUsersClick(view: View) {
        val intent = Intent(this, HouseUsersActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    public fun onManageDevicesClick(view: View) {
        val intent = Intent(this, HouseDevicesActivity::class.java)
        intent.putExtra("HOUSE_ID", houseId)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    public fun btnbackk() {
        val backButton = findViewById<ImageButton>(R.id.btnBackkk)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}