package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel

import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.CircularProgressBar


@Composable
fun RecipeScreenTopLevel(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
    mealViewModel: MealViewModel = viewModel()
) {
    RecipeScreen(navController, dataViewModel, mealViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
    mealViewModel: MealViewModel = viewModel()
) {

    var id = dataViewModel.getString(RECIPE_ID)
    /*   Text(
           text = "works! $id",
           modifier = Modifier
               .padding(2.dp),
           fontSize = 20.sp
       )*/
    val db = FirebaseFirestore.getInstance()
    val documentRef = id?.let { db.collection("recipes").document(it) }
    if (id != null) {
        YourComposableFunction(documentId = id, mealViewModel)
    }

}

@Composable
fun YourComposableFunction(documentId: String, mealViewModel: MealViewModel) {
    val documentState = remember { mutableStateOf<Recipe?>(null) }

    // Observe the LiveData returned by getDocumentById and update the documentState
    // object when it changes
    LaunchedEffect(documentId) {
        mealViewModel.getDocumentById(documentId).observeForever { recipe ->
            Log.d("YourComposableFunction", "result: $recipe")
            documentState.value = recipe
        }
    }

    Log.d("YourComposableFunction", "documentState: ${documentState.value}")

    if (documentState.value == null) {
        Text(
            text = "not working",
            modifier = Modifier.padding(2.dp),
            fontSize = 20.sp
        )
        //CircularProgressBar(isDisplayed = true)

    } else {
        val document = documentState.value!!
        ShowRecipeContent(recipe = documentState.value!!)
        Text (
                text = "works! ${document.title}",
        modifier = Modifier.padding(2.dp),
        fontSize = 20.sp
        )
    }
}


@Composable
fun ShowRecipeContent(recipe: Recipe) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "works! ${recipe.difficulty}",
            modifier = Modifier
                .padding(50.dp),
            fontSize = 20.sp
        )
    }
}
