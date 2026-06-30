package com.example.mybets.home.bets

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R


data class JugadorClasificacion(val id: String, val nombre: String, val puntos: Int)

class ClasificacionAdapter(private val listaJugadores: List<JugadorClasificacion>) : RecyclerView.Adapter<ClasificacionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardClasificacion: CardView = view.findViewById(R.id.cardClasificacion)
        val tvPosicion: TextView = view.findViewById(R.id.tvPosicion)
        val tvNombreJugador: TextView = view.findViewById(R.id.tvNombreJugador)
        val tvPuntos: TextView = view.findViewById(R.id.tvPuntos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clasificacion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jugador = listaJugadores[position]

        holder.tvPosicion.text = (position + 1).toString()
        holder.tvNombreJugador.text = jugador.nombre
        holder.tvPuntos.text = "${jugador.puntos} pts"


        when (position) {
            0 -> {

                holder.cardClasificacion.setCardBackgroundColor(Color.parseColor("#10B9B1"))
                holder.tvNombreJugador.setTextColor(Color.WHITE)
                holder.tvPuntos.setTextColor(Color.WHITE)
                holder.tvPosicion.setTextColor(Color.WHITE)
            }
            1 -> {

                holder.cardClasificacion.setCardBackgroundColor(Color.parseColor("#6EE7B7"))
                holder.tvNombreJugador.setTextColor(Color.parseColor("#1F2937"))
                holder.tvPuntos.setTextColor(Color.parseColor("#1F2937"))
                holder.tvPosicion.setTextColor(Color.parseColor("#1F2937"))
            }
            2 -> {

                holder.cardClasificacion.setCardBackgroundColor(Color.parseColor("#D1FAE5"))
                holder.tvNombreJugador.setTextColor(Color.parseColor("#1F2937"))
                holder.tvPuntos.setTextColor(Color.parseColor("#10B9B1"))
                holder.tvPosicion.setTextColor(Color.parseColor("#1F2937"))
            }
            else -> {

                holder.cardClasificacion.setCardBackgroundColor(Color.WHITE)
                holder.tvNombreJugador.setTextColor(Color.parseColor("#1F2937"))
                holder.tvPuntos.setTextColor(Color.parseColor("#10B9B1"))
                holder.tvPosicion.setTextColor(Color.parseColor("#1F2937"))
            }
        }
    }

    override fun getItemCount() = listaJugadores.size
}