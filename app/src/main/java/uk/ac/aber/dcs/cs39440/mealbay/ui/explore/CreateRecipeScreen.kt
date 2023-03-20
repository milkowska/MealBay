package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

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
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(navController: NavHostController) {
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
