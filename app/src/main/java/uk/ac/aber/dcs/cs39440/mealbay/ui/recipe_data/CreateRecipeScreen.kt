package uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data

import android.annotation.SuppressLint
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
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
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
    var difficulty by rememberSaveable { mutableStateOf(0) }
    var rating by rememberSaveable { mutableStateOf(0) }

    val isButtonEnabled by remember { derivedStateOf { recipeName.isNotEmpty() && totalTime.isNotEmpty() && difficulty > 0 && rating > 0 } }

    var isErrorInTextField by remember {
        mutableStateOf(false)
    }
    val maxChars = 26
    val maxCharsLonger = 52

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
                      //  navController.navigate(Screen.Explore.route)
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

                TextField(
                    value = recipeName,
                    label = {
                        Text(text = stringResource(R.string.title))
                    },
                    onValueChange = {
                        if (it.length <= maxCharsLonger) {
                            recipeName = it
                            isErrorInTextField = recipeName.isEmpty()
                        }
                    },
                    modifier = Modifier.width(340.dp),
                    singleLine = true,
                    isError = isErrorInTextField,
                    trailingIcon = {
                        Text(
                            text = "${maxCharsLonger - recipeName.length}",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )

                Text(
                    text = stringResource(R.string.total_time_of_new_recipe),
                    modifier = Modifier.padding(10.dp)
                )

                TextField(
                    value = totalTime,
                    label = {
                        Text(text = stringResource(R.string.total_time))
                    },
                    onValueChange = {
                        if (it.length <= maxChars) {
                            totalTime = it
                            isErrorInTextField = totalTime.isEmpty()
                        }
                    },
                    modifier = Modifier.width(340.dp),
                    singleLine = true,
                    isError = isErrorInTextField,
                    trailingIcon = {
                        Text(
                            text = "${maxChars - totalTime.length}",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
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
 * A custom RatingBar composable that displays a row of stars for rating selection.
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