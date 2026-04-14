package com.example.mybets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mybets.databinding.FragmentRecoverPasswordBinding

class RecoverPasswordFragment : Fragment(R.layout.fragment_recover_password) {

    private lateinit var binding: FragmentRecoverPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecoverPasswordBinding.bind(view)

        // Programamos la flecha de regreso
        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Por ahora el botón de enviar correo no hace nada,
        // pero aquí es donde pondremos la lógica la próxima semana.
        binding.btnEnviarCorreo.setOnClickListener {
            // Espacio para futura lógica
        }
    }
}