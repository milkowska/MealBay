package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.data.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.RecipeList
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * This composable function is displaying either an empty screen if a private custom recipes collection does not exist/ is
 * empty or the list of recipes created by the user. When one recipe is clicked, it navigates to the recipe screen which
 * shows the actual data of this recipe.
 * It displays a row of buttons which are responsible for creating a new recipe or navigating to the main explore page where
 * the user can search for public recipes.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to retrieve user ID.
 */
@Composable
fun PrivateCustomRecipesScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    //to handle moments while the data is being fetched from the database before displaying it on the screen
    val (isLoading, setIsLoading) = remember { mutableStateOf(true) }
    val (isEmpty, setIsEmpty) = remember { mutableStateOf(false) }

    //storing private recipes collection
    val (customRecipeList, setCustomRecipeList) = remember { mutableStateOf(listOf<Recipe>()) }
    val context = LocalContext.current

    //getting the current user id
    val userId = dataViewModel.getString(CURRENT_USER_ID)

    TopLevelScaffold(
        navController = navController,
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
        {
            //Displaying a Circular progress indicator while the data is being fetched
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

            if (userId != null) {
                // fetching the private (custom) recipes data given the user ID.
                fetchCustomRecipes(
                    userId = userId,
                    onSuccess = { queryDocumentSnapshots ->
                        setIsLoading(false)
                        val list = queryDocumentSnapshots.documents
                        val customRecipes = list.mapNotNull { documentSnapshot ->
                            val r: Recipe? = documentSnapshot.toObject(Recipe::class.java)
                            if (r != null) {
                                r.id = documentSnapshot.id
                            }
                            r
                        }
                        //saving the data into a local variable
                        setCustomRecipeList(customRecipes)
                    },
                    // If the collection is empty or does not exist
                    onEmpty = {
                        setIsLoading(false)
                        setIsEmpty(true)
                        setCustomRecipeList(emptyList())
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

                if (isLoading) {
                    //Displaying a Circular progress indicator while the data is being fetched
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
                } else if (customRecipeList.isNotEmpty()) {
                    // Displaying the private recipes data
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        RecipeList(
                            customRecipeList,
                            navController,
                            dataViewModel,
                            showButtons = true,
                            userId = userId
                        )
                    }
                } else if (isEmpty) {
                    // Informing the user when the data is empty as it was not created before
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        Spacer(modifier = Modifier.height(20.dp))

                        Image(
                            painter = painterResource(id = R.drawable.no_data_available_transparent),
                            contentDescription = stringResource(id = R.string.no_data_image),
                            modifier = Modifier
                                .width(300.dp)
                                .height(260.dp),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = stringResource(id = R.string.no_recipes_created),
                            modifier = Modifier
                                .padding(top = 50.dp, start = 25.dp, end = 25.dp, bottom = 40.dp),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            //This button navigates user to creating the custom recipe screen
                            ElevatedButton(
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

                            //This button navigates to the explore screen where all public recipes can be seen
                            ElevatedButton(
                                onClick = {
                                    navController.navigate(Screen.Explore.route)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 15.dp)
                                    .weight(0.5f)
                            ) {
                                Text(
                                    stringResource(R.string.other_recipes),
                                    fontFamily = Railway,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * This function fetches the data from Firebase Datastore given the current user ID.
 * If the private collection of custom recipes exists, the data is fetched, if the data is empty a callback
 * function onEmpty is executed or onFailure if there is any failure during reading and fetching the data.
 *
 * @param userId The ID of the user for whom to fetch custom recipes.
 * @param onSuccess A callback function to handle the query snapshot if it is successful.
 * @param onFailure A callback function to handle the exception if the query fails.
 * @param onEmpty A callback function to handle the case where the query returns an empty result set.
 */
@Composable
fun fetchCustomRecipes(
    userId: String,
    onSuccess: (QuerySnapshot) -> Unit,
    onFailure: (Exception) -> Unit,
    onEmpty: () -> Unit,
) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    DisposableEffect(userId) {
        val listenerRegistration =
            db.collection("users")
                .document(userId)
                .collection("privateRecipes")
                .orderBy("title")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        onFailure(error)
                    } else if (value != null && !value.isEmpty) {
                        onSuccess(value)
                    } else {
                        onEmpty()
                    }
                }
        onDispose {
            listenerRegistration.remove()
        }
    }
}