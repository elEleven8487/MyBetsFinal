package com.example.mybets.home.bets

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController // Importación necesaria para viajar
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SalasFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var rvMisSalas: RecyclerView
    private lateinit var adapter: SalasAdapter
    private val misSalasList = mutableListOf<Sala>()

    private lateinit var etCodigoSala: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_salas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardCrearGrupo = view.findViewById<CardView>(R.id.cardCrearGrupo)
        val btnUnirse = view.findViewById<Button>(R.id.btnUnirse)
        etCodigoSala = view.findViewById(R.id.etCodigoSala)

        rvMisSalas = view.findViewById(R.id.rvMisSalas)
        adapter = SalasAdapter(misSalasList)
        rvMisSalas.adapter = adapter

        cardCrearGrupo.setOnClickListener {
            mostrarDialogoNuevoGrupo()
        }

        btnUnirse.setOnClickListener {
            val codigoIngresado = etCodigoSala.text.toString().trim()
            if (codigoIngresado.length == 6) {
                unirseASalaEnFirebase(codigoIngresado)
            } else {
                etCodigoSala.error = "El código debe tener 6 caracteres"
            }
        }

        cargarMisSalas()
    }

    private fun mostrarDialogoNuevoGrupo() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Configurar Nuevo Grupo")
        builder.setMessage("Ponle un nombre a tu quiniela:")

        val input = EditText(requireContext())
        input.hint = "Ej. Los Reyes de la Quiniela"
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT
        input.setPadding(60, 40, 60, 40)
        builder.setView(input)

        builder.setPositiveButton("Siguiente") { dialog, _ ->
            val nombreSala = input.text.toString().trim()
            if (nombreSala.isNotEmpty()) {

                val paquete = Bundle().apply {
                    putString("nombre_sala", nombreSala)
                }

                requireView().findNavController().navigate(R.id.seleccionarPartidosFragment, paquete)
            } else {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun unirseASalaEnFirebase(codigo: String) {
        val userId = auth.currentUser?.uid ?: return
        val salaRef = db.collection("salas").document(codigo)

        salaRef.get().addOnSuccessListener { documento ->
            if (documento.exists()) {
                salaRef.update("miembros", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "¡Te uniste a la sala!", Toast.LENGTH_SHORT).show()
                        etCodigoSala.text.clear()
                    }
            } else {
                Toast.makeText(requireContext(), "Ese código no existe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarMisSalas() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("salas")
            .whereArrayContains("miembros", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                misSalasList.clear()
                if (snapshot != null) {
                    for (documento in snapshot.documents) {
                        val codigo = documento.getString("codigo") ?: ""
                        val nombre = documento.getString("nombre") ?: "Sin Nombre"
                        misSalasList.add(Sala(codigo, nombre))
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}