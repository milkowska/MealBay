package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
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
import androidx.compose.material.Card
import androidx.compose.material.icons.filled.Close
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID

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
    Log.d("MYTAG", "the id is $id or ${dataViewModel.getString(RECIPE_ID)}")
    if (id != null) {
        FetchRecipeByID(navController, documentId = id, mealViewModel)
    }
}

@Composable
fun FetchRecipeByID(
    navController: NavHostController,
    documentId: String,
    mealViewModel: MealViewModel
) {
    val documentState = remember { mutableStateOf<Recipe?>(null) }

    // Observe the LiveData returned by getDocumentById and update the documentState object when it changes
    LaunchedEffect(documentId) {
        mealViewModel.getDocumentById(documentId).observeForever { recipe ->
            Log.d("FetchRecipeByID", "result: $recipe")
            documentState.value = recipe
        }
    }

    Log.d("FetchRecipeByID", "documentState: ${documentState.value}")

    if (documentState.value == null) {
        Log.d("FetchRecipeByID", "documentState value is null!")
    } else {
        ShowRecipeContent(navController, recipe = documentState.value!!)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ShowRecipeContent(
    navController: NavHostController,
    recipe: Recipe,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        val isUserCollectionEmpty by dataViewModel.isUserCollectionEmpty.observeAsState(initial = true)
        var userId = dataViewModel.getString(CURRENT_USER_ID)
        val (showCollections, setShowCollections) = remember { mutableStateOf(false) }
        val collectionsFetched = remember { mutableStateOf(listOf<DocumentSnapshot>()) }
        val isLoading = remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val ingredients = recipe.ingredients
            val preparation = recipe.preparation


            LaunchedEffect(userId) {
                val db = FirebaseFirestore.getInstance()
                if (userId != null) {
                    db.collection("users")
                        .document(userId)
                        .collection("collections")
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                Log.w("DisplayCollections", "Error fetching collections", error)
                            } else {
                                value?.let {
                                    collectionsFetched.value = it.documents
                                    isLoading.value = false
                                }
                            }
                        }
                }
            }
            LaunchedEffect(userId) {
                if (userId != null) {
                    dataViewModel.checkUserCollectionEmpty(userId)
                }
            }


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
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        backgroundColor = Color(0xFFFFDAD4)
                    )
                }
            ) {

                LazyColumn {
                    item {
                        Image(
                            painter = rememberImagePainter("${recipe.photo}"),
                            contentDescription = "Recipe image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                                .padding(25.dp)
                                .clip(RoundedCornerShape(25.dp)),
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
                            Text(
                                "Difficulty: ${recipe.difficulty}",
                                modifier = Modifier.weight(0.5f),
                                fontSize = 18.sp
                            )
                            Text(
                                "Rating: ${recipe.rating}",
                                modifier = Modifier.weight(0.5f),
                                fontSize = 18.sp
                            )
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
                                modifier = Modifier.weight(1f),
                                fontSize = 18.sp
                            )

                            ElevatedButton(
                                onClick = {
                                    setShowCollections(!showCollections)
                                },
                                enabled = !isUserCollectionEmpty,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .width(170.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add icon",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        "Add",
                                        modifier = Modifier.padding(start = 4.dp),
                                        fontSize = 17.sp
                                    )
                                }
                            }

                        }

                    }
                    item {
                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.ingredients),
                                fontSize = 25.sp

                            )
                        }
                    }
                    items(recipe.ingredients.size) { index ->
                        val item = recipe.ingredients[index]
                        val splitItems = item.split("|") // Split the string using the delimiter

                        splitItems.forEach { splitItem ->
                            Text(
                                buildAnnotatedString {
                                    append("  • ") // bullet point
                                    withStyle(SpanStyle(fontSize = 20.sp)) {
                                        append(splitItem) // item text
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.preparation),
                                fontSize = 25.sp,
                                modifier = Modifier.padding(start = 20.dp),
                            )
                        }
                    }

                    items(recipe.preparation.size) { index ->
                        val item = recipe.preparation[index]

                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontSize = 22.sp)) {
                                    append("  ${index + 1}) ")
                                }
                                withStyle(SpanStyle(fontSize = 19.sp)) {
                                    append(item)
                                    Spacer(modifier = Modifier.padding(6.dp))
                                }
                            }
                        )
                    }
                }


            }
        }
        if (showCollections) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .size(width = 350.dp, height = 450.dp)
                    .align(Alignment.Center),
                backgroundColor = Color(0xFFFFB4A4),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                Column {
                    IconButton(
                        onClick = { setShowCollections(false) },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 4.dp, top = 4.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }

                        CollectionList(
                            collections = collectionsFetched.value,
                            onItemClick = { collection ->
                                // Handle the collection item click here
                                println("Clicked on: ${collection.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionList(
    collections: List<DocumentSnapshot>,
    onItemClick: (DocumentSnapshot) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .width(300.dp) // Set the width constraint
                .height(400.dp) // Set the height constraint
                .padding(16.dp),
            backgroundColor = Color(0xFFFFB4A7),

            ) {

            LazyColumn {
                items(collections) { collection ->
                    val collectionName = collection.getString("name") ?: "Unnamed"
                    Text(
                        text = collectionName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onItemClick(collection) }
                    )
                }
            }

        }
    }
}
