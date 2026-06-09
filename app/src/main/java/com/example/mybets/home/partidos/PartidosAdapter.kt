package com.example.mybets.home.partidos // ¡Asegúrate de que sea tu carpeta!

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mybets.R
import com.example.mybets.core.api.MatchData

class PartidosAdapter(private val partidos: List<MatchData>) : RecyclerView.Adapter<PartidosAdapter.PartidoViewHolder>() {

    class PartidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivLeagueLogo: ImageView = view.findViewById(R.id.ivLeagueLogo)
        val tvLeagueName: TextView = view.findViewById(R.id.tvLeagueName)
        val ivLocalLogo: ImageView = view.findViewById(R.id.ivLocalLogo)
        val tvLocalName: TextView = view.findViewById(R.id.tvLocalName)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val ivVisitanteLogo: ImageView = view.findViewById(R.id.ivVisitanteLogo)
        val tvVisitanteName: TextView = view.findViewById(R.id.tvVisitanteName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_partido, parent, false)
        return PartidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartidoViewHolder, position: Int) {
        val partido = partidos[position]

        holder.tvLeagueName.text = partido.league.name
        holder.tvLocalName.text = partido.teams.home.name
        holder.tvVisitanteName.text = partido.teams.away.name

        // Cortamos la fecha para mostrar solo la hora
        holder.tvFecha.text = partido.fixture.date.substring(11, 16)

        holder.ivLeagueLogo.load(partido.league.logo) { crossfade(true) }
        holder.ivLocalLogo.load(partido.teams.home.logo) { crossfade(true) }
        holder.ivVisitanteLogo.load(partido.teams.away.logo) { crossfade(true) }
    }

    override fun getItemCount() = partidos.size
}