package uk.ac.aber.dcs.cs39440.mealbay

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.aber.dcs.cs39440.mealbay.ui.collection.CollectionScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.ExploreScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.home.HomeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list.ListScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.MealBayTheme
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealBayTheme {

                val db = FirebaseFirestore.getInstance()
                val user: MutableMap<String, Any> = HashMap()
                user["firstName"] = "Robert"
                user["lastName"] = "Barski"

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            Log.d("FB", "onCreate: ${it.id}")
                        }.addOnFailureListener {
                            Log.d("FB", "onCreate: $it")
                        }
                    BuildNavigationGraph()
                }
            }
        }
    }
}

@Composable
private fun BuildNavigationGraph() {

    val navController = rememberNavController()

    var startRoute = Screen.Home.route

    /* if (configSet == false || configSet == null) {
         startRoute = Screen.Welcome.route
     }*/

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable(Screen.Home.route) { HomeScreenTopLevel(navController) }
        composable(Screen.Explore.route) {
            ExploreScreenTopLevel(
                navController
            )
        }
        composable(Screen.Collection.route) { CollectionScreenTopLevel(navController) }
        composable(Screen.List.route) { ListScreenTopLevel(navController) }
    }
}
