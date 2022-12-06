package com.example.practicamapas.Repository

import androidx.lifecycle.MutableLiveData
import com.example.practicamapas.Models.Cita
import com.google.firebase.database.*

class CitaRepository {
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Citas")

    @Volatile private var INSTANCE : CitaRepository ?= null

    fun getInstance() : CitaRepository{
        return INSTANCE ?: synchronized(this){

            val instance = CitaRepository()
            INSTANCE = instance
            instance
        }
    }
    fun loadUsers(citaList : MutableLiveData<List<Cita>>){
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _citaList : List<Cita> = snapshot.children.map { dataSnapshot ->

                        dataSnapshot.getValue(Cita::class.java)!!
                    }
                    citaList.postValue(_citaList)

                }catch (e : Exception){
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}