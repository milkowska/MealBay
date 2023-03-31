package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class MealViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val _mealOfTheDay = MutableLiveData<Recipe?>()
    val mealOfTheDay: LiveData<Recipe?> = _mealOfTheDay
    private val _recipesByCategory = MutableLiveData<List<Recipe>>()
    val recipesByCategory: LiveData<List<Recipe>> get() = _recipesByCategory

    init {
        fetchMealOfTheDay()
    }

    fun fetchMealOfTheDay() {
        val calendar = Calendar.getInstance()
        val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        firestore.collection("recipesready")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val recipes = querySnapshot.documents.map { document ->
                        val recipe = document.toObject(Recipe::class.java)
                        recipe?.copy(id = document.id)
                    }.filterNotNull()
                    val dailyRecipeIndex = currentDayOfYear % recipes.size
                    _mealOfTheDay.value = recipes[dailyRecipeIndex]
                    Log.d("FETCHMEAL", "${_mealOfTheDay.value}")
                } else {
                    _mealOfTheDay.value = null
                }
            }
            .addOnFailureListener { exception ->
                _mealOfTheDay.value = null
                Log.e("MealViewModel", "Failed to fetch recipes: ${exception.message}")
            }
    }


    fun getDocumentById(documentId: String): MutableLiveData<Recipe?> {
        val documentState = MutableLiveData<Recipe?>()
        if (documentId.isBlank()) {
            Log.d("ERR", "Invalid documentId")
            documentState.value = null
            return documentState
        }

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


    /**
     * Fetches recipes from the "recipesready" collection in Firestore based on the given category to retrieve documents
     * where the "category" field matches the provided category.
     *
     * @param category The category used to filter recipes from the collection.
     *
     */
    fun fetchRecipesByCategory(category: String): List<Recipe>? {
        Log.d("MealViewModel", "fetchRecipesByCategory called with category: $category")
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("recipesready")
            .whereEqualTo("category", category)

        query.get()
            .addOnSuccessListener { documents ->
                val recipes = mutableListOf<Recipe>()
                for (document in documents) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipe.apply { id = document.id
                    Log.d("MYTAG1", "the id is $id ")}

                recipes.add(recipe)
                }
                _recipesByCategory.value = recipes
                Log.d("MealViewModel", "Recipes fetched successfully: ${recipes.size}")
            }
            .addOnFailureListener { exception ->
                Log.w("fetchRecipesByCategory", "Error fetching recipes by category: ", exception)
            }
        return _recipesByCategory.value
    }
}
