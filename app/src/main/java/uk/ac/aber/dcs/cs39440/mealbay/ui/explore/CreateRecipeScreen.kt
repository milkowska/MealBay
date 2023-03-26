package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.*
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var recipeName by rememberSaveable { mutableStateOf("") }
    var totalTime by rememberSaveable { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(0) }
    var rating by remember { mutableStateOf(0) }

    var isErrorInTextField by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_new_recipe),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Explore.route) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                backgroundColor = Color(0xFFFFDAD4)
            )
        }) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.peoplecooking),
                contentDescription = stringResource(id = R.string.people_cooking),
                modifier = Modifier
                    .height(220.dp)
                    .padding(20.dp),
                contentScale = ContentScale.FillHeight
            )

            Text(
                text = stringResource(R.string.name_of_new_recipe),
                modifier = Modifier.padding(10.dp)
            )

            TextField(
                value = recipeName,
                label = {
                    Text(text = stringResource(R.string.title))
                },
                onValueChange = {
                    recipeName = it
                    isErrorInTextField = recipeName.isEmpty()
                },
                modifier = Modifier.width(340.dp),
                singleLine = true,
                isError = isErrorInTextField,
                )

            Text(
                text = stringResource(R.string.total_time_of_new_recipe),
                modifier = Modifier.padding(20.dp)
            )

            TextField(
                value = totalTime,
                label = {
                    Text(text = stringResource(R.string.total_time))
                },
                onValueChange = {
                    totalTime = it
                    isErrorInTextField = totalTime.isEmpty()
                },
                modifier = Modifier.width(340.dp),
                singleLine = true,
                isError = isErrorInTextField,

                )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.difficulty_of_new_recipe),
                fontSize = 18.sp
            )
            RatingBar(rating = difficulty) {
                difficulty = it
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.rating_of_new_recipe),
                fontSize = 18.sp
            )

            RatingBar(rating = rating) {
                rating = it
            }

            Spacer(modifier = Modifier.height(15.dp))

            ElevatedButton(
                onClick = {
                    //if enable then nav -> ingredfietns

                    dataViewModel.saveString(recipeName, NEW_RECIPE_TITLE)
                    dataViewModel.saveString(totalTime, NEW_RECIPE_TIME)
                    val difficultyInString = getDifficulty(difficulty)
                    if (difficultyInString != null) {
                        dataViewModel.saveString(difficultyInString, NEW_RECIPE_DIFFICULTY)
                    }
                    val ratingInString = getRating(rating)
                    if (ratingInString != null) {
                        dataViewModel.saveString(ratingInString, NEW_RECIPE_RATING)
                    }

                    navController.navigate(Screen.Ingredients.route)
                },
                modifier = Modifier.width(180.dp)
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}


@Composable
fun RatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row {
        for (i in 1..5) {
            val icon = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .clickable { onRatingChanged(i) }
                    .size(24.dp)
                    .padding(2.dp)
            )
        }
    }
}

@Composable
private fun AddIngredientsScreen() {
    TODO("Not yet implemented")
}

fun createRecipe(
    category: String?,
    difficulty: String?,
    ingredients: List<String>,
    photo: String?,
    preparation: List<String>,
    rating: String?,
    title: String?,
    totalTime: String?,
    firestore: FirebaseFirestore
) {
    val recipe = Recipe(
        category = category,
        difficulty = difficulty,
        ingredients = ingredients,
        photo = photo,
        preparation = preparation,
        rating = rating,
        title = title,
        total_time = totalTime
    )

    firestore.collection("recipes")
        .add(recipe)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "Recipe document added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error adding recipe document", e)
        }
}

/**
 * This function converts the difficulty as integer into a String value, so that one start equals very easy,
 * 2 -> easy,
 * 3 -> medium,
 * 4 -> hard,
 * 5 -> very hard.
 */
fun getDifficulty(difficultyInInt: Int): String? {
    val difficultyLevels = listOf("very easy", "easy", "medium", "hard", "very hard")
    return difficultyLevels.getOrNull(difficultyInInt - 1)
}

/**
 * This function converts the rating as integer into a String value.
 */
fun getRating(ratingInInt: Int): String? {
    val ratingLevels = listOf("1.0", "2.0", "3.0", "4.0", "5.0")
    return ratingLevels.getOrNull(ratingInInt - 1)
}