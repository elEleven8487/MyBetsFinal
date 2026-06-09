package com.example.mybets.home.bets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R
import com.google.firebase.firestore.FirebaseFirestore

class ClasificacionTabFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvClasificacion: RecyclerView
    private lateinit var spinnerTipo: Spinner
    private var jornadaActivaId: String = "jornada_1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clasificacion_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvClasificacion = view.findViewById(R.id.rvClasificacion)
        spinnerTipo = view.findViewById(R.id.spinnerTipoClasificacion)
        val btnCalcularPuntos = view.findViewById<Button>(R.id.btnCalcularPuntos)
        rvClasificacion.layoutManager = LinearLayoutManager(requireContext())

        val salaId = arguments?.getString("codigo_sala")
        if (salaId != null) {
            cargarOpcionesDeJornadas(salaId)

            btnCalcularPuntos.setOnClickListener {
                calcularPuntajes(salaId)
            }
        }
    }

    private fun cargarOpcionesDeJornadas(salaId: String) {
        db.collection("salas").document(salaId).collection("jornadas").get()
            .addOnSuccessListener { query ->
                val numJornadas = query.size()
                if (numJornadas == 0) return@addOnSuccessListener

                // ¡NUEVO! Creamos la lista dinámica: ["General", "Jornada 1", "Jornada 2"...]
                val opciones = mutableListOf("General")
                for (i in 1..numJornadas) {
                    opciones.add("Jornada $i")
                }

                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    opciones
                )
                spinnerTipo.adapter = spinnerAdapter

                // Detectamos qué eligió el usuario
                spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            // Eligió "General" (Sumamos todas las jornadas)
                            cargarPuntosGenerales(salaId)
                        } else {
                            // Eligió una Jornada específica
                            val jornadaSeleccionada = "jornada_$position"
                            cargarJugadoresYPuntos(salaId, jornadaSeleccionada)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
    }

    private fun cargarJugadoresYPuntos(salaId: String, jornadaId: String) {
        val listaTemporal = mutableListOf<JugadorClasificacion>()

        db.collection("salas").document(salaId).get().addOnSuccessListener { documentoSala ->
            val idsMiembros = documentoSala.get("miembros") as? List<String> ?: emptyList()
            if (idsMiembros.isEmpty()) return@addOnSuccessListener

            for (id in idsMiembros) {
                db.collection("usuarios").document(id).get()
                    .addOnSuccessListener { documentoUsuario ->
                        val nombre = documentoUsuario.getString("username") ?: "Usuario"

                        // Buscamos los puntos en la jornada específica
                        db.collection("salas").document(salaId)
                            .collection("jornadas").document(jornadaId)
                            .collection("puntuaciones").document(id).get()
                            .addOnSuccessListener { docPuntos ->
                                val puntosReales = docPuntos.getLong("total")?.toInt() ?: 0
                                listaTemporal.add(JugadorClasificacion(id, nombre, puntosReales))

                                if (listaTemporal.size == idsMiembros.size) {
                                    val listaOrdenada =
                                        listaTemporal.sortedByDescending { it.puntos }
                                    rvClasificacion.adapter = ClasificacionAdapter(listaOrdenada)
                                }
                            }
                    }
            }
        }
    }

    private fun cargarPuntosGenerales(salaId: String) {
        val listaTemporal = mutableListOf<JugadorClasificacion>()

        db.collection("salas").document(salaId).get().addOnSuccessListener { documentoSala ->
            val idsMiembros = documentoSala.get("miembros") as? List<String> ?: emptyList()
            if (idsMiembros.isEmpty()) return@addOnSuccessListener

            // Para los puntos generales, por ahora cargaremos la jornada activa (próximamente sumaremos todas)
            jornadaActivaId = documentoSala.getString("jornadaActiva") ?: "jornada_1"
            cargarJugadoresYPuntos(salaId, jornadaActivaId)
        }
    }

    private fun calcularPuntajes(salaId: String) {
        db.collection("salas").document(salaId).get().addOnSuccessListener { documentoSala ->
            jornadaActivaId = documentoSala.getString("jornadaActiva") ?: "jornada_1"
            Toast.makeText(
                requireContext(),
                "Calculando resultados de $jornadaActivaId...",
                Toast.LENGTH_SHORT
            ).show()

            db.collection("salas").document(salaId).collection("jornadas").document(jornadaActivaId)
                .get().addOnSuccessListener { docJornada ->
                val partidos = docJornada.get("partidosDetalles") as? List<HashMap<String, Any>>
                    ?: return@addOnSuccessListener
                val resultadosOficiales = mutableMapOf<String, String>()
                val opciones = listOf("L", "E", "V")

                for (partido in partidos) {
                    val idPartido = partido["id"] as String
                    resultadosOficiales[idPartido] = opciones.random() // Simulamos el resultado
                }

                // ¡NUEVO!: Guardamos estos resultados oficiales en Firebase para que el Historial los pueda ver
                db.collection("salas").document(salaId).collection("jornadas")
                    .document(jornadaActivaId)
                    .update("resultadosOficiales", resultadosOficiales)

                db.collection("salas").document(salaId).get().addOnSuccessListener { docSala ->
                    val miembros =
                        docSala.get("miembros") as? List<String> ?: return@addOnSuccessListener
                    for (userId in miembros) {
                        db.collection("salas").document(salaId).collection("jornadas")
                            .document(jornadaActivaId)
                            .collection("pronosticos").document(userId).get()
                            .addOnSuccessListener { docPronostico ->
                                var puntosObtenidos = 0
                                if (docPronostico.exists()) {
                                    val predicciones =
                                        docPronostico.get("predicciones") as? Map<String, String>
                                            ?: emptyMap()
                                    for ((idPart, seleccion) in predicciones) {
                                        if (resultadosOficiales[idPart] == seleccion) puntosObtenidos += 3
                                    }
                                }
                                val datosPuntos = hashMapOf("total" to puntosObtenidos)
                                db.collection("salas").document(salaId).collection("jornadas")
                                    .document(jornadaActivaId)
                                    .collection("puntuaciones").document(userId).set(datosPuntos)
                                    .addOnSuccessListener {
                                        cargarOpcionesDeJornadas(salaId)
                                    }
                            }
                    }
                }
            }
        }
    }
}