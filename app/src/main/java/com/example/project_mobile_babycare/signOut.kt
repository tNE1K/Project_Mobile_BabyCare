package com.example.project_mobile_babycare

import com.google.firebase.auth.FirebaseAuth

class signOut {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    fun signOut() {
        firebaseAuth.signOut()
    }
}