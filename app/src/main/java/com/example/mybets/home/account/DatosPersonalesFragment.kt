package com.example.mybets.home.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mybets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DatosPersonalesFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_datos_personales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBackDatos)
        val tvUsername = view.findViewById<TextView>(R.id.tvDatosUsername)
        val tvNombreReal = view.findViewById<TextView>(R.id.tvDatosNombreReal)
        val tvApPaterno = view.findViewById<TextView>(R.id.tvDatosApPaterno)
        val tvApMaterno = view.findViewById<TextView>(R.id.tvDatosApMaterno)
        val tvTelefono = view.findViewById<TextView>(R.id.tvDatosTelefono)
        val tvFechaNac = view.findViewById<TextView>(R.id.tvDatosFechaNac)

        // Botón para regresar al menú anterior
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Leemos los datos directamente de tu colección "usuarios" usando tus nombres de campo exactos
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documento ->
                    if (documento.exists()) {
                        tvUsername.text = documento.getString("username") ?: "No registrado"
                        tvNombreReal.text = documento.getString("nombreReal") ?: "No registrado"
                        tvApPaterno.text = documento.getString("apPaterno") ?: "No registrado"
                        tvApMaterno.text = documento.getString("apMaterno") ?: "No registrado"
                        tvTelefono.text = documento.getString("telefono") ?: "No registrado"
                        tvFechaNac.text = documento.getString("fechaNacimiento") ?: "No registrado"
                    } else {
                        Toast.makeText(requireContext(), "No se encontró el perfil", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("DATOS", "Error al descargar la info: ${e.message}")
                    Toast.makeText(requireContext(), "Fallo al conectar con la base de datos", Toast.LENGTH_SHORT).show()
                }
        }
    }
}