package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.Ingredient
import kotlinx.coroutines.tasks.await
import java.util.UUID

class IngredientRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val ingredientCollection = firestore.collection("ingredients")
    private val TAG = "IngredientRepository"

    suspend fun createIngredient(ingredient: Ingredient): Boolean {
        return try {
            ingredientCollection.document(ingredient.id).set(ingredient).await()
            Log.d(TAG, "Ingredient added with id=${ingredient.id}")
            true
        } catch (e: Exception) {
            Log.d(TAG, "Error occured while creating ingredient id=${ingredient.id}")
            false
        }
    }

    suspend fun getOrCreateIngredient(name: String): Ingredient? {
        return try {
            val querySnapshot = ingredientCollection
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                doc.toObject(Ingredient::class.java)?.copy(id = doc.id)
            } else {
                val newId = UUID.randomUUID().toString()
                val newIngredient = Ingredient(id = newId, name = name)
                ingredientCollection.document(newId).set(newIngredient).await()
                Log.d(TAG, "Created new ingredient: $name with id=$newId")
                newIngredient
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getOrCreateIngredient for name=$name", e)
            null
        }
    }

    suspend fun getIngredientById(id: String): Ingredient? {
        return try {
            val doc = ingredientCollection.document(id).get().await()
            doc.toObject(Ingredient::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getIngredientsByIds(ids: List<String>): List<Ingredient> {
        return try {
            if (ids.isEmpty()) return emptyList()

            val chunks = ids.chunked(10)
            val results = mutableListOf<Ingredient>()

            for (chunk in chunks) {
                val snapshot = ingredientCollection
                    .whereIn("id", chunk)
                    .get()
                    .await()
                results += snapshot.toObjects(Ingredient::class.java)
            }

            results
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch ingredients by ids", e)
            emptyList()
        }
    }


}