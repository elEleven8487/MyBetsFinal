package com.example.mybets.core

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(): Authentication {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    override suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { ResponseService.Success(it) } ?: ResponseService.Error("Usuario no encontrado")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            ResponseService.Error("Correo o contraseña incorrectos")
        } catch (e: Exception) {
            ResponseService.Error("Error inesperado. Intenta de nuevo")
        }
    }

    override suspend fun requestSignUp(email: String, password: String, nombreUsuario: String): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Guardamos el apodo y correo iniciales
                val mapaUsuario = hashMapOf(
                    "username" to nombreUsuario,
                    "correo" to email,
                    "uid" to firebaseUser.uid
                )
                firestore.collection("usuarios").document(firebaseUser.uid).set(mapaUsuario).await()
                ResponseService.Success(firebaseUser)
            } else {
                ResponseService.Error("No se pudo crear el usuario")
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            ResponseService.Error("Este correo ya esta registrado, intenta con otro")
        } catch (e: FirebaseAuthWeakPasswordException) {
            ResponseService.Error("La contraseña es muy debil")
        } catch (e: Exception) {
            ResponseService.Error("Error inesperado. Intenta de nuevo")
        }
    }

    override suspend fun saveUserProfile(
        uid: String, nombreReal: String, apPaterno: String, apMaterno: String, telefono: String, fechaNacimiento: String
    ): ResponseService<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Preparamos los datos reales
            val mapaPerfil = hashMapOf<String, Any>(
                "nombreReal" to nombreReal,
                "apPaterno" to apPaterno,
                "apMaterno" to apMaterno,
                "telefono" to telefono,
                "fechaNacimiento" to fechaNacimiento
            )

            // Usamos merge() para fusionar estos datos con el username que ya existe
            firestore.collection("usuarios").document(uid).set(mapaPerfil, SetOptions.merge()).await()
            ResponseService.Success(true)
        } catch (e: Exception) {
            ResponseService.Error("Error al guardar la información: ${e.message}")
        }
    }
}