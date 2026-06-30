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


        Handler(Looper.getMainLooper()).postDelayed({

            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }, 3000)
    }
}