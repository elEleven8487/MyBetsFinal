package com.example.mybets.home.bets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R
import com.example.mybets.core.api.SportsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SeleccionarPartidosFragment : Fragment() {

    private lateinit var rvPartidosSeleccion: RecyclerView
    private lateinit var spinnerLigas: Spinner
    private lateinit var adapter: SeleccionPartidosAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var todosLosPartidosOriginales: List<PartidoSeleccion> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_seleccionar_partidos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPartidosSeleccion = view.findViewById(R.id.rvPartidosSeleccion)
        spinnerLigas = view.findViewById(R.id.spinnerLigas)
        val btnGuardarQuiniela = view.findViewById<Button>(R.id.btnGuardarQuiniela)
        val tvTituloSeleccion = view.findViewById<TextView>(R.id.tvTituloSeleccion)

        rvPartidosSeleccion.layoutManager = LinearLayoutManager(requireContext())
        adapter = SeleccionPartidosAdapter(emptyList())
        rvPartidosSeleccion.adapter = adapter

        val nombreSala = arguments?.getString("nombre_sala") ?: "Mi Quiniela"

        // ¡LA CLAVE! Revisamos si venimos a agregar una jornada a una sala existente
        val salaIdExistente = arguments?.getString("sala_id_existente")
        if (salaIdExistente != null) {
            tvTituloSeleccion.text = "Nueva Jornada"
            btnGuardarQuiniela.text = "Activar Nueva Jornada"
        }

        cargarPartidosDeLaAPI()

        btnGuardarQuiniela.setOnClickListener {
            val seleccionadosIds = adapter.partidosSeleccionados.toList()
            if (seleccionadosIds.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona al menos un partido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val partidosCompletosParaGuardar = todosLosPartidosOriginales.filter { seleccionadosIds.contains(it.id) }

            // Decidimos qué acción tomar dependiendo de si ya existe la sala
            if (salaIdExistente != null) {
                agregarJornadaASala(salaIdExistente, partidosCompletosParaGuardar)
            } else {
                crearSalaConPartidos(nombreSala, seleccionadosIds, partidosCompletosParaGuardar)
            }
        }
    }

    private fun cargarPartidosDeLaAPI() {
        // Buscamos el círculo de carga y la lista en la pantalla
        val pbCargando = requireView().findViewById<android.widget.ProgressBar>(R.id.pbCargandoSeleccion)

        // Mostramos el círculo y ocultamos la lista temporalmente
        pbCargando.visibility = View.VISIBLE
        rvPartidosSeleccion.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val repository = SportsRepository()
                val respuesta = repository.probarConexion()

                todosLosPartidosOriginales = respuesta.response.map { match ->
                    PartidoSeleccion(
                        id = match.fixture.id.toString(),
                        local = match.teams.home.name,
                        visitante = match.teams.away.name,
                        liga = match.league.name,
                        fechaHora = match.fixture.date,
                        logoLocal = match.teams.home.logo,
                        logoVisitante = match.teams.away.logo
                    )
                }

                val ligasUnicas = listOf("Todas las Ligas") + todosLosPartidosOriginales.map { it.liga }.distinct().sorted()
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, ligasUnicas)
                spinnerLigas.adapter = spinnerAdapter

                spinnerLigas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val ligaSeleccionada = ligasUnicas[position]
                        val listaFiltrada = if (ligaSeleccionada == "Todas las Ligas") todosLosPartidosOriginales else todosLosPartidosOriginales.filter { it.liga == ligaSeleccionada }
                        adapter.actualizarLista(listaFiltrada)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                // ¡Terminó de cargar! Ocultamos el círculo y mostramos los partidos
                pbCargando.visibility = View.GONE
                rvPartidosSeleccion.visibility = View.VISIBLE

            } catch (e: Exception) {
                Log.e("SELECCION", "Error: ${e.message}")
                pbCargando.visibility = View.GONE // También lo ocultamos si hay error
            }
        }
    }

    // --- FUNCIÓN ANTIGUA (Crear sala desde cero) ---
    private fun crearSalaConPartidos(nombre: String, partidosIds: List<String>, partidosDetalles: List<PartidoSeleccion>) {
        val userId = auth.currentUser?.uid ?: return
        val nuevoCodigo = generarCodigoAleatorio()

        val salaData = hashMapOf(
            "codigo" to nuevoCodigo, "adminId" to userId, "miembros" to listOf(userId),
            "nombre" to nombre, "partidosElegidos" to partidosIds, "jornadaActiva" to "jornada_1",
            "fechaCreacion" to System.currentTimeMillis()
        )
        val jornadaData = hashMapOf("numero" to 1, "partidosDetalles" to partidosDetalles, "estado" to "activa", "fechaCreacion" to System.currentTimeMillis())

        db.collection("salas").document(nuevoCodigo).set(salaData).addOnSuccessListener {
            db.collection("salas").document(nuevoCodigo).collection("jornadas").document("jornada_1").set(jornadaData).addOnSuccessListener {
                Toast.makeText(requireContext(), "¡Sala lista!", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

    // --- ¡FUNCIÓN NUEVA! (Agregar jornada a grupo existente) ---
    private fun agregarJornadaASala(salaId: String, partidosDetalles: List<PartidoSeleccion>) {
        // Contamos cuántas jornadas tiene la sala para saber el número de la nueva (ej. si hay 1, la nueva es la 2)
        db.collection("salas").document(salaId).collection("jornadas").get()
            .addOnSuccessListener { query ->
                val numeroNuevaJornada = query.size() + 1
                val nombreJornada = "jornada_$numeroNuevaJornada"

                val jornadaData = hashMapOf(
                    "numero" to numeroNuevaJornada,
                    "partidosDetalles" to partidosDetalles,
                    "estado" to "activa",
                    "fechaCreacion" to System.currentTimeMillis()
                )

                // Guardamos la nueva jornada
                db.collection("salas").document(salaId).collection("jornadas").document(nombreJornada).set(jornadaData)
                    .addOnSuccessListener {
                        // Actualizamos el puntero de la sala principal para que todos vean la nueva jornada
                        db.collection("salas").document(salaId).update("jornadaActiva", nombreJornada)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "¡Jornada $numeroNuevaJornada activada!", Toast.LENGTH_LONG).show()
                                findNavController().popBackStack()
                            }
                    }
            }
    }

    private fun generarCodigoAleatorio(): String {
        return (1..6).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString("")
    }
}