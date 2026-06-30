package com.example.mybets

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mybets.core.AuthRepository
import com.example.mybets.core.ResponseService
import com.example.mybets.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        binding.buttonRegistrar.setOnClickListener {

            val username = binding.etRegisterName.text.toString().trim()
            val correo = binding.etRegisterCorreo.text.toString().trim()
            val password = binding.etRegisterContrasena.text.toString().trim()

            if (username.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                // Mandamos los tres datos iniciales
                val resultado = authRepository.requestSignUp(correo, password, username)

                when (resultado) {
                    is ResponseService.Success -> {
                        Toast.makeText(requireContext(), "¡Cuenta creada! Completa tu perfil legal.", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.registerInformationFragment)
                    }
                    is ResponseService.Error -> {
                        Toast.makeText(requireContext(), resultado.error, Toast.LENGTH_LONG).show()
                    }
                    is ResponseService.Loading -> { }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}