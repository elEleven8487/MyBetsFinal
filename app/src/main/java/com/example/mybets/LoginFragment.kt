package com.example.mybets
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.mybets.databinding.FragmentLoginBinding // Esto importa  ViewBinding
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment(R.layout.fragment_login) {

    // Variable para conectar la vista (View Binding)
    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflamos el diseño para poder usar "binding."
        binding = FragmentLoginBinding.bind(view)

        setupValidation()

        binding.tvIrRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Al tocar "Restablecer contraseña", viajamos a la nueva pantalla
        binding.tvRestablecerContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
        }
    }

    private fun setupValidation() {
        // Desactivamos el botón al inicio
        binding.btnIngresar.isEnabled = false

        // Los "vigilantes" ahora llaman a tu función validadora
        binding.etEmail.addTextChangedListener {
            validaterFields()
        }

        binding.etPassword.addTextChangedListener {
            validaterFields()
        }
    }

    private fun validaterFields() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Usamos la herramienta nativa de Android para validar el correo
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 8

        // OJO AQUÍ: Cambié un poco tu lógica de error.
        // Si el campo está vacío (isEmpty), no mostramos error aún para no asustar al usuario.
        // Solo mostramos error si ya escribió algo y es inválido.
        binding.textInputLayoutEmail.error = if (email.isEmpty() || isEmailValid) null else "Correo Inválido"
        binding.textInputLayoutPassword.error = if (password.isEmpty() || isPasswordValid) null else "Mínimo 8 caracteres"

        binding.btnIngresar.isEnabled =
            email.isNotEmpty() && password.isNotEmpty() && isEmailValid && isPasswordValid
    }


}