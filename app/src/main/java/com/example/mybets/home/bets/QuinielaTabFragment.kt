package com.example.mybets.home.bets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybets.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuinielaTabFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: QuinielaSalaAdapter
    private var jornadaActivaId: String = "jornada_1"
    private var totalPartidosElegidos = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quiniela_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvQuinielaTab = view.findViewById<RecyclerView>(R.id.rvQuinielaTab)
        val btnGuardarPronosticos = view.findViewById<Button>(R.id.btnGuardarPronosticos)
        val btnNuevaJornada = view.findViewById<Button>(R.id.btnNuevaJornada) // ¡Nuevo botón!

        rvQuinielaTab.layoutManager = LinearLayoutManager(requireContext())
        val salaId = arguments?.getString("codigo_sala")

        if (salaId != null) {
            // Le pasamos el botón a la función para que decida si mostrarlo o no
            cargarPartidosDesdeFirebase(salaId, rvQuinielaTab, btnNuevaJornada)

            // Si el Admin toca el botón, lo mandamos a elegir partidos, avisando de qué sala viene
            btnNuevaJornada.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("sala_id_existente", salaId)
                }
                findNavController().navigate(R.id.seleccionarPartidosFragment, bundle)
            }
        }

        btnGuardarPronosticos.setOnClickListener {
            if (!::adapter.isInitialized) return@setOnClickListener
            val pronosticos = adapter.obtenerPronosticos()
            if (pronosticos.size < totalPartidosElegidos) {
                Toast.makeText(requireContext(), "¡Te faltan partidos por predecir!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (salaId != null) guardarPronosticosEnFirebase(salaId, pronosticos)
        }
    }

    private fun cargarPartidosDesdeFirebase(salaId: String, recyclerView: RecyclerView, btnNuevaJornada: Button) {
        db.collection("salas").document(salaId).get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    jornadaActivaId = documento.getString("jornadaActiva") ?: "jornada_1"

                    // ¡AQUÍ ESTÁ LA MAGIA! Comparamos el Admin de la sala con el usuario actual
                    val adminId = documento.getString("adminId")
                    if (adminId == auth.currentUser?.uid) {
                        btnNuevaJornada.visibility = View.VISIBLE
                    }

                    db.collection("salas").document(salaId)
                        .collection("jornadas").document(jornadaActivaId)
                        .get()
                        .addOnSuccessListener { docJornada ->
                            if (docJornada.exists()) {
                                val partidosGuardados = docJornada.get("partidosDetalles") as? List<HashMap<String, Any>> ?: emptyList()
                                totalPartidosElegidos = partidosGuardados.size

                                if (partidosGuardados.isEmpty()) return@addOnSuccessListener

                                val partidosConvertidos = partidosGuardados.map { map ->
                                    PartidoSala(
                                        id = map["id"] as? String ?: "",
                                        liga = map["liga"] as? String ?: "",
                                        fechaHora = map["fechaHora"] as? String ?: "",
                                        localNombre = map["local"] as? String ?: "",
                                        localLogoUrl = map["logoLocal"] as? String ?: "",
                                        visitanteNombre = map["visitante"] as? String ?: "",
                                        visitanteLogoUrl = map["logoVisitante"] as? String ?: ""
                                    )
                                }
                                adapter = QuinielaSalaAdapter(partidosConvertidos)
                                recyclerView.adapter = adapter
                            }
                        }
                }
            }
    }

    private fun guardarPronosticosEnFirebase(salaId: String, pronosticos: Map<String, String>) {
        val userId = auth.currentUser?.uid ?: return
        val datosPronostico = hashMapOf("predicciones" to pronosticos, "fechaGuardado" to System.currentTimeMillis())

        db.collection("salas").document(salaId)
            .collection("jornadas").document(jornadaActivaId)
            .collection("pronosticos").document(userId)
            .set(datosPronostico)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "¡Pronósticos guardados!", Toast.LENGTH_SHORT).show()
            }
    }
}