package com.example.mybets // Asegúrate de que este sea tu paquete correcto

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.mybets.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        setupValidation()
    }

    //
    private fun setupValidation() {
        // 1. Al tocar la flecha, simulamos el botón de "Atrás" del celular
        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 2. Desactivamos el botón de registrar al inicio
        binding.buttonRegistrar.isEnabled = false

        // 3. Ponemos a los "vigilantes" de texto
        binding.etRegisterName.addTextChangedListener { validateFields() }
        binding.etRegisterCorreo.addTextChangedListener { validateFields() }
        binding.etRegisterContrasena.addTextChangedListener { validateFields() }
    }

    private fun validateFields() {
        // Extraemos el texto
        val name = binding.etRegisterName.text.toString().trim()
        val email = binding.etRegisterCorreo.text.toString().trim()
        val password = binding.etRegisterContrasena.text.toString().trim()

        // Validamos las reglas
        val isNameValid = name.isNotEmpty()
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 8

        // Mostramos errores visuales en los contenedores padres
        binding.textInputLayoutRegisterName.error = if (name.isEmpty() || isNameValid) null else "El nombre es obligatorio"
        binding.textInputLayoutRegisterCorreo.error = if (email.isEmpty() || isEmailValid) null else "Correo inválido"
        binding.textInputLayoutRegisterContrasena.error = if (password.isEmpty() || isPasswordValid) null else "Mínimo 8 caracteres"

        // El botón solo se enciende si TODO está correcto
        binding.buttonRegistrar.isEnabled = isNameValid && email.isNotEmpty() && isEmailValid && isPasswordValid
    }
} //