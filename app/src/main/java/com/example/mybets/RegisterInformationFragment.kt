package com.example.mybets // Revisa que sea tu paquete

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mybets.databinding.FragmentRegisterInformationBinding

class RegisterInformationFragment : Fragment(R.layout.fragment_register_information) {

    private lateinit var binding: FragmentRegisterInformationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterInformationBinding.bind(view)

        // Hacemos que la flecha de la esquina superior izquierda nos regrese
        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // El botón "Registrar Información" está listo y visible,
        // pero aún no navega a ninguna parte, tal como pediste.
        binding.btnCompletarRegistro.setOnClickListener {
            // Lógica pendiente para guardar datos
        }
    }
}