package com.example.mybets.core

import com.google.firebase.auth.FirebaseUser

interface Authentication {
    suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser>

    suspend fun requestSignUp(email: String, password: String, nombreUsuario: String): ResponseService<FirebaseUser>

    suspend fun saveUserProfile(
        uid: String,
        nombreReal: String,
        apPaterno: String,
        apMaterno: String,
        telefono: String,
        fechaNacimiento: String
    ): ResponseService<Boolean>
}