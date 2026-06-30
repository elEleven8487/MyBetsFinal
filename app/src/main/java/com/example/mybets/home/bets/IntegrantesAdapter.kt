package com.example.mybets.home.bets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R


data class Integrante(val id: String, val nombre: String)

class IntegrantesAdapter(private val listaIntegrantes: List<Integrante>) : RecyclerView.Adapter<IntegrantesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreIntegrante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_integrante, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val integrante = listaIntegrantes[position]
        holder.tvNombre.text = integrante.nombre
    }

    override fun getItemCount() = listaIntegrantes.size
}