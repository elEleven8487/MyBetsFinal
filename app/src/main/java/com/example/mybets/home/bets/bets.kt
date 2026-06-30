package com.example.mybets.home.bets

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import com.example.mybets.R
import com.example.mybets.core.api.SportsRepository
import com.example.mybets.home.partidos.PartidosAdapter

class bets : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvPartidos = view.findViewById<RecyclerView>(R.id.rvPartidos)
        val spinnerLigas = view.findViewById<Spinner>(R.id.spinnerLigasBets)
        val pbCargando = view.findViewById<android.widget.ProgressBar>(R.id.pbCargandoBets) // <- Conectamos el círculo

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                pbCargando.visibility = View.VISIBLE
                rvPartidos.visibility = View.GONE

                Log.d("API_PRUEBA", "Intentando descargar partidos...")
                val repository = SportsRepository()
                val respuesta = repository.probarConexion()
                Log.d("API_PRUEBA", "¡Éxito! Partidos recibidos: ${respuesta.response.size}")

                val todosLosPartidos = respuesta.response.sortedBy { it.league.name }

                val nombresLigas = mutableListOf("Todas las Ligas")
                val ligasExtraidas = todosLosPartidos.map { it.league.name }.distinct()
                nombresLigas.addAll(ligasExtraidas)

                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresLigas)
                spinnerLigas.adapter = spinnerAdapter

                var adapter = PartidosAdapter(todosLosPartidos)
                rvPartidos.adapter = adapter

                spinnerLigas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val ligaSeleccionada = nombresLigas[position]
                        val listaFiltrada = if (ligaSeleccionada == "Todas las Ligas") {
                            todosLosPartidos
                        } else {
                            todosLosPartidos.filter { it.league.name == ligaSeleccionada }
                        }
                        adapter = PartidosAdapter(listaFiltrada)
                        rvPartidos.adapter = adapter
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }


                pbCargando.visibility = View.GONE
                rvPartidos.visibility = View.VISIBLE

            } catch (e: Exception) {

                pbCargando.visibility = View.GONE
                Log.e("API_PRUEBA", "Error al descargar: ${e.message}")
            }
        }
    }
}