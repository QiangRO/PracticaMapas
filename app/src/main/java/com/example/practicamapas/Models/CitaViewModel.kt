package com.example.practicamapas.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.practicamapas.Repository.CitaRepository

class CitaViewModel:ViewModel() {
    private val repository: CitaRepository
    private val _allCitas = MutableLiveData<List<Cita>>()
    val allCitas: LiveData<List<Cita>> = _allCitas

    init {
        repository = CitaRepository().getInstance()
        repository.loadUsers(_allCitas)

    }
}