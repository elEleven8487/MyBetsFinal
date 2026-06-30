package com.example.mybets.home.bets

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybets.R


data class PartidoSala(
    val id: String,
    val liga: String,
    val fechaHora: String,
    val localNombre: String,
    val localLogoUrl: String,
    val visitanteNombre: String,
    val visitanteLogoUrl: String,
    var seleccion: String = ""
)

class QuinielaSalaAdapter(private val listaPartidos: List<PartidoSala>) : RecyclerView.Adapter<QuinielaSalaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLiga: TextView = view.findViewById(R.id.tvLiga)
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora)
        val tvLocal: TextView = view.findViewById(R.id.tvEquipoLocal)
        val tvVisitante: TextView = view.findViewById(R.id.tvEquipoVisitante)
        val ivLogoLocal: ImageView = view.findViewById(R.id.ivLogoLocal)
        val ivLogoVisitante: ImageView = view.findViewById(R.id.ivLogoVisitante)

        val btnLocal: Button = view.findViewById(R.id.btnLocal)
        val btnEmpate: Button = view.findViewById(R.id.btnEmpate)
        val btnVisitante: Button = view.findViewById(R.id.btnVisitante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiniela_sala, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partido = listaPartidos[position]

        holder.tvLiga.text = partido.liga
        holder.tvLocal.text = partido.localNombre
        holder.tvVisitante.text = partido.visitanteNombre


        holder.tvFechaHora.text = partido.fechaHora.substringBefore("T")


        Glide.with(holder.itemView.context).load(partido.localLogoUrl).into(holder.ivLogoLocal)
        Glide.with(holder.itemView.context).load(partido.visitanteLogoUrl).into(holder.ivLogoVisitante)


        actualizarBotones(holder, partido.seleccion)

        holder.btnLocal.setOnClickListener {
            partido.seleccion = "L"
            notifyItemChanged(position)
        }
        holder.btnEmpate.setOnClickListener {
            partido.seleccion = "E"
            notifyItemChanged(position)
        }
        holder.btnVisitante.setOnClickListener {
            partido.seleccion = "V"
            notifyItemChanged(position)
        }
    }

    private fun actualizarBotones(holder: ViewHolder, seleccion: String) {

        val colorInactivo = android.graphics.Color.parseColor("#E0E0E0")
        val textoInactivo = android.graphics.Color.parseColor("#1F2937")


        val colorActivo = android.graphics.Color.parseColor("#10B9B1")
        val textoActivo = android.graphics.Color.parseColor("#FFFFFF")

        holder.btnLocal.setBackgroundColor(colorInactivo)
        holder.btnLocal.setTextColor(textoInactivo)
        holder.btnEmpate.setBackgroundColor(colorInactivo)
        holder.btnEmpate.setTextColor(textoInactivo)
        holder.btnVisitante.setBackgroundColor(colorInactivo)
        holder.btnVisitante.setTextColor(textoInactivo)

        when (seleccion) {
            "L" -> { holder.btnLocal.setBackgroundColor(colorActivo); holder.btnLocal.setTextColor(textoActivo) }
            "E" -> { holder.btnEmpate.setBackgroundColor(colorActivo); holder.btnEmpate.setTextColor(textoActivo) }
            "V" -> { holder.btnVisitante.setBackgroundColor(colorActivo); holder.btnVisitante.setTextColor(textoActivo) }
        }
    }

    override fun getItemCount() = listaPartidos.size


    fun obtenerPronosticos(): Map<String, String> {
        val pronosticos = mutableMapOf<String, String>()
        for (partido in listaPartidos) {
            if (partido.seleccion.isNotEmpty()) {
                pronosticos[partido.id] = partido.seleccion
            }
        }
        return pronosticos
    }

}