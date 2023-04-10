package uk.ac.aber.dcs.cs39440.mealbay.ui.user_recipes

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
                        Toast.makeText(
                            context,
                            "You have no custom recipes yet.",
                            Toast.LENGTH_SHORT
                        ).show()
                        EmptyCustomRecipesScreen()
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
                    RecipeList(context, customRecipeList, navController, dataViewModel)
                }

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
                            stringResource(R.string.your_recipes),
                            fontFamily = Railway,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomRecipesList(customRecipes: List<Recipe>) {

}

fun EmptyCustomRecipesScreen() {


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
