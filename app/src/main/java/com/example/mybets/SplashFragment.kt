package com.example.mybets // Revisa que este sea tu paquete correcto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment(R.layout.splash_fragment) { // Revisa que aquí diga el nombre correcto de tu XML

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hacemos que la app espere 3000 milisegundos (3 segundos)
        Handler(Looper.getMainLooper()).postDelayed({
            // Navegamos al Login (Esta flecha debe existir en tu nav_onboarding.xml)
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }, 3000)
    }
}