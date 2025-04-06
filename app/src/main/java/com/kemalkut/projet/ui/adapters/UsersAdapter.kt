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

/**
 * UsersAdapter — Adaptateur personnalisé pour afficher une liste d’utilisateurs ayant accès à une maison.
 *
 * Cette classe permet :
 * - d’afficher le login des utilisateurs,
 * - d’indiquer s’ils sont propriétaires,
 * - de proposer un bouton pour retirer l’accès à un utilisateur (si ce n’est pas un propriétaire),
 * - d’effectuer une recherche via un filtre sur les logins.
 *
 * @param context Le contexte de l’activité ou application.
 * @param dataSource La liste complète des utilisateurs de la maison.
 * @param onRemoveClick Callback appelé lorsque l’utilisateur clique sur le bouton de suppression d’accès.
 */
class UsersAdapter(
    private val context: Context,
    private val dataSource: List<UserHouseAccessData>,
    private val onRemoveClick: (String) -> Unit
) : BaseAdapter(), Filterable {

    /** Liste filtrée affichée à l'écran (initialement identique à dataSource). */
    private var filteredUsers: List<UserHouseAccessData> = dataSource.toList()

    /** Permet d'injecter dynamiquement les vues. */
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    /** Retourne le nombre d’utilisateurs affichés dans la liste (filtrés). */
    override fun getCount(): Int = filteredUsers.size

    /** Retourne l'utilisateur à la position indiquée dans la liste filtrée. */
    override fun getItem(position: Int): UserHouseAccessData = filteredUsers[position]

    /** Retourne un ID unique pour chaque élément (ici, sa position). */
    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * Génère la vue d’un élément (ligne utilisateur) dans la liste.
     *
     * @param position Position de l'utilisateur.
     * @param convertView Vue réutilisable.
     * @param parent Vue parente.
     * @return La vue à afficher pour cette ligne.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.user_list_item, parent, false)
        val user = getItem(position)

        val textView = rowView.findViewById<TextView>(R.id.userItemText)
        textView.text = "${user.userLogin} ${if (user.owner == 1) "(Propriétaire)" else ""}"

        val removeButton = rowView.findViewById<Button>(R.id.buttonRemoveUser)
        removeButton.visibility = if (user.owner == 1) View.GONE else View.VISIBLE

        removeButton.setOnClickListener {
            onRemoveClick(user.userLogin)
        }

        return rowView
    }

    /**
     * Fournit un filtre personnalisé permettant de rechercher des utilisateurs par login.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            /**
             * Filtre la liste d’utilisateurs en fonction du texte saisi.
             */
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = if (query.isEmpty()) {
                    dataSource
                } else {
                    dataSource.filter {
                        it.userLogin.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = results }
            }

            /**
             * Met à jour la liste affichée avec les résultats filtrés.
             */
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredUsers = results?.values as? List<UserHouseAccessData> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
