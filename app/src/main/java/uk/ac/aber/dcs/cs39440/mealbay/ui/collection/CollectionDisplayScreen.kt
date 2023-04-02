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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.*
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDisplayScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var collection = dataViewModel.getString(COLLECTION_NAME)
    var userId = dataViewModel.getString(CURRENT_USER_ID)
    Scaffold(
        topBar = {
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
        }
    ) {
        if (userId != null) {
            CollectionList(userId, navController)
        }
    }
}

@Composable
fun CollectionList(
    //  collections: List<DocumentSnapshot>,
    //onItemClick: (DocumentSnapshot, String) -> Unit,
    userId: String,
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),

    ) {
    val recipes = remember { mutableStateListOf<Recipe>() }
    val selectedCollectionId = dataViewModel.getString(COLLECTION_ID)

    Log.d("DEBUG", "Selected collection ID: $selectedCollectionId")

    LaunchedEffect(userId) {
        val recipeIds = selectedCollectionId?.let {
            getRecipeIdsForCollection(it, userId)
        }
        recipes.clear()
        recipeIds?.let { getRecipesByIds(it) }?.let { recipes.addAll(it) }
    }

    LazyColumn {
        items(recipes) { recipe ->
            RecipeItem(recipe) {
                recipe.id?.let {
                    dataViewModel.saveString(it, RECIPE_ID)
                    Log.d("debug", "${recipe.id}, ${recipe.title}")
                }
                navController.navigate(Screen.Recipe.route)
            }
        }
    }
}

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

        Log.d("SNAPSHOT", "Snapshot: $snapshot")

        snapshot.documents.mapNotNull {
            it.getString("recipeId")
        }

    } catch (e: Exception) {
        Log.e("GET_RECIPE_IDS", "Error fetching recipe IDs", e)
        emptyList()
    }
}

suspend fun getRecipesByIds(recipeIds: List<String>): List<Recipe> {
    val firestore = Firebase.firestore
    val recipesRef = firestore.collection("recipesready")

    return try {
        val recipes = mutableListOf<Recipe>()

        for (recipeId in recipeIds) {
            val recipeSnapshot = recipesRef.document(recipeId).get().await()
            val recipe = recipeSnapshot.toObject(Recipe::class.java)

            if (recipe != null) {
                // Set the recipe ID manually
                recipe.id = recipeId
                recipes.add(recipe)
            }
        }

        recipes
    } catch (e: Exception) {
        Log.e("GET_RECIPES_BY_IDS", "Error fetching recipes", e)
        emptyList()
    }
}


@Composable
fun RecipeItem(
    recipe: Recipe,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(top = 5.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        val (photo, title, rating) = createRefs()

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
                    .height(120.dp)
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
                .padding(top = 2.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
                .constrainAs(rating) {
                    start.linkTo(title.start)
                    end.linkTo(title.end)
                    top.linkTo(title.bottom, 0.dp)
                },
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
