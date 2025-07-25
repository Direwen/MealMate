package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.GroceryList
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GroceryListRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val groceryListCollection = firestore.collection("groceryLists")
    private val TAG = "GroceryListRepository"

    suspend fun getOrCreateGroceryList(creatorId: String): GroceryList? {
        return try {
            val querySnapshot = groceryListCollection
                .whereEqualTo("creatorId", creatorId)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                doc.toObject(GroceryList::class.java)?.copy(id = doc.id)
            } else {
                val newId = UUID.randomUUID().toString()
                val newGroceryList = GroceryList(id = newId, creatorId = creatorId)
                groceryListCollection.document(newId).set(newGroceryList).await()
                Log.d(TAG, "Created new grocery list: id=$newId")
                newGroceryList
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getOrCreateGroceryList", e)
            null
        }
    }
}