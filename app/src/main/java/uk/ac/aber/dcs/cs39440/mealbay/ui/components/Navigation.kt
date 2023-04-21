package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uk.ac.aber.dcs.cs39440.mealbay.ui.collection.CollectionDisplayScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.collection.CollectionScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.CreateRecipeScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.ExploreScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.RecipeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.home.FilteredByCategoryScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.home.HomeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.login.LoginScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.onboarding.SplashScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.CategoryScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.IngredientsScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.PreparationScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list.ListScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.PrivateCustomRecipesScreen

/**
 * A Composable function that sets up the navigation flow of the app using Jetpack Navigation.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(currentRoute: MutableState<String>) {
    val navController = rememberNavController()

    // Listen to the NavController's destination changes
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = destination.route ?: ""
        }
    }
    val user = Firebase.auth.currentUser
    var start = Screen.Splash.route

    start = if (user != null) {
        Screen.Home.route
    } else {
        Screen.Splash.route
    }
    // Defining the navigation graph
    NavHost(
        navController = navController,
        startDestination = start
    ) {
        composable(Screen.Home.route) { HomeScreenTopLevel(navController) }
        composable(Screen.Explore.route) {
            ExploreScreen(
                navController
            )
        }
        composable(Screen.Collection.route) { CollectionScreenTopLevel(navController) }
        composable(Screen.List.route) { ListScreenTopLevel(navController) }
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Recipe.route) { RecipeScreenTopLevel(navController) }
        composable(Screen.Create.route) { CreateRecipeScreen(navController) }
        composable(Screen.Ingredients.route) { IngredientsScreen(navController) }
        composable(Screen.Preparation.route) { PreparationScreen(navController) }
        composable(Screen.Category.route) { CategoryScreen(navController) }
        composable(Screen.Custom.route) { PrivateCustomRecipesScreen(navController) }
        composable(Screen.Filtered.route) { FilteredByCategoryScreen(navController) }
        composable(Screen.ColDisplay.route) { CollectionDisplayScreen(navController) }
    }
}
