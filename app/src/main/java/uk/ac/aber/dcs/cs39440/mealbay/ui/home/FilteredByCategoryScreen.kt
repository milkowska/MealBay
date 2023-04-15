package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import uk.ac.aber.dcs.cs39440.mealbay.data.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_CATEGORY
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.RecipeList
import uk.ac.aber.dcs.cs39440.mealbay.model.MealViewModel


/**
 * Composable function that displays a screen with a list of recipes filtered by category.
 * It gets the category from dataViewModel and fetches the list of recipes from mealViewModel and
 * getRecipesByCategory using the category. Displays a CircularProgressIndicator if the data is
 * still loading and a RecipeList if the data is ready.
 *
 * @param navController the navigation controller.
 * @param dataViewModel the view model that holds the category and user data.
 * @param mealViewModel the view model that holds the recipe data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable

fun FilteredByCategoryScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
    mealViewModel: MealViewModel = viewModel()
) {
    val category = dataViewModel.getString(CURRENT_CATEGORY)
    val recipeList by category?.let {
        mealViewModel.fetchRecipesByCategory(it).observeAsState(null)
    } ?: remember { mutableStateOf(null) }

    val (isLoading, setIsLoading) = remember { mutableStateOf(true) }

    // When the category changes, set isLoading to true
    LaunchedEffect(category) {
        setIsLoading(true)
    }

    // When the recipeList changes, set isLoading to false
    LaunchedEffect(recipeList) {
        setIsLoading(false)
    }

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
        } else if (recipeList != null) {
            RecipeList(
                recipeList = recipeList!!,
                navController = navController,
                dataViewModel = dataViewModel,
                showButtons = false
            )
        }
    }
}