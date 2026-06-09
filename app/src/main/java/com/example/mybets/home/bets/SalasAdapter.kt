package com.example.mybets.home.bets

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

data class Sala(val codigo: String, val nombre: String)

// NOTA IMPORTANTE: Cambiamos "List" a "MutableList" para poder borrar elementos en tiempo real
class SalasAdapter(private var salasList: MutableList<Sala>) : RecyclerView.Adapter<SalasAdapter.SalaViewHolder>() {

    class SalaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreSala: TextView = view.findViewById(R.id.tvNombreSala)
        val tvCodigoSala: TextView = view.findViewById(R.id.tvCodigoSala)
        val btnSalirSala: ImageView = view.findViewById(R.id.btnSalirSala) // Conectamos la "X"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sala, parent, false)
        return SalaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalaViewHolder, position: Int) {
        val sala = salasList[position]
        holder.tvNombreSala.text = sala.nombre
        holder.tvCodigoSala.text = "ID: ${sala.codigo}"

        // 1. Clic en la tarjeta para ENTRAR a la sala
        holder.itemView.setOnClickListener { view ->
            val paqueteDatos = Bundle().apply {
                putString("codigo_sala", sala.codigo)
                putString("nombre_sala", sala.nombre)
            }
            view.findNavController().navigate(R.id.salaPrivadaFragment, paqueteDatos)
        }

        // 2. Clic en la "X" roja para SALIR de la sala
        holder.btnSalirSala.setOnClickListener { view ->
            val context = view.context

            // Cuadro de confirmación profesional
            AlertDialog.Builder(context)
                .setTitle("Salir del grupo")
                .setMessage("¿Estás seguro de que quieres salir de '${sala.nombre}'? Perderás el acceso y tus puntos.")
                .setPositiveButton("Sí, salir") { _, _ ->

                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton

                    // Magia de Firebase: Borramos tu ID del grupo
                    FirebaseFirestore.getInstance().collection("salas").document(sala.codigo)
                        .update("miembros", FieldValue.arrayRemove(userId))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Saliste del grupo correctamente", Toast.LENGTH_SHORT).show()

                            // Borramos la tarjeta de la pantalla con una animación
                            val posActual = holder.adapterPosition
                            if (posActual != RecyclerView.NO_POSITION) {
                                salasList.removeAt(posActual)
                                notifyItemRemoved(posActual)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al intentar salir", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun getItemCount() = salasList.size
}