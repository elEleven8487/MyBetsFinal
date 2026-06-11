package com.example.mybets.home.bets

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SalaPagerAdapter(fragment: Fragment, private val salaId: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4 // ¡Ahora son 4 pestañas!

    override fun createFragment(position: Int): Fragment {
        val paquete = Bundle().apply {
            putString("codigo_sala", salaId)
        }


        val fragment = when (position) {
            0 -> QuinielaTabFragment() as Fragment
            1 -> IntegrantesTabFragment() as Fragment
            2 -> ClasificacionTabFragment() as Fragment
            3 -> HistorialTabFragment() as Fragment
            else -> QuinielaTabFragment() as Fragment
        }

        fragment.arguments = paquete
        return fragment
    }
}