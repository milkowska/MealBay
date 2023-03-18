package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import uk.ac.aber.dcs.cs39440.mealbay.ui.collection.CollectionScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.CreateRecipeScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.ExploreScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.MealViewModel
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.RecipeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.home.HomeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.login.LoginScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.onboarding.SplashScreen
import uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list.ListScreenTopLevel

@Composable
fun Navigation() {
    val navController = rememberNavController()

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
        composable(Screen.Create.route) { CreateRecipeScreen(navController)}
    }
}
