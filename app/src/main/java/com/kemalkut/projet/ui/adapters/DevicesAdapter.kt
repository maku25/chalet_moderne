package com.kemalkut.projet.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kemalkut.projet.R
import com.kemalkut.projet.model.device.DeviceData

class DevicesAdapter(
    private val context: Context,
    private val devices: List<DeviceData>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = devices.size

    override fun getItem(position: Int): DeviceData = devices[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.device_list_item, parent, false)
        val device = getItem(position)

        val textViewType = view.findViewById<TextView>(R.id.deviceType)
        val textViewId = view.findViewById<TextView>(R.id.deviceId)
        val textViewState = view.findViewById<TextView>(R.id.deviceState)
        val textViewCommands = view.findViewById<TextView>(R.id.deviceCommands)

        textViewType.text = "Type : ${device.type}"
        textViewId.text = "ID : ${device.id}"

        textViewState.text = when {
            device.power != null -> "État : ${if (device.power == 1) "ON" else "OFF"}"
            device.opening != null -> "Ouverture : ${device.opening}%"
            else -> "État inconnu"
        }

        textViewCommands.text = "Commandes : ${device.availableCommands.joinToString()}"

        return view
    }
}
