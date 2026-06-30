package com.example.mybets

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mybets.core.AuthRepository
import com.example.mybets.core.ResponseService
import com.example.mybets.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnIngresar.setOnClickListener {
            val correo = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val resultado = authRepository.requestLogin(correo, password)

                when (resultado) {
                    is ResponseService.Success -> {
                        Toast.makeText(requireContext(), "¡Bienvenido de vuelta!", Toast.LENGTH_SHORT).show()


                        val intent = Intent(requireContext(), HomeActivity::class.java)


                        startActivity(intent)


                        activity?.finish()
                    }
                    is ResponseService.Error -> {
                        Toast.makeText(requireContext(), resultado.error, Toast.LENGTH_LONG).show()
                    }
                    is ResponseService.Loading -> { }
                }
            }
        }

        binding.tvIrRegistro.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        binding.tvRestablecerContrasena.setOnClickListener {
            findNavController().navigate(R.id.recoverPasswordFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}