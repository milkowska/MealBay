package uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.data.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.*
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * This is a composable function that displays either the categories to be chosen by the user for new recipe or shows
 * a screen informing about the success of adding a recipe to the private collection.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to save the recipe data.
 */
@Composable
fun CategoryScreen(
    navController: NavController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    val categorySelected = remember { mutableStateOf(false) }

    if (!categorySelected.value) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = stringResource(id = R.string.category),
                    fontSize = 21.sp
                )
                Text(
                    text = stringResource(id = R.string.which_category),
                    fontSize = 17.sp
                )

                Divider(
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                val categories = remember {
                    listOf(
                        "Breakfast",
                        "Beverage",
                        "Dessert",
                        "Dinner",
                        "Lunch",
                        "Salad",
                        "Soup",
                        "Vegan",
                        "Vegetarian"
                    )
                }
                val selectedCategory = remember { mutableStateOf<String?>(null) }

                LazyColumn {
                    items(categories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = category)
                            Checkbox(
                                checked = category == selectedCategory.value,
                                onCheckedChange = {
                                    if (it) {
                                        selectedCategory.value = category
                                    } else {
                                        selectedCategory.value = null
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    checkedColor = Color(0xFF9C4234)
                                )
                            )
                        }
                    }
                }

                FilledTonalButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        selectedCategory.value?.let {
                            dataViewModel.saveString(it, NEW_RECIPE_CATEGORY)
                        }
                        //Setting a default picture of the recipe
                        dataViewModel.saveString(
                            "https://cdn.pixabay.com/photo/2020/09/02/08/19/dinner-5537679_960_720.png",
                            NEW_RECIPE_PHOTO
                        )
                        categorySelected.value = true
                    },
                    enabled = selectedCategory.value != null,
                    modifier = Modifier
                        .width(220.dp)
                        .height(50.dp),
                ) {
                    Text(text = stringResource(id = R.string.save), fontFamily = Railway)
                }

            }
        }
    } else {
        // Retrieving custom recipe data using data view model
        val title = dataViewModel.getString(NEW_RECIPE_TITLE)
        val category = dataViewModel.getString(NEW_RECIPE_CATEGORY)
        val difficulty = dataViewModel.getString(NEW_RECIPE_DIFFICULTY)
        val ingredients = dataViewModel.getStringList(NEW_RECIPE_INGREDIENTS)
        val photo = dataViewModel.getString(NEW_RECIPE_PHOTO)
        val preparation = dataViewModel.getStringList(NEW_RECIPE_PREPARATION)
        val rating = dataViewModel.getString(NEW_RECIPE_RATING)
        val totalTime = dataViewModel.getString(NEW_RECIPE_TIME)
        val userID = dataViewModel.getString(CURRENT_USER_ID)

        ingredients?.let { nonNullIngredients ->
            preparation?.let { nonNullPreparation ->
                val newRecipe = Recipe(
                    category = category,
                    difficulty = difficulty,
                    ingredients = nonNullIngredients,
                    photo = photo,
                    preparation = nonNullPreparation,
                    rating = rating,
                    title = title,
                    total_time = totalTime
                )
                if (userID != null) {
                    // Saving the recipe
                    savePrivateRecipe(userID, newRecipe)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                text = stringResource(id = R.string.exciting_news),
                fontSize = 21.sp
            )

            Text(
                text = stringResource(id = R.string.recipe_created),
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(30.dp),
            )

            Image(
                painter = painterResource(id = R.drawable.success_1),
                contentDescription = stringResource(id = R.string.success_picture),
                modifier = Modifier
                    .height(350.dp)
                    .width(330.dp),
                contentScale = ContentScale.FillHeight
            )

            FilledTonalButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = {
                    navController.navigate(Screen.Explore.route)
                    categorySelected.value = false
                },
                modifier = Modifier
                    .width(220.dp)
                    .height(50.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.done),
                    fontFamily = Railway
                )
            }
        }
    }
}

/**
 * This function is used to save a private recipe for a specific user to the Firestore database. It takes in the user ID
 * and a Recipe object and adds the recipe to the "privateRecipes" collection under the user's document in Firestore.
 *
 * @param userId The ID of the user that the private recipe belongs to.
 * @param recipe The Recipe object that represents the private recipe to be saved.
 */
fun savePrivateRecipe(userId: String, recipe: Recipe) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .document(userId)
        .collection("privateRecipes")
        .add(recipe)
        .addOnSuccessListener { documentReference ->
            Log.d("savePrivateRecipe", "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("savePrivateRecipe", "Error adding document", e)
        }
}
