package com.example.practicamapas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.practicamapas.databinding.ActivityCitaBinding
import com.example.practicamapas.timepicker.TimePickerFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime

class CitaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCitaBinding
    // Write a message to the database
    private lateinit var database:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cita)
        binding = ActivityCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrarCita.setOnClickListener {
            val fechaCita = binding.etdFechaCita.text.toString()
            val horaInicio = binding.etdHoraInicioCita.text.toString()
            val horaFinal = binding.etdHoraFinalCita.text.toString()
            val motivo = binding.etMotivoCita.text.toString()
            val fechaRegistro = LocalDateTime.now().toString()
            database = FirebaseDatabase.getInstance().getReference("Citas")
            val Cita = Cita(fechaCita, horaInicio, horaFinal, motivo, fechaRegistro)
            database.child(fechaCita).setValue(Cita).addOnSuccessListener {
                Toast.makeText(this, "Cita registrada", Toast.LENGTH_SHORT).show()
                binding.etdFechaCita.setText("")
                binding.etdHoraInicioCita.setText("")
                binding.etdHoraFinalCita.setText("")
                binding.etMotivoCita.setText("")
            }.addOnFailureListener{
                Toast.makeText(this, "Error al registrar cita", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment{onTimeSelected(it)}
        timePicker.show(supportFragmentManager, "time")
    }
    private fun onTimeSelected(time: String) {
        binding.etdHoraInicioCita.setText("Has seleccionado las: $time")
    }
}