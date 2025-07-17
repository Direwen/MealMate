package com.minsikhein_bj01lr.mealmate.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("users")

    // Creates a Firestore user doc only if it doesn't exist
    suspend fun createUserIfNotExists(user: FirebaseUser, name: String) {
        val userRef = userCollection.document(user.uid)
        val snapshot = userRef.get().await()
        if (!snapshot.exists()) {
            val newUser = User(
                id = user.uid,
                name = name,
                email = user.email ?: ""
            )
            userRef.set(newUser).await()
        }
    }

    // Gets the user document from Firestore
    suspend fun getCurrentUser(userId: String): User? {
        return try {
            val doc = userCollection.document(userId).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

}
