package com.example.mybets.home.bets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistorialTabFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var rvHistorial: RecyclerView
    private lateinit var spinnerJornadas: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historial_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvHistorial = view.findViewById(R.id.rvHistorial)
        spinnerJornadas = view.findViewById(R.id.spinnerJornadasHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(requireContext())

        val salaId = arguments?.getString("codigo_sala")
        if (salaId != null) {
            cargarOpcionesDeJornadas(salaId)
        }
    }

    private fun cargarOpcionesDeJornadas(salaId: String) {
        // Contamos cuántas jornadas hay en Firebase
        db.collection("salas").document(salaId).collection("jornadas").get()
            .addOnSuccessListener { query ->
                val numJornadas = query.size()
                if (numJornadas == 0) return@addOnSuccessListener


                val opciones = mutableListOf<String>()
                for (i in 1..numJornadas) {
                    opciones.add("Jornada $i")
                }

                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    opciones
                )
                spinnerJornadas.adapter = spinnerAdapter


                spinnerJornadas.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val jornadaSeleccionada = "jornada_${position + 1}"
                            cargarMiHistorial(salaId, jornadaSeleccionada)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
            }
    }

    private fun cargarMiHistorial(salaId: String, jornadaId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("salas").document(salaId)
            .collection("jornadas").document(jornadaId).get()
            .addOnSuccessListener { docJornada ->
                val partidos =
                    docJornada.get("partidosDetalles") as? List<HashMap<String, Any>> ?: emptyList()
                // ¡NUEVO! Leemos los resultados oficiales (si ya se calcularon)
                val resultadosOficiales =
                    docJornada.get("resultadosOficiales") as? Map<String, String> ?: emptyMap()

                db.collection("salas").document(salaId)
                    .collection("jornadas").document(jornadaId)
                    .collection("pronosticos").document(userId).get()
                    .addOnSuccessListener { docPronostico ->
                        val misPredicciones =
                            docPronostico.get("predicciones") as? Map<String, String> ?: emptyMap()

                        val listaFinal = mutableListOf<ItemHistorial>()
                        for (partido in partidos) {
                            val id = partido["id"] as? String ?: ""
                            val local = partido["local"] as? String ?: "Local"
                            val visitante = partido["visitante"] as? String ?: "Visitante"

                            val miVoto = misPredicciones[id] ?: "Sin voto"


                            var estado = 0 // Pendiente
                            if (resultadosOficiales.containsKey(id)) {
                                val resultadoReal = resultadosOficiales[id]
                                estado = if (resultadoReal == miVoto) 1 else 2 // 1 Acierto, 2 Fallo
                            }

                            listaFinal.add(ItemHistorial("$local vs $visitante", miVoto, estado))
                        }


                        val llEstadoVacio =
                            requireView().findViewById<android.widget.LinearLayout>(R.id.llEstadoVacioHistorial)

                        if (listaFinal.isEmpty()) {

                            rvHistorial.visibility = android.view.View.GONE
                            llEstadoVacio.visibility = android.view.View.VISIBLE
                        } else {

                            rvHistorial.visibility = android.view.View.VISIBLE
                            llEstadoVacio.visibility = android.view.View.GONE

                            val adapter = HistorialAdapter(listaFinal)
                            rvHistorial.adapter = adapter
                        }
                    }
            }
    }
}
