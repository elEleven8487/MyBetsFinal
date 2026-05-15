package com.example.mybets

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mybets.core.AuthRepository
import com.example.mybets.core.ResponseService
import com.example.mybets.databinding.FragmentRegisterInformationBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterInformationFragment : Fragment(R.layout.fragment_register_information) {

    private var _binding: FragmentRegisterInformationBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterInformationBinding.bind(view)

        binding.btnCompletarRegistro.setOnClickListener {
            val nombreReal = binding.etNombres.text.toString().trim()
            val apPaterno = binding.etPaterno.text.toString().trim()
            val apMaterno = binding.etMaterno.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()
            val fechaNac = binding.etFecha.text.toString().trim()

            if (nombreReal.isEmpty() || apPaterno.isEmpty() || apMaterno.isEmpty() || telefono.isEmpty() || fechaNac.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, llena todos tus datos reales", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uidActual = FirebaseAuth.getInstance().currentUser?.uid

            if (uidActual != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val resultado = authRepository.saveUserProfile(
                        uidActual, nombreReal, apPaterno, apMaterno, telefono, fechaNac
                    )

                    when (resultado) {
                        is ResponseService.Success -> {
                            Toast.makeText(requireContext(), "¡Bienvenido a MyBets!", Toast.LENGTH_SHORT).show()

                            // 1. Creamos el "transporte" (Intent) para ir al nuevo edificio
                            val intent = Intent(requireContext(), HomeActivity::class.java)

                            // 2. Iniciamos el viaje
                            startActivity(intent)

                            // 3. Cerramos el edificio de Registro (MainActivity) para que el usuario
                            // no pueda regresar con el botón de "atrás" del celular
                            activity?.finish()
                        }
                        is ResponseService.Error -> {
                            Toast.makeText(requireContext(), resultado.error, Toast.LENGTH_LONG).show()
                        }
                        is ResponseService.Loading -> { }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Error: Usuario no detectado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}