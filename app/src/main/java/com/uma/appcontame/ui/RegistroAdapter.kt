package com.uma.appcontame.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uma.appcontame.R
import com.uma.appcontame.data.Registro

class RegistroAdapter(private var lista: List<Registro> = emptyList()) :
    RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = lista[position]
        holder.tvTitulo.text = registro.titulo
        holder.tvDescripcion.text = registro.descripcion
    }

    override fun getItemCount(): Int = lista.size

    // Esta función refresca la lista en pantalla cuando Firebase mande los datos
    fun actualizarDatos(nuevaLista: List<Registro>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}