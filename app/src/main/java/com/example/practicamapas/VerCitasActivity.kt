package com.example.practicamapas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practicamapas.databinding.ActivityVerCitasBinding
import com.google.firebase.database.DatabaseReference

class VerCitasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerCitasBinding
    private lateinit var database: DatabaseReference
    // ... database = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_citas)
    }
}