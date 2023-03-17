package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import uk.ac.aber.dcs.cs39440.mealbay.R
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

    if (id != null) {
        YourComposableFunction(documentId = id, mealViewModel)
    }

}

@Composable
fun YourComposableFunction(documentId: String, mealViewModel: MealViewModel) {
    val documentState = remember { mutableStateOf<Recipe?>(null) }

    // Observe the LiveData returned by getDocumentById and update the documentState object when it changes
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

    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowRecipeContent(recipe: Recipe) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ingredients = recipe.ingredients
        val preparation = recipe.preparation

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        recipe.title?.let {
                            Text(
                                text = it,
                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* handle navigation icon click */ }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    backgroundColor = Color(0xFFFFDAD4)
                )
            }
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn {
                item {
                    Image(
                        painter = rememberImagePainter("${recipe.photo}"),
                        contentDescription = "Recipe image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(330.dp)
                            .padding(15.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Difficulty: ${recipe.difficulty}", modifier = Modifier.weight(0.5f))
                        Text("Rating: ${recipe.rating}", modifier = Modifier.weight(0.5f))
                    }
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Total time: ${recipe.total_time}",
                            modifier = Modifier.weight(1f)
                        )
                        ElevatedButton(
                            onClick = { /* Handle button click */ },
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .width(180.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add icon",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "Add",
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Divider(modifier = Modifier.height(1.dp))
                }
                item {
                    Text(
                        stringResource(R.string.ingredients),
                        fontSize = 25.sp,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                items(recipe.ingredients.size) { index ->
                    val item = recipe.ingredients[index]
                    Text(
                        buildAnnotatedString {

                            append("  • ") // bullet point
                            withStyle(SpanStyle(fontSize = 20.sp)) {
                                append(item) // item text

                                Spacer(modifier = Modifier.padding(5.dp))
                            }
                        }
                    )
                }
            }
        }
    }
}