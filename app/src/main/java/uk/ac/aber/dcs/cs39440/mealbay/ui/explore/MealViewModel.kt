package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe

class MealViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = Firebase.firestore

    fun getDocumentById(documentId: String): MutableLiveData<Recipe?> {
        val documentState = MutableLiveData<Recipe?>()

        firestore.collection("recipesready").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val recipe = document.toObject(Recipe::class.java)
                    documentState.value = recipe
                    if (recipe != null) {
                        Log.d("SUC","Document retrieved successfully: ${documentState.value}, ${recipe.difficulty}")
                    }
                } else {
                    documentState.value = null
                    Log.d("SUC","Document not found")
                }
            }
            .addOnFailureListener { exception ->
                documentState.value = null
                println("Failed to retrieve document: ${exception.message}")
                // handle the exception here
            }

        return documentState
    }
}