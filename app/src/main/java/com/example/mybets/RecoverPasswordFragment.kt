package com.example.mybets // Asegúrate de que este paquete sea el tuyo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mybets.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RecoverPasswordFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recover_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Conectamos los elementos de tu diseño
        val buttonBack = view.findViewById<ImageView>(R.id.buttonBack)
        val etRecoverEmail = view.findViewById<TextInputEditText>(R.id.etRecoverEmail)
        val btnEnviarCorreo = view.findViewById<MaterialButton>(R.id.btnEnviarCorreo)


        buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }


        btnEnviarCorreo.setOnClickListener {
            val email = etRecoverEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, escribe tu correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "¡Correo enviado! Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()

                    findNavController().popBackStack()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}