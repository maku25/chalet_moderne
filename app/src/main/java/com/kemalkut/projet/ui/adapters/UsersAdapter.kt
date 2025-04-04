package com.kemalkut.projet.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.kemalkut.projet.R
import com.kemalkut.projet.model.user.UserHouseAccessData

class UsersAdapter(
    private val context: Context,
    private val dataSource: List<UserHouseAccessData>,
    private val onRemoveClick: (String) -> Unit
) : BaseAdapter(), Filterable {

    private var filteredUsers: List<UserHouseAccessData> = dataSource.toList()

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): UserHouseAccessData {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.user_list_item, parent, false)
        val user = getItem(position)

        val textView = rowView.findViewById<TextView>(R.id.userItemText)
        textView.text = "${user.userLogin} ${if (user.owner === 1) "(Propriétaire)" else ""}"

        val removeButton = rowView.findViewById<Button>(R.id.buttonRemoveUser)

        removeButton.visibility = if (user.owner == 1) View.GONE else View.VISIBLE

        removeButton.setOnClickListener {
            onRemoveClick(user.userLogin)
        }

        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""
                val results = if (query.isEmpty()) {
                    dataSource
                } else {
                    dataSource.filter {
                        it.userLogin.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = results }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredUsers = results?.values as List<UserHouseAccessData>
                notifyDataSetChanged()
            }
        }
    }
}