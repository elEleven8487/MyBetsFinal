package com.example.mybets.home.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mybets.R
import com.google.firebase.auth.FirebaseAuth

class Account : Fragment() { // ¡Corregido a "Account"!

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnMenuSeguridad = view.findViewById<CardView>(R.id.btnMenuSeguridad)
        val btnMenuDatos = view.findViewById<CardView>(R.id.btnMenuDatos)
        val btnCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)

        // 1. Ir a Inicio de Sesión / Seguridad
        btnMenuSeguridad.setOnClickListener {
            try {
                findNavController().navigate(R.id.seguridadFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al abrir Seguridad", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Botón de Datos Personales
        btnMenuDatos.setOnClickListener {
            try {
                // IMPORTANTE: Asegúrate de registrar el DatosPersonalesFragment en tu archivo nav_graph.xml
                findNavController().navigate(R.id.datosPersonalesFragment)
            } catch (e: Exception) {
                Log.e("NAVEGACION", "Error al abrir Datos Personales: ${e.message}")
                Toast.makeText(requireContext(), "Agrega el ID a tu nav_graph", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Cerrar Sesión
        btnCerrarSesion.setOnClickListener {
            // 1. Desconectamos de Firebase
            auth.signOut()
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()

            // 2. Truco Definitivo: Pedimos a Android que abra la app desde su pantalla de inicio original
            val intent = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)

            if (intent != null) {
                // 3. Limpiamos absolutamente todo el historial
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Error al reiniciar la app", Toast.LENGTH_SHORT).show()
            }
        }
    }
}