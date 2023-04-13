package uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.*
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.maxRecipeNameCharsLength
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.maxTotalTimeCharsLength
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.minCharsLength
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen

/**
 * This composable function is displaying the screen where the user can interact and add values to the new recipe that he
 * is creating. It prompts for a recipe name, total time of preparation, giving these two a length limit and difficulty and
 * personal rating for this recipe by clicking on the amount of stars that is satisfactiory for the user ( there are five stars
 * available, 1 is the smallest value while 5 is the largest). Once the data is collected correctly and the user presses a next
 * button it navigates to the Ingredients screen.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to save the recipe title, total time, rating and difficulty data.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    // Variables that store recipe name, total time, difficulty and rating data for a new recipe that user creates
    var recipeName by rememberSaveable { mutableStateOf("") }
    var totalTime by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableStateOf(0) }
    var rating by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current

    // for enabling the button that navigates to the next step only if all necessary data is entered
    val isButtonEnabled by remember { derivedStateOf { recipeName.isNotEmpty() && totalTime.isNotEmpty() && difficulty > 0 && rating > 0 } }

    //Handling an error state in the text field
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
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                backgroundColor = Color(0xFFFFDAD4)
            )
        }) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(id = R.drawable.peoplecooking),
                    contentDescription = stringResource(id = R.string.people_cooking),
                    modifier = Modifier
                        .height(200.dp)
                        .padding(10.dp),
                    contentScale = ContentScale.FillHeight
                )

                Text(
                    text = stringResource(R.string.name_of_new_recipe),
                    modifier = Modifier.padding(10.dp)
                )

                // Text field to enter a title value for the recipe, limiting it to 52 characters long
                TextField(
                    value = recipeName,
                    label = {
                        Text(text = stringResource(R.string.title))
                    },
                    onValueChange = {
                        if (it.length <= maxRecipeNameCharsLength) {
                            recipeName = it
                            isErrorInTextField = recipeName.isEmpty()
                        }
                    },
                    modifier = Modifier.width(340.dp),
                    singleLine = true,
                    isError = isErrorInTextField,
                    trailingIcon = {
                        Text(
                            text = "${maxRecipeNameCharsLength - recipeName.length}",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )

                Text(
                    text = stringResource(R.string.total_time_of_new_recipe),
                    modifier = Modifier.padding(10.dp)
                )

                // Text field to enter a total time value for the recipe preparation, limiting it to 26 characters long
                TextField(
                    value = totalTime,
                    label = {
                        Text(text = stringResource(R.string.total_time))
                    },
                    onValueChange = {
                        if (it.length <= maxTotalTimeCharsLength) {
                            totalTime = it
                            isErrorInTextField = totalTime.isEmpty()
                        }
                    },
                    modifier = Modifier.width(340.dp),
                    singleLine = true,
                    isError = isErrorInTextField,
                    trailingIcon = {
                        Text(
                            text = "${maxTotalTimeCharsLength - totalTime.length}",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.difficulty_of_new_recipe),
                    fontSize = 18.sp
                )

                // This rating bar is for setting a difficulty level from very easy to very hard
                RatingBar(rating = difficulty) {
                    difficulty = it
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.rating_of_new_recipe),
                    fontSize = 18.sp
                )

                // This rating bar is for setting a rating from 1 star to 5
                RatingBar(rating = rating) {
                    rating = it
                }

                Spacer(modifier = Modifier.height(15.dp))

                // A next button that saves the data of a recipe name, total time, difficulty and rating into dataViewModel
                ElevatedButton(
                    onClick = {
                        if (recipeName.trim().length < minCharsLength || totalTime.trim().length < minCharsLength) {
                            Toast.makeText(
                                context,
                                "The details are too short.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
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
                        }
                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .width(180.dp)
                        .height(50.dp),
                ) {
                    Text(stringResource(R.string.next))
                }
            }

        }
    }
}

/**
 * A composable function that displays a row of stars for rating selection.
 *
 * @param rating The current selected rating (1 to 5)
 * @param onRatingChanged A callback function that will be invoked when the user changes the rating by clicking a star
 */
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

/**
 * This function converts the difficulty as integer into a String value, so that one start equals -> very easy,
 * 2 -> easy,
 * 3 -> medium,
 * 4 -> hard,
 * 5 -> very hard.
 *
 * @param difficultyInInt Difficulty value as an Integer
 * @return difficulty level as a String 
 */
fun getDifficulty(difficultyInInt: Int): String? {
    val difficultyLevels = listOf("very easy", "easy", "medium", "hard", "very hard")
    return difficultyLevels.getOrNull(difficultyInInt - 1)
}

/**
 * This function converts the rating as integer into a String value.
 *
 * @param ratingInInt rating value as an integer
 *
 * @return rating level as a String value
 */
fun getRating(ratingInInt: Int): String? {
    val ratingLevels = listOf("1.0", "2.0", "3.0", "4.0", "5.0")
    return ratingLevels.getOrNull(ratingInInt - 1)
}