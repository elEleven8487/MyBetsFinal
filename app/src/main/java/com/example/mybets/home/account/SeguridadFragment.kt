package com.example.mybets.home.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mybets.R
import com.google.firebase.auth.FirebaseAuth

class SeguridadFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_seguridad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBackSeguridad)
        val tvCorreo = view.findViewById<TextView>(R.id.tvCorreoSeguridad)
        val btnCambiarPassword = view.findViewById<Button>(R.id.btnCambiarPassword)


        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            tvCorreo.text = userEmail
        } else {
            tvCorreo.text = "Error al cargar correo"
        }


        btnCambiarPassword.setOnClickListener {
            if (userEmail != null) {
                auth.sendPasswordResetEmail(userEmail)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "¡Correo enviado! Revisa tu bandeja de entrada o SPAM.", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Error al enviar correo: ${error.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}