package com.example.mybets.home.bets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R
import com.google.firebase.firestore.FirebaseFirestore

class IntegrantesTabFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvIntegrantes: RecyclerView
    private lateinit var adapter: IntegrantesAdapter
    private val listaIntegrantes = mutableListOf<Integrante>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_integrantes_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvIntegrantes = view.findViewById(R.id.rvIntegrantes)
        rvIntegrantes.layoutManager = LinearLayoutManager(requireContext())


        adapter = IntegrantesAdapter(listaIntegrantes)
        rvIntegrantes.adapter = adapter

        val salaId = arguments?.getString("codigo_sala")

        if (salaId != null) {
            cargarIntegrantes(salaId)
        }
    }

    private fun cargarIntegrantes(salaId: String) {

        db.collection("salas").document(salaId).get()
            .addOnSuccessListener { documentoSala ->
                if (documentoSala.exists()) {
                    val idsMiembros = documentoSala.get("miembros") as? List<String> ?: emptyList()

                    if (idsMiembros.isNotEmpty()) {
                        obtenerNombresDeUsuarios(idsMiembros)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar la sala", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerNombresDeUsuarios(ids: List<String>) {
        listaIntegrantes.clear()


        for (id in ids) {
            db.collection("usuarios").document(id).get()
                .addOnSuccessListener { documentoUsuario ->

                    val nombre = documentoUsuario.getString("username") ?:
                    documentoUsuario.getString("nombreReal") ?: "Usuario $id"

                    listaIntegrantes.add(Integrante(id, nombre))
                    adapter.notifyDataSetChanged() // Refrescamos la lista visualmente
                }
                .addOnFailureListener {
                    Log.e("INTEGRANTES", "Error al buscar usuario: $id")
                }
        }
    }
}