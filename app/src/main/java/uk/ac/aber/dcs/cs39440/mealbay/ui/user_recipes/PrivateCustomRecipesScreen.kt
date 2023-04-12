package uk.ac.aber.dcs.cs39440.mealbay.ui.user_recipes

import android.util.Log
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
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.RecipeList
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

@Composable
fun PrivateCustomRecipesScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    val (isLoading, setIsLoading) = remember { mutableStateOf(true) }
    val (isEmpty, setIsEmpty) = remember { mutableStateOf(false) }
    val (customRecipeList, setCustomRecipeList) = remember { mutableStateOf(listOf<Recipe>()) }
    val context = LocalContext.current

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
                fetchCustomRecipes(
                    userId = userId,
                    onSuccess = { queryDocumentSnapshots ->
                        setIsLoading(false)
                        val list = queryDocumentSnapshots.documents
                        val customRecipes = list.mapNotNull { documentSnapshot ->
                            val r: Recipe? = documentSnapshot.toObject(Recipe::class.java)
                            if (r != null) {
                                r.id = documentSnapshot.id
                                Log.d("TEST333", "${r.title}")
                            }
                            r
                        }
                        setCustomRecipeList(customRecipes)

                    },
                    onEmpty = {
                        Log.d("TEST333", "NO CUSTOM")

                        setIsLoading(false)
                        setIsEmpty(true)
                        //EmptyCustomRecipesScreen(navController)
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

                if (!isLoading && customRecipeList.isNotEmpty()) {
                    setIsEmpty(false)
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        RecipeList(
                            context,
                            customRecipeList,
                            navController,
                            dataViewModel,
                            showButtons = true
                        )
                    }
                }

                if (customRecipeList.isEmpty() && isEmpty) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier
                                .fillMaxWidth()
                           // .padding(bottom = 4.dp)
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

@Composable
fun CustomRecipesList(customRecipes: List<Recipe>) {

}


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
