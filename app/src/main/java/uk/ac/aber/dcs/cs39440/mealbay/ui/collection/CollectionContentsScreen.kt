package uk.ac.aber.dcs.cs39440.mealbay.ui.collection

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.data.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.*
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * This composable function is a top level entry for RecipesListInCollection function and retrieves data about the user ID
 * and collection name. It contains TopAppBar composable to display the title of the collection that is selected.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to retrieve collection and userID.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDisplayScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var collection = dataViewModel.getString(COLLECTION_NAME)
    var userId = dataViewModel.getString(CURRENT_USER_ID)
    Scaffold {
        Column {
            TopAppBar(
                title = {
                    if (collection != null) {
                        Text(
                            text = collection,
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
            if (userId != null) {
                RecipesListInCollection(userId, navController)
            }
        }
    }
}

/**
 * This composable function is used to display the private content of selected collection of the user.
 *
 * @param userId The current user ID.
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to save the selected collection ID.
 */
@Composable
fun RecipesListInCollection(
    userId: String,
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
) {
    val selectedCollectionId = dataViewModel.getString(COLLECTION_ID)
    Log.d("CollectionList", "Selected collection ID: $selectedCollectionId")

    val recipes = remember { mutableStateListOf<Recipe>() }
    val isLoading = remember { mutableStateOf(true) }
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(userId, selectedCollectionId) {
        isLoading.value = true
        val recipeIds = selectedCollectionId?.let {
            getRecipeIdsForCollection(it, userId)
        }
        recipes.clear()
        recipeIds?.let { getRecipesByIds(it, userId) }?.let { recipes.addAll(it) }
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFFFDDB4))
        }
    } else {
        if (recipes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {

                Text(
                    text = stringResource(id = R.string.no_recipes),
                    fontSize = 19.sp,
                    modifier = Modifier
                        .padding(25.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                Image(
                    painter = painterResource(R.drawable.nodatahere),
                    contentDescription = "Empty Collection Image",
                    modifier = Modifier
                        .size(340.dp)

                )

                Text(
                    text = stringResource(id = R.string.gohere),
                    fontSize = 19.sp,
                    modifier = Modifier
                        .padding(start = 25.dp, end = 25.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(35.dp))
            }
        } else {
            LazyColumn {
                items(recipes) { recipe ->
                    RecipeItem(recipe,
                        onClick = {
                            recipe.id?.let {
                                // Saving recipe Id to fetch the right recipe object.
                                dataViewModel.saveString(it, RECIPE_ID)
                                Log.d("CollectionList", "${recipe.id}, ${recipe.title}")
                            }
                            navController.navigate(Screen.Recipe.route)
                        }, onDelete = { recipeId ->
                            openDialog.value = true
                            dataViewModel.saveString(recipeId, RECIPE_ID)
                        })
                }
            }
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.are_you_sure),
                            fontFamily = Railway,
                            fontSize = 22.sp,
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.pressing_confirm_two),
                            fontFamily = Railway,
                            fontSize = 16.sp
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            openDialog.value = false
                            val recipeId = dataViewModel.getString(RECIPE_ID)
                            val recipeToRemove =
                                recipes.find {
                                    it.id == recipeId
                                }
                            Log.d("CollectionList", "${dataViewModel.getString(RECIPE_ID)}")
                            if (selectedCollectionId != null && recipeToRemove != null) {
                                deleteRecipeFromCollection(
                                    userId, selectedCollectionId,
                                    recipeToRemove.id!!
                                ) {
                                    // Refresh the recipes list
                                    recipes.remove(recipeToRemove)
                                }
                            }
                        }) {
                            Text(
                                text = stringResource(id = R.string.confirm),
                                fontFamily = Railway,
                                fontSize = 16.sp
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            openDialog.value = false
                        }) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                fontFamily = Railway,
                                fontSize = 16.sp
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * getRecipeIdsForCollection function retrieves a list of recipe IDs for a given collection ID and user ID by querying
 * the Firestore database.
 *
 * @param collectionId The ID of the collection to retrieve recipe IDs for.
 * @param userId The ID of the user to retrieve the collection from.
 *
 * @return A list of recipe IDs associated with the given collection ID and user ID or an empty list if an error occurs.
 */
suspend fun getRecipeIdsForCollection(collectionId: String, userId: String): List<String> {
    val firestore = Firebase.firestore

    val userCollectionsRef = firestore
        .collection("users")
        .document(userId)
        .collection("collections")

    return try {
        val snapshot = userCollectionsRef
            .document(collectionId)
            .collection("recipes")
            .get()
            .await()
//        Log.d("SNAPSHOT", "Snapshot: $snapshot")

        snapshot.documents.mapNotNull {
            it.getString("recipeId")
        }

    } catch (e: Exception) {
        Log.e("GET_RECIPE_IDS", "Error fetching recipe IDs", e)
        emptyList()
    }
}

/**
 * This function retrieves asynchronously a list of Recipe objects from either the 'recipesready' or 'privateRecipes'
 * collections in Firebase Firestore based on a list of recipe IDs. It then maps the resulting snapshots to Recipe objects
 * and filters out any null values before returning the list.
 *
 * @param recipeIds The list of recipe IDs to retrieve.
 * @param userId The current user ID to retrieve private recipes.
 *
 * @return A list of Recipe objects matching the provided recipe IDs.
 */
suspend fun getRecipesByIds(recipeIds: List<String>, userId: String): List<Recipe> {
    val firestore = Firebase.firestore
    val recipesReadyRef = firestore.collection("recipesready")
    val privateRecipesRef =
        firestore.collection("users").document(userId).collection("privateRecipes")

    return try {
        val recipesDeferred = recipeIds.map { recipeId ->
            CoroutineScope(Dispatchers.IO).async {
                val recipesReadySnapshot = recipesReadyRef.document(recipeId).get().await()
                val privateRecipesSnapshot = privateRecipesRef.document(recipeId).get().await()

                val recipe = when {
                    recipesReadySnapshot.exists() -> {
                        val recipe = recipesReadySnapshot.toObject(Recipe::class.java)
                        recipe?.id = recipeId
                        recipe
                    }
                    privateRecipesSnapshot.exists() -> {
                        val recipe = privateRecipesSnapshot.toObject(Recipe::class.java)
                        recipe?.id = recipeId
                        recipe
                    }
                    else -> null
                }
                recipe
            }
        }

        recipesDeferred.awaitAll()
            .filterNotNull() // removes any null values
    } catch (e: Exception) {
        Log.e("GET_RECIPES_BY_IDS", "Error fetching recipes", e)
        emptyList()
    }
}

/**
 * This composable function displays a single recipe item as a card in a list. The recipe is represented by
 * the Recipe object passed in as a parameter.
 *
 * @param recipe The recipe object to be displayed as a card in the list.
 * @param onClick The lambda to be called when the user clicks on the recipe card.
 * @param onDelete The lambda to be called when the user clicks on the delete button.
 */
@Composable
fun RecipeItem(
    recipe: Recipe,
    onClick: () -> Unit,
    onDelete: (String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(top = 5.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        val (photo, title, category, deleteButton) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(photo) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, 5.dp)
                }
        ) {
            Image(
                painter = rememberImagePainter(recipe.photo),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .height(130.dp)
                    .width(155.dp)
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .padding(top = 10.dp),
                contentScale = ContentScale.Crop
            )
        }

        recipe.title?.let {
            Text(
                text = it,
                modifier = Modifier
                    .padding(2.dp)
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        end.linkTo(photo.start, 16.dp)
                        top.linkTo(photo.top, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    },
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Category: ${recipe.category}",
            modifier = Modifier
                .padding(top = 2.dp, start = 4.dp, end = 4.dp, bottom = 0.dp)
                .constrainAs(category) {
                    start.linkTo(title.start)
                    end.linkTo(title.end)
                    top.linkTo(title.bottom, 0.dp)
                },
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { onDelete(recipe.id!!) },
            modifier = Modifier
                .constrainAs(deleteButton) {
                    start.linkTo(category.start)
                    end.linkTo(category.end)
                    top.linkTo(category.bottom)
                }
        ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Recipe")
        }
    }
}

/**
 * deleteRecipeFromCollection function deletes a recipe from a user's collection in the Firestore database.
 *
 * @param userId The ID of the user who owns the collection.
 * @param collectionId The ID of the collection to delete the recipe from.
 * @param recipeId The ID of the recipe to be deleted.
 * @param onSuccess Callback function to be executed after successful deletion.
 */
fun deleteRecipeFromCollection(
    userId: String,
    collectionId: String,
    recipeId: String,
    onSuccess: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .document(userId)
        .collection("collections")
        .document(collectionId)
        .collection("recipes")
        .document(recipeId)
        .delete()
        .addOnSuccessListener {
            Log.d("deleteRecipe", "DocumentSnapshot successfully deleted!")
            onSuccess() // Refresh the list after successful deletion
        }
        .addOnFailureListener { e ->
            Log.w("deleteRecipe", "Error deleting document", e)
        }
}