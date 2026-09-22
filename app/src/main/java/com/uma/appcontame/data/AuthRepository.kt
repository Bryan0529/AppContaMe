package com.uma.appcontame.data


import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.uma.appcontame.R
import kotlinx.coroutines.tasks.await // 🟢 IMPORTANTE: Resuelve el .await() de Firebase

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    fun usuarioActual() = auth.currentUser

    suspend fun iniciarSesionConGoogle(): Result<String> { // 🟢 Tipo de Result especificado
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(
                    context.getString(R.string.default_web_client_id)
                )
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider
                    .getCredential(googleCredential.idToken, null)

                auth.signInWithCredential(firebaseCredential).await()
                Result.success(auth.currentUser?.uid.orEmpty())
            } else {
                Result.failure(Exception("Credencial de Google no reconocida"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }
}