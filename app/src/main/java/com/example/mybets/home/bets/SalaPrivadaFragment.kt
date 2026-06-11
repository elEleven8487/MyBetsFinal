package com.example.mybets.home.bets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mybets.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SalaPrivadaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sala_privada, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackSala = view.findViewById<ImageView>(R.id.btnBackSala)
        val tvDetalleCodigo = view.findViewById<TextView>(R.id.tvDetalleCodigo)
        val tvDetalleNombre = view.findViewById<TextView>(R.id.tvDetalleNombre)

        val tabLayoutSala = view.findViewById<TabLayout>(R.id.tabLayoutSala)
        val viewPagerSala = view.findViewById<ViewPager2>(R.id.viewPagerSala)


        btnBackSala.setOnClickListener {
            findNavController().popBackStack()
        }


        val salaId = arguments?.getString("codigo_sala") ?: "XXXXXX"
        val salaNombre = arguments?.getString("nombre_sala") ?: "Sala"
        tvDetalleCodigo.text = "ID: $salaId"
        tvDetalleNombre.text = salaNombre


        val adapter = SalaPagerAdapter(this, salaId)
        viewPagerSala.adapter = adapter


        TabLayoutMediator(tabLayoutSala, viewPagerSala) { tab, position ->
            tab.text = when (position) {
                0 -> "Quiniela"
                1 -> "Integrantes"
                2 -> "Clasificación"
                3 -> "Historial" // ¡El nombre de tu nueva pestaña!
                else -> ""
            }
        }.attach()
    }
}