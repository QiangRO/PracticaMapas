package com.example.practicamapas.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.practicamapas.R
import com.example.practicamapas.Models.Cita

class MyAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val citaList = ArrayList<Cita>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.cita_item,
            parent,false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = citaList[position]
        holder.fechaCita.text = currentitem.fechCita
        holder.horaCita.text = currentitem.horaInicio
        holder.motivoCita.text = currentitem.motivo
    }

    override fun getItemCount(): Int {
        return citaList.size
    }

    fun updateCitaLista(citaList : List<Cita>){
        this.citaList.clear()
        this.citaList.addAll(citaList)
        notifyDataSetChanged()
    }

    class  MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val fechaCita : TextView = itemView.findViewById(R.id.tvFechaCita)
        val horaCita : TextView = itemView.findViewById(R.id.tvHoraCita)
        val motivoCita : TextView = itemView.findViewById(R.id.tvMotivoCita)
    }
}