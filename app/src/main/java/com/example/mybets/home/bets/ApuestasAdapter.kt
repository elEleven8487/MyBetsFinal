package com.example.mybets.home.bets

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R

// 1. Modificamos el modelo para que recuerde la selección del usuario ("L", "E" o "V")
data class Partido(val id: String, val local: String, val visitante: String, var seleccion: String = "")

class ApuestasAdapter(private val listaPartidos: List<Partido>) : RecyclerView.Adapter<ApuestasAdapter.ApuestaViewHolder>() {

    class ApuestaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLocal: TextView = view.findViewById(R.id.tvEquipoLocal)
        val tvVisitante: TextView = view.findViewById(R.id.tvEquipoVisitante)
        val btnLocal: Button = view.findViewById(R.id.btnLocal)
        val btnEmpate: Button = view.findViewById(R.id.btnEmpate)
        val btnVisitante: Button = view.findViewById(R.id.btnVisitante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApuestaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_apuesta, parent, false)
        return ApuestaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApuestaViewHolder, position: Int) {
        val partido = listaPartidos[position]
        holder.tvLocal.text = partido.local
        holder.tvVisitante.text = partido.visitante

        // 2. Pintamos los botones según lo que el usuario haya seleccionado
        actualizarBotones(holder, partido.seleccion)

        // 3. ¿Qué pasa si toca LOCAL?
        holder.btnLocal.setOnClickListener {
            partido.seleccion = "L"
            notifyItemChanged(position) // Refresca solo esta tarjeta para cambiar el color
        }

        // 4. ¿Qué pasa si toca EMPATE?
        holder.btnEmpate.setOnClickListener {
            partido.seleccion = "E"
            notifyItemChanged(position)
        }

        // 5. ¿Qué pasa si toca VISITANTE?
        holder.btnVisitante.setOnClickListener {
            partido.seleccion = "V"
            notifyItemChanged(position)
        }
    }

    // Función que maneja los colores de los botones
    private fun actualizarBotones(holder: ApuestaViewHolder, seleccion: String) {
        // Definimos los colores: Inactivo (Gris) y Activo (Negro)
        val colorInactivo = Color.parseColor("#E0E0E0")
        val textoInactivo = Color.parseColor("#212121")

        val colorActivo = Color.parseColor("#212121")
        val textoActivo = Color.parseColor("#FFFFFF")

        // Primero ponemos todos en gris (reseteo)
        holder.btnLocal.setBackgroundColor(colorInactivo)
        holder.btnLocal.setTextColor(textoInactivo)
        holder.btnEmpate.setBackgroundColor(colorInactivo)
        holder.btnEmpate.setTextColor(textoInactivo)
        holder.btnVisitante.setBackgroundColor(colorInactivo)
        holder.btnVisitante.setTextColor(textoInactivo)

        // Luego pintamos de negro SOLO el que está seleccionado
        when (seleccion) {
            "L" -> {
                holder.btnLocal.setBackgroundColor(colorActivo)
                holder.btnLocal.setTextColor(textoActivo)
            }
            "E" -> {
                holder.btnEmpate.setBackgroundColor(colorActivo)
                holder.btnEmpate.setTextColor(textoActivo)
            }
            "V" -> {
                holder.btnVisitante.setBackgroundColor(colorActivo)
                holder.btnVisitante.setTextColor(textoActivo)
            }
        }
    }

    override fun getItemCount() = listaPartidos.size
}