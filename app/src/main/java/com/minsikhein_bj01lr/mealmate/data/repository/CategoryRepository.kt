package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.Category
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val categoryCollection = firestore.collection("categories")
    private val TAG = "CategoryRepository"

    suspend fun getAllCategories(): List<Category>? {
        return try {
            val snapshot = categoryCollection.get().await()
            snapshot.toObjects(Category::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching categories", e)
            null
        }
    }

    suspend fun getCategoryById(id: String): Category? {
        return try {
            val document = categoryCollection.document(id).get().await()
            document.toObject(Category::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching category by ID: $id", e)
            null
        }
    }

    suspend fun addCategory(category: Category): Boolean {
        return try {
            categoryCollection.document(category.id).set(category).await()
            Log.d(TAG, "Category added with id=${category.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding category", e)
            false
        }
    }

    suspend fun updateCategory(category: Category): Boolean {
        return try {
            categoryCollection.document(category.id).set(category).await()
            Log.d(TAG, "Category updated with id=${category.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating category", e)
            false
        }
    }

    suspend fun deleteCategory(id: String): Boolean {
        return try {
            categoryCollection.document(id).delete().await()
            Log.d(TAG, "Category deleted with id=$id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting category", e)
            false
        }
    }
}