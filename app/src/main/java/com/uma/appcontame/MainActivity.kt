package com.uma.appcontame

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uma.appcontame.ui.MainViewModel
import com.uma.appcontame.ui.RegistroAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: RegistroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Vincular componentes visuales
        val btnLogin = findViewById<Button>(R.id.btnLoginGoogle)
        val btnLogout = findViewById<Button>(R.id.btnCerrarSesion)
        val rvRegistros = findViewById<RecyclerView>(R.id.rvRegistros)

        // 2. Configurar el RecyclerView para la lista de ingresos y gastos
        adapter = RegistroAdapter()
        rvRegistros.layoutManager = LinearLayoutManager(this)
        rvRegistros.adapter = adapter

        // 3. Inicializar el ViewModel pasando el contexto
        viewModel = MainViewModel(this)

        // 4. Observar el estado del usuario (Arquitectura MVVM)
        viewModel.usuario.observe(this) { usuarioFirebase ->
            if (usuarioFirebase != null) {
                btnLogin.visibility = View.GONE
                btnLogout.visibility = View.VISIBLE
                viewModel.cargarRegistros()
            } else {
                btnLogin.visibility = View.VISIBLE
                btnLogout.visibility = View.GONE
                adapter.actualizarDatos(emptyList())
            }
        }

        // Guía del catedrático: Observar la consulta para mostrar los datos en pantalla
        viewModel.registros.observe(this) { listaDeRegistros ->
            adapter.actualizarDatos(listaDeRegistros)
        }

        // Observar mensajes de alerta o error
        viewModel.mensaje.observe(this) { textoMensaje ->
            if (!textoMensaje.isNullOrEmpty()) {
                Toast.makeText(this, textoMensaje, Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Configurar los clics de los botones
        btnLogin.setOnClickListener {
            viewModel.iniciarSesion()
        }

        btnLogout.setOnClickListener {
            viewModel.cerrarSesion()
        }
    }
}