package com.uma.appcontame.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import com.uma.appcontame.data.AuthRepository
import com.uma.appcontame.data.FirestoreRepository
import com.uma.appcontame.data.Registro

class MainViewModel(private val context: Context) : ViewModel() {
    private val authRepository = AuthRepository(context)
    private val firestoreRepository = FirestoreRepository()

    private val _usuario = MutableLiveData(authRepository.usuarioActual())
    val usuario = _usuario as LiveData<FirebaseUser?>

    private val _registros = MutableLiveData<List<Registro>>(emptyList())
    val registros: LiveData<List<Registro>> = _registros

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun iniciarSesion() {
        viewModelScope.launch {
            val resultado = authRepository.iniciarSesionConGoogle()
            resultado.onSuccess {
                _usuario.value = authRepository.usuarioActual()
                cargarRegistros()
            }.onFailure { error ->
                _mensaje.value = "No se pudo iniciar sesion: ${error.message}"
            }
        }
    }

    fun cargarRegistros() {
        val uid = authRepository.usuarioActual()?.uid
        if (uid == null) {
            _mensaje.value = "Inicie sesion para consultar datos"
            return
        }

        firestoreRepository.consultarRegistros(
            uid = uid,
            alExito = { lista -> _registros.value = lista },
            alError = { error -> _mensaje.value = error }
        )
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _usuario.value = null
        _registros.value = emptyList()
    }
}