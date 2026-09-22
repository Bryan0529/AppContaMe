package com.uma.appcontame.data


import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    fun consultarRegistros(
        uid: String,
        alExito: (List<Registro>) -> Unit,
        alError: (String) -> Unit
    ) {
        db.collection("registros")
            .whereEqualTo("propietarioUid", uid)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents.mapNotNull { documento ->
                    documento.toObject(Registro::class.java)
                        ?.copy(id = documento.id)
                }
                alExito(lista)
            }
            .addOnFailureListener { error ->
                alError(error.message ?: "No se pudieron consultar los datos")
            }
    }
}