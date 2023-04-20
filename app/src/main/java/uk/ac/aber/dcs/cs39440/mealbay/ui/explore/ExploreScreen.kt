package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.data.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * This is a composable function that is used to display the public recipe collection called recipesready stored in the
 * firebase. If the data is still being fetched, a circular progress indicator is displayed. Once the data is retrieved
 * a recipe data function is called to display the data in a specific structure.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to save recipe ID.
 */
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
fun ExploreScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    TopLevelScaffold(
        navController = navController,
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
        {
            val (isLoading, setIsLoading) = remember { mutableStateOf(true) }
            val (recipeList, setRecipeList) = remember { mutableStateOf(listOf<Recipe>()) }
            val context = LocalContext.current

            // While the data is being retrieved the circular progress indicator appears
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(29.dp)
                    )
                }
            }

            // Fetching recipe data and mapping it to Recipe object
            FirebaseFetcher(
                onSuccess = { queryDocumentSnapshots ->
                    setIsLoading(false)
                    val list = queryDocumentSnapshots.documents
                    val recipes = list.mapNotNull { documentSnapshot ->
                        val r: Recipe? = documentSnapshot.toObject(Recipe::class.java)
                        if (r != null) {
                            r.id = documentSnapshot.id
                        }
                        r
                    }
                    setRecipeList(recipes)
                },
                onFailure = {
                    setIsLoading(false)
                    Toast.makeText(
                        context,
                        "Fail to get the data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            // Displaying the data in a specific layout
            if (!isLoading) {
                RecipeData(recipeList, navController, dataViewModel)
            }
        }
    }
}

/**
 * This is a composable function that sets up a listener to a Firebase Firestore collection called "recipesready" and
 * fetches the data ordering it by field "title".
 * The DisposableEffect is used to remove the listener when the composable function is disposed.
 *
 * @param onSuccess A callback function that will be called when the data is successfully fetched from the Firestore collection.
 * @param onFailure A callback function that will be called when an error occurs while fetching data from the Firestore collection.
 */
@Composable
fun FirebaseFetcher(
    onSuccess: (QuerySnapshot) -> Unit,
    onFailure: () -> Unit
) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    DisposableEffect(Unit) {
        val listenerRegistration =
            db.collection("recipesready").orderBy("title").addSnapshotListener { value, error ->
                if (error != null) {
                    onFailure()
                } else if (value != null && !value.isEmpty) {
                    onSuccess(value)
                }
            }

        onDispose {
            listenerRegistration.remove()
        }
    }
}

/**
 * Composable function to display a list of recipes.
 *
 * @param recipeList List of recipes to be displayed.
 * @param navController NavController to navigate to different screens.
 * @param dataViewModel Instance of DataViewModel to get and set data across multiple composables.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeData(
    recipeList: List<Recipe>,
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var recipeId: String?

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
        ) {

            Box(modifier = Modifier.weight(1f)) {

                LazyColumn {
                    // setting data for each item was by recipeList
                    itemsIndexed(recipeList) { index, item ->

                        ConstraintLayout(
                            modifier = Modifier
                                .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    recipeList[index]?.id?.let {
                                        recipeId = it
                                        dataViewModel.saveString(recipeId!!, RECIPE_ID)
                                        Log.d("TEST", "$recipeId")
                                    }
                                    navController.navigate(Screen.Recipe.route)
                                }
                        ) {
                            val (photo, title, rating) = createRefs()
                            Box(
                                modifier = Modifier
                                    .constrainAs(photo) {
                                        start.linkTo(parent.start)
                                        top.linkTo(parent.top, 5.dp)

                                    }
                            ) {
                                recipeList[index]?.photo?.let {
                                    Image(
                                        painter = rememberImagePainter(it),
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
                            }
                            recipeList[index]?.title?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .constrainAs(title) {
                                            start.linkTo(photo.end, 16.dp)
                                            end.linkTo(parent.end)
                                            top.linkTo(photo.top, margin = 16.dp)
                                            width = Dimension.fillToConstraints
                                        },
                                    fontSize = 19.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            recipeList[index]?.rating?.let {
                                Text(
                                    text = "Rating: $it",
                                    modifier = Modifier
                                        .padding(
                                            top = 2.dp,
                                            start = 4.dp,
                                            end = 4.dp,
                                            bottom = 4.dp
                                        )
                                        .constrainAs(rating) {
                                            start.linkTo(title.start)
                                            end.linkTo(title.end)
                                            top.linkTo(
                                                title.bottom,
                                                0.dp
                                            )
                                        },
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val userId = dataViewModel.getString(CURRENT_USER_ID)
                val userHasRecipes = remember(userId) {
                    var hasRecipes = false
                    val firestore = Firebase.firestore
                    val privateRecipesRef =
                        userId?.let {
                            firestore.collection("users").document(it).collection("privateRecipes")
                        }
                    privateRecipesRef?.get()?.addOnSuccessListener { querySnapshot ->
                        hasRecipes = !querySnapshot.isEmpty
                      //  Log.e("RecipeData", "$hasRecipes")
                    }?.addOnFailureListener { exception ->
                        //Log.e("RecipeData", "Error checking for user's recipes", exception)
                    }
                    hasRecipes
                }

                FilledTonalButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        navController.navigate(Screen.Create.route)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp)
                        .weight(0.5f)
                ) {
                    Text(
                        stringResource(R.string.create_new),
                        fontFamily = Railway
                    )
                }

                FilledTonalButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        navController.navigate(Screen.Custom.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp)
                        .weight(0.5f),
                    // enabled = !userHasRecipes
                ) {
                    Text(
                        stringResource(R.string.your_recipes),
                        fontFamily = Railway,
                    )
                }
            }
        }
    }
}