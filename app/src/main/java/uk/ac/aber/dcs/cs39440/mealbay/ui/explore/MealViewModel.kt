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

/**
 *  MealViewModel class provides functions that retrieve and display data to the UI.
 *  It maintains the state of the data for the UI.
 */
class MealViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val _mealOfTheDay = MutableLiveData<Recipe?>()
    val mealOfTheDay: LiveData<Recipe?> = _mealOfTheDay
    private val _recipesByCategory = MutableLiveData<List<Recipe>>()
    val recipesByCategory: LiveData<List<Recipe>> get() = _recipesByCategory

    // Initializing the MealViewModel to fetch a meal when an instance of MealViewModel is created.
    init {
        fetchMealOfTheDay()
    }

    /**
     * This function fetches a recipe document from a public recipe collection based on the current day of the year.
     * The recipes are not displayed randomly, so that they will not repeat until the whole list of recipes was displayed.
     * It is based on the local date of the user.
     */
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
                    // Setting the index based on the current day of the year and a collection size
                    val dailyRecipeIndex = currentDayOfYear % recipes.size
                    _mealOfTheDay.value = recipes[dailyRecipeIndex]
                    //Log.d("fetchMeal", "A meal for today is: ${_mealOfTheDay.value}")
                } else {
                    _mealOfTheDay.value = null
                }
            }
            .addOnFailureListener { exception ->
                _mealOfTheDay.value = null
                Log.e("fetchMeal", "Failed to fetch recipes: ${exception.message}")
            }
    }

    /**
     * This function retrieves a Recipe document given the user ID and a document ID. It checks public collection of recipes
     * and a private custom recipes collection that the user can create.
     * returns a MutableLiveData object containing the state of the retrieved document, or null if the document was not found.
     *
     * @param documentId The ID of the recipe document to be retrieved
     * @param userId The ID of the user associated with the recipe document.
     * @return A MutableLiveData object containing the recipe document as a Recipe object.
     */
    fun getDocumentById(documentId: String, userId: String): MutableLiveData<Recipe?> {

        val documentState = MutableLiveData<Recipe?>()

        // If the document ID is not in recipesready or privateRecipes collections then no data is retrieved
        if (documentId.isBlank()) {
            Log.d("ERR", "Invalid documentId")
            documentState.value = null
            return documentState
        }

        //Setting references to paths of the two collections in the database.
        val recipesReadyDocRef = firestore.collection("recipesready").document(documentId)
        val privateRecipesDocRef =
            firestore.collection("users").document(userId).collection("privateRecipes")
                .document(documentId)

        //Checking the recipesready collection first
        recipesReadyDocRef.get().addOnCompleteListener { recipesReadyTask ->
            if (recipesReadyTask.isSuccessful) {
                val recipesReadyDoc = recipesReadyTask.result
                if (recipesReadyDoc != null && recipesReadyDoc.exists()) {
                    //Converting the document into an object of recipe class
                    val recipe = recipesReadyDoc.toObject(Recipe::class.java)
                    documentState.value = recipe
                    //Log.d("getDocById", "Document retrieved from recipesready: $recipe")
                } else {
                    //Checking the privateRecipes collection for the document
                    privateRecipesDocRef.get().addOnCompleteListener { privateRecipesTask ->
                        if (privateRecipesTask.isSuccessful) {
                            val privateRecipesDoc = privateRecipesTask.result
                            if (privateRecipesDoc != null && privateRecipesDoc.exists()) {
                                val recipe = privateRecipesDoc.toObject(Recipe::class.java)
                                documentState.value = recipe
                                Log.d(
                                    "getDocById",
                                    "Document retrieved from privateRecipes: $recipe"
                                )
                            } else {
                                //Document is not found
                                documentState.value = null
                            }
                        } else {
                            documentState.value = null
                            Log.e(
                                "ERR",
                                "Failed to retrieve document from privateRecipes collection",
                                privateRecipesTask.exception
                            )
                        }
                    }
                }
            } else {
                privateRecipesDocRef.get().addOnCompleteListener { privateRecipesTask ->
                    if (privateRecipesTask.isSuccessful) {
                        val privateRecipesDoc = privateRecipesTask.result
                        if (privateRecipesDoc != null && privateRecipesDoc.exists()) {
                            val recipe = privateRecipesDoc.toObject(Recipe::class.java)
                            documentState.value = recipe
                            Log.d(
                                "SUC",
                                "Document retrieved from privateRecipes collection: $recipe"
                            )
                        } else {
                            documentState.value = null
                            Log.d("SUC", "Document not found")
                        }
                    } else {
                        documentState.value = null
                        Log.e(
                            "ERR",
                            "Failed to retrieve document from privateRecipes collection",
                            privateRecipesTask.exception
                        )
                    }
                }
            }
        }

        return documentState
    }

    /**
     * Fetches recipes from the "recipesready" collection in Firestore based on the given category to retrieve documents
     * where the "category" field matches the provided category.
     *
     * @param category The category used to filter recipes from the collection.
     * @return The list of recipes fetched from the database, or null if the operation fails.
     */
    fun fetchRecipesByCategory(category: String): List<Recipe>? {
        Log.d("fetchRecipesByCategory", "fetchRecipesByCategory called with category: $category")
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("recipesready")
            .whereEqualTo("category", category)

        query.get()
            .addOnSuccessListener { documents ->
                val recipes = mutableListOf<Recipe>()
                for (document in documents) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipe.apply {
                        id = document.id
                        Log.d("fetchRecipesByCategory", "the id is $id ")
                    }
                    recipes.add(recipe)
                }
                _recipesByCategory.value = recipes
                // Log.d("fetchRecipesByCategory", "Recipes fetched successfully: ${recipes.size}")
            }
            .addOnFailureListener { exception ->
                // Log.w("fetchRecipesByCategory", "Error fetching recipes by category: ", exception)
            }

        return _recipesByCategory.value
    }
}
