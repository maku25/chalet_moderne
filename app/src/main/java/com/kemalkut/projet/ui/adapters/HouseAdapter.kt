package com.kemalkut.projet.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.kemalkut.projet.R
import com.kemalkut.projet.model.house.HouseData

/**
 * Adapter pour afficher la liste des maisons partagées dans le UserDashboard.
 * Pour chaque item, il affiche l'ID de la maison et si l'utilisateur en est propriétaire.
 * Le bouton "Gérer" appelle une fonction passée en paramètre.
 */
class HouseAdapter(
    private val context: Context,
    private val houses: ArrayList<HouseData>,
    private val onManageClick: (HouseData) -> Unit
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = houses.size

    override fun getItem(position: Int): HouseData = houses[position]

    override fun getItemId(position: Int): Long = houses[position].houseId.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.house_list_item, parent, false)
        val house = getItem(position)

        val textView = view.findViewById<TextView>(R.id.houseItemText)
        val manageButton = view.findViewById<Button>(R.id.houseItemButton)

        textView.text = "Maison ${house.houseId}"

        manageButton.setOnClickListener {
            onManageClick(house)
        }

        return view
    }
}
