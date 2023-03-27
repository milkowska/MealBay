package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.cs39440.mealbay.ui.collection.CollectionScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.CreateRecipeScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.ExploreScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.RecipeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.home.HomeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.login.LoginScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.onboarding.SplashScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.CategoryScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.IngredientsScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.PreparationScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list.ListScreenTopLevel

/**
 * A Composable function that sets up the navigation flow of the app using Jetpack Navigation.
 */
@Composable
fun Navigation() {
    val navController = rememberNavController()

     // Defining the navigation graph
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Home.route) { HomeScreenTopLevel(navController) }
        composable(Screen.Explore.route) {
            ExploreScreen(
                navController)
        }
        composable(Screen.Collection.route) { CollectionScreenTopLevel(navController) }
        composable(Screen.List.route) { ListScreenTopLevel(navController) }
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Recipe.route) { RecipeScreenTopLevel(navController) }
        composable(Screen.Create.route) { CreateRecipeScreen(navController) }
        composable(Screen.Ingredients.route) { IngredientsScreen(navController)}
        composable(Screen.Preparation.route) { PreparationScreen(navController)}
        composable(Screen.Category.route) { CategoryScreen(navController)}
    }
}
