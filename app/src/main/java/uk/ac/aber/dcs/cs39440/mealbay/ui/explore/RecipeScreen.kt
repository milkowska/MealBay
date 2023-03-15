package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel

import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID


@Composable
fun RecipeScreenTopLevel(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    RecipeScreen(navController, dataViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {

    /* val recipeId = remember {
         val arguments = navController.currentBackStackEntry?.arguments
         arguments?.getString("recipeId") ?: ""
     }
 */
    /* var db: FirebaseFirestore = FirebaseFirestore.getInstance()
     var recipeLoaded: Recipe? = null
     LaunchedEffect(recipeId, db) {
         val documentSnapshot = db.collection("recipes").document(recipeId).get().await()
         var recipe = documentSnapshot.toObject(Recipe::class.java)
         if (recipe != null) {
             recipeLoaded = recipe

         } else {
             // handle document not found error
         }
     }
 */
    //   recipeLoaded?.let { ShowRecipeContent(it) }


    var id = dataViewModel.getString(RECIPE_ID)
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "works! $id",
            modifier = Modifier
                .padding(2.dp),
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
            text = "works! ${recipe.title}",
            modifier = Modifier
                .padding(2.dp),
            fontSize = 20.sp
        )
    }


}