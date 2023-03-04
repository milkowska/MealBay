package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.cs39440.mealbay.ui.collection.CollectionScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.ExploreScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.home.HomeScreenTopLevel
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
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
            ExploreScreenTopLevel(
                navController
            )
        }
        composable(Screen.Collection.route) { CollectionScreenTopLevel(navController) }
        composable(Screen.List.route) { ListScreenTopLevel(navController) }
    }
}
