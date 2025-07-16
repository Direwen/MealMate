package com.minsikhein_bj01lr.mealmate.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.minsikhein_bj01lr.mealmate.data.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel: ViewModel() {
    // the internal list of categories (this list changes can trigger UI recomposition)
    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    // Expose only read-only version to outside
    val categories = _categories.asStateFlow()

    init {
        getCategories()
    }

    fun getCategories() {
        // Get the Firestore database instance
        val db = Firebase.firestore

        // Listen to the "categories" collection in real-time
        db.collection("categories")
            .addSnapshotListener { value, error ->
                // If something goes wrong (e.g. no internet), just return
                if (error != null) {
                    return@addSnapshotListener
                }

                // If we get some data (bcuz something changed in collection)
                if (value != null) {
                    // Convert the Firestore documents into a list of Category objects
                    _categories.value = value.toObjects(Category::class.java)
                    // UI using this list will automatically update
                }
            }
    }
}
