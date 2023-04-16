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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material.Scaffold
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.data.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import uk.ac.aber.dcs.cs39440.mealbay.model.MealViewModel
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * Top level composable that wraps the [RecipeScreen] composable. This is used to ensure that the
 * RecipeScreen composable can be used as a destination in the navigation graph.
 *
 * @param navController The NavHostController used for navigation.
 * @param dataViewModel The DataViewModel used for storing and retrieving data across multiple composables.
 * @param mealViewModel The MealViewModel used for fetching and storing meals data.
 */
@Composable
fun RecipeScreenTopLevel(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
    mealViewModel: MealViewModel = viewModel()
) {
    RecipeScreen(navController, dataViewModel, mealViewModel)
}

/**
 * Composable function that displays the details of a recipe based on the recipe ID.
 *
 * @param navController the navigation controller used to navigate to other screens.
 * @param dataViewModel the view model used to retrieve and save data.
 * @param mealViewModel the view model used for meal planning.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
    mealViewModel: MealViewModel = viewModel()
) {

    val id = dataViewModel.getString(RECIPE_ID)
    val userId = dataViewModel.getString(CURRENT_USER_ID)
    //Log.d("RecipeScreen", "the id is $id or ${dataViewModel.getString(RECIPE_ID)}")
    if (id != null) {
        if (userId != null) {
            FetchRecipeByID(navController, documentId = id, userId = userId, mealViewModel)
        }
    }
}

/**
 * This function fetches a recipe by its ID from the database and displays its content in the UI.
 *
 * @param navController A NavHostController object to allow for navigation.
 * @param documentId The documentId of the recipe to be fetched.
 * @param userId The userId of the current user.
 * @param mealViewModel A MealViewModel object for fetching the recipe from the database.
 */
@Composable
fun FetchRecipeByID(
    navController: NavHostController,
    documentId: String,
    userId: String,
    mealViewModel: MealViewModel
) {
    val documentState = remember { mutableStateOf<Recipe?>(null) }

    // Observe the LiveData returned by getDocumentById and update the documentState object when it changes
    LaunchedEffect(documentId) {
        mealViewModel.getDocumentById(documentId, userId).observeForever { recipe ->
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

/**
 * A Composable function that displays the content of a recipe.
 *
 * @param navController The navController for navigating to other screens.
 * @param recipe The recipe to be displayed.
 * @param dataViewModel The ViewModel for handling data.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
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
        // Using Data View Model do get the data
        val isUserCollectionEmpty by dataViewModel.isUserCollectionEmpty.observeAsState(initial = true)
        val userId = dataViewModel.getString(CURRENT_USER_ID)

        val (showCollections, setShowCollections) = remember { mutableStateOf(false) }
        val collectionsFetched = remember { mutableStateOf(listOf<DocumentSnapshot>()) }
        val isLoading = remember { mutableStateOf(true) }
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val showDialog = remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                        backgroundColor = Color(0xFFFFB4A7)
                    )
                },
                scaffoldState = scaffoldState,
                content = {
                    LazyColumn {
                        item {
                            Image(
                                painter = rememberImagePainter("${recipe.photo}"),
                                contentDescription = "Recipe image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp)
                                    .padding(15.dp)
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
                                            fontSize = 17.sp,
                                            fontFamily = Railway,
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
                                    fontSize = 25.sp,
                                    color = Color(0xFF9C4234)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        items(recipe.ingredients.size) { index ->
                            val item = recipe.ingredients[index]
                            val splitItems = item.split("|")

                            splitItems.forEach { splitItem ->
                                Text(
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color(0xFF9C4234))) {
                                            append("  • ") // bullet point

                                        }
                                        withStyle(SpanStyle(fontSize = 20.sp)) {
                                            append(splitItem) // item text
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(10.dp)
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
                                    color = Color(0xFF9C4234),
                                    modifier = Modifier.padding(start = 20.dp),
                                )
                            }
                        }

                        items(recipe.preparation.size) { index ->
                            val item = recipe.preparation[index]

                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontSize = 22.sp,
                                            color = Color(0xFF9C4234)
                                        )
                                    ) {
                                        append("  ${index + 1}) ")
                                    }
                                    withStyle(SpanStyle(fontSize = 19.sp)) {
                                        append(item)
                                        Spacer(modifier = Modifier.padding(6.dp))
                                    }
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                },

                )
        }

        if (showCollections) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .size(width = 350.dp, height = 450.dp)
                    .align(Alignment.Center),
                backgroundColor = Color(0xFFFFDED8),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column {
                        IconButton(
                            onClick = { setShowCollections(false) },
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(end = 0.dp, top = 4.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }

                        dataViewModel.getString(RECIPE_ID)?.let {
                            CollectionList(
                                collections = collectionsFetched.value,
                                onItemClick = { collection, recipeId ->
                                    if (userId != null) {
                                        checkAndAddRecipeToCollection(
                                            collection.id,
                                            recipeId,
                                            userId,
                                            showDialog
                                        ) {
                                            scope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("Recipe has been added to the collection.")
                                            }
                                            setShowCollections(false)
                                        }
                                        Log.d(
                                            "AAA",
                                            "${collection.id}, recipe:  $recipeId, user:  $userId "
                                        )
                                    }
                                },
                                recipeId = it
                            )
                        }
                    }
                }

                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text(text = stringResource(id = R.string.recipe_already)) },
                        text = { Text(text = stringResource(id = R.string.recipe_already_two)) },
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = Color(0xFFFFDAD6),
                        confirmButton = {
                            Button(
                                onClick = { showDialog.value = false },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFDB7465),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .width(80.dp)
                                    .padding(end = 5.dp, bottom = 5.dp),
                                content = { Text(stringResource(id = R.string.ok)) }
                            )
                        },
                    )
                }
            }
        }
    }
}


/**
 *  CheckAndAddRecipeToCollection function checks if a recipe is already added to a collection and adds it if it's not.
 *
 * @param collectionID the ID of the collection to add the recipe to.
 * @param recipeId the ID of the recipe to add to the collection.
 * @param userId the ID of the user that owns the collection.
 * @param showDialog a mutable state to control whether to show a dialog when the recipe is already added.
 * @param onSuccess a callback to be invoked if the recipe is added successfully.
 */
@OptIn(ExperimentalMaterialApi::class)
fun checkAndAddRecipeToCollection(
    collectionID: String,
    recipeId: String,
    userId: String,
    showDialog: MutableState<Boolean>,
    onSuccess: () -> Unit
) {
    val userCollectionsRef = Firebase.firestore
        .collection("users")
        .document(userId)
        .collection("collections")

    val recipeRef = userCollectionsRef
        .document(collectionID)
        .collection("recipes")
        .document(recipeId)

    recipeRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                showDialog.value = true
            } else {
                addRecipeToCollection(collectionID, recipeId, userId, onSuccess)
            }
        }
        .addOnFailureListener { e ->
            Log.w("CheckRecipe", "Error checking recipe", e)
        }
}

/**
 * Composable function that displays a list of user collections.
 *
 * @param collections a list of DocumentSnapshot representing the user collections.
 * @param onItemClick a function that will be called when a collection is clicked.
 * @param recipeId the ID of the recipe to be added to the collection.
 */
@Composable
fun CollectionList(
    collections: List<DocumentSnapshot>,
    onItemClick: (DocumentSnapshot, String) -> Unit,
    recipeId: String
) {
    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(400.dp)
                .padding(start = 5.dp, bottom = 40.dp, end = 5.dp),
            backgroundColor = Color(0xFFDBB2AA),
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(collections) { collection ->
                    val collectionName = collection.getString("name") ?: "Unnamed"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(collection, recipeId) }
                            .height(48.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = collectionName)
                    }
                    Divider()
                }
            }
        }
    }
}

/**
 * addRecipeToCollection function adds the given recipe to the specified collection.
 *
 * @param collectionID the ID of the collection where the recipe should be added.
 * @param recipeId the ID of the recipe to add.
 * @param userId the ID of the user that owns the collection.
 * @param onSuccess a callback function to be called upon successful completion of the operation.
 */
@OptIn(ExperimentalMaterialApi::class)
fun addRecipeToCollection(
    collectionID: String,
    recipeId: String,
    userId: String,
    onSuccess: () -> Unit
) {
    val userCollectionsRef = userId?.let {
        Firebase.firestore
            .collection("users")
            .document(it)
            .collection("collections")
    }

    val recipeRef = userCollectionsRef
        .document(collectionID)
        .collection("recipes")
        .document(recipeId) // recipeId -> the document ID

    val data = mapOf(
        "recipeId" to recipeId
    )

    recipeRef.set(data)
        .addOnSuccessListener {
            // Log.d("AddRecipe", "Recipe added with ID: ${recipeRef.id}")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.w("AddRecipe", "Error adding recipe", e)
        }
}
