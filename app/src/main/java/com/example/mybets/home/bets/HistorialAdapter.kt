package com.example.mybets.home.bets

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R

// estado: 0 = Pendiente, 1 = Acierto, 2 = Fallo
data class ItemHistorial(val partido: String, val prediccion: String, val estado: Int)

class HistorialAdapter(private val lista: List<ItemHistorial>) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPartido: TextView = view.findViewById(R.id.tvPartidoHistorial)
        val tvMiPronostico: TextView = view.findViewById(R.id.tvMiPronostico)
        val tvIndicadorResultado: TextView = view.findViewById(R.id.tvIndicadorResultado)
        val fondoMiPronostico: LinearLayout = view.findViewById(R.id.fondoMiPronostico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tvPartido.text = item.partido
        holder.tvMiPronostico.text = item.prediccion

        when (item.estado) {
            1 -> { // Acierto
                holder.tvIndicadorResultado.text = "✅"
                holder.fondoMiPronostico.setBackgroundColor(Color.parseColor("#D1FAE5")) // Verde tenue
                holder.tvMiPronostico.setTextColor(Color.parseColor("#10B9B1"))
            }
            2 -> { // Fallo
                holder.tvIndicadorResultado.text = "❌"
                holder.fondoMiPronostico.setBackgroundColor(Color.parseColor("#FEE2E2")) // Rojo tenue
                holder.tvMiPronostico.setTextColor(Color.parseColor("#EF4444")) // Rojo fuerte
            }
            else -> { // Pendiente
                holder.tvIndicadorResultado.text = "⏳"
                holder.fondoMiPronostico.setBackgroundColor(Color.parseColor("#E5E7EB")) // Gris tenue
                holder.tvMiPronostico.setTextColor(Color.parseColor("#1F2937"))
            }
        }
    }

    override fun getItemCount() = lista.size
}