package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_CATEGORY
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.RecipeList
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.MealViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FilteredByCategoryScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
    mealViewModel: MealViewModel = viewModel()
) {
    val category = dataViewModel.getString(CURRENT_CATEGORY)
    val recipeList = category?.let { mealViewModel.fetchRecipesByCategory(it) }
    val recipeListLiveData = category?.let { getRecipesByCategory(it) }
    val (isLoading, setIsLoading) = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$category recipes",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                backgroundColor = Color(0xFFFFDAD4)
            )
        }) {

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                CircularProgressIndicator(
                    modifier = Modifier
                        .size(29.dp)
                )
            }
        }

        if (recipeList != null) {

            setIsLoading(false)
            RecipeList(
                recipeList = recipeList,
                navController = navController,
                dataViewModel = dataViewModel,
                showButtons = false
            )
        }
    }
}

/**
 * getRecipesByCategory function gets a reference to the Firestore database and uses a query to retrieve documents
 * from the "recipesready" collection that have the specified category value. It maps the retrieved documents to Recipe
 * objects, setting the ID of each recipe from the document ID, and updates the value of the recipesLiveData
 * object with the resulting list.
 */
fun getRecipesByCategory(category: String): MutableLiveData<List<Recipe>> {
    val recipesLiveData = MutableLiveData<List<Recipe>>()
    val db = FirebaseFirestore.getInstance()

    db.collection("recipesready")
        .whereEqualTo("category", category)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val recipes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                val recipe = documentSnapshot.toObject(Recipe::class.java)
                recipe?.apply { id = documentSnapshot.id
                Log.d("MYTAG", "the id is $id ")}
            }
            recipesLiveData.value = recipes
        }
        .addOnFailureListener { exception ->
            // handle the exception here, e.g., log the error or set the value to null
            recipesLiveData.value = null
            println("Failed to get recipes by category: ${exception.message}")
        }

    return recipesLiveData
}