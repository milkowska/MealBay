package uk.ac.aber.dcs.cs39440.mealbay.ui.user_recipes

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID

@Composable
fun PrivateCustomRecipesScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    val (isLoading, setIsLoading) = remember { mutableStateOf(true) }
    val (recipeList, setRecipeList) = remember { mutableStateOf(listOf<Recipe>()) }
    val context = LocalContext.current

    val userId = dataViewModel.getString(CURRENT_USER_ID)
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
                setRecipeList(customRecipes)

            },
            onEmpty = {
                Log.d("TEST333", "NO CUSTOM")
                Toast.makeText(context, "You have no custom recipes yet.", Toast.LENGTH_SHORT).show()
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
