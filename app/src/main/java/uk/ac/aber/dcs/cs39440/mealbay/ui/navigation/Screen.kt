package uk.ac.aber.dcs.cs39440.mealbay.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Collection : Screen("collection")
    object List : Screen("list")
    object Splash : Screen("onboarding")
    object Login : Screen("login")
    object Recipe : Screen("recipe")
    object Create : Screen("create")
    object Ingredients: Screen("ingredients")
    object Preparation: Screen("preparation")
    object Category: Screen("category")
    object Custom: Screen("custom")
}

val screens = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Collection,
    Screen.List,
)