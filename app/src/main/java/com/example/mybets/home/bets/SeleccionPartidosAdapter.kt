package com.example.mybets.home.bets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R


data class PartidoSeleccion(
    val id: String,
    val local: String,
    val visitante: String,
    val liga: String,
    val fechaHora: String,
    val logoLocal: String,
    val logoVisitante: String
)

class SeleccionPartidosAdapter(
    private var listaPartidos: List<PartidoSeleccion>
) : RecyclerView.Adapter<SeleccionPartidosAdapter.ViewHolder>() {

    val partidosSeleccionados = mutableSetOf<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLocal: TextView = view.findViewById(R.id.tvEquipoLocal)
        val tvVisitante: TextView = view.findViewById(R.id.tvEquipoVisitante)
        val cbSeleccionar: CheckBox = view.findViewById(R.id.cbSeleccionar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seleccion_partido, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partido = listaPartidos[position]
        holder.tvLocal.text = partido.local
        holder.tvVisitante.text = partido.visitante

        holder.cbSeleccionar.setOnCheckedChangeListener(null)
        holder.cbSeleccionar.isChecked = partidosSeleccionados.contains(partido.id)

        holder.cbSeleccionar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                partidosSeleccionados.add(partido.id)
            } else {
                partidosSeleccionados.remove(partido.id)
            }
        }
    }

    override fun getItemCount() = listaPartidos.size

    fun actualizarLista(nuevaLista: List<PartidoSeleccion>) {
        listaPartidos = nuevaLista
        notifyDataSetChanged()
    }
}