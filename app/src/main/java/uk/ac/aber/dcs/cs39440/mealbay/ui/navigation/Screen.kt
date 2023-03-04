package uk.ac.aber.dcs.cs39440.mealbay.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Collection : Screen("collection")
    object List : Screen("list")
    object Splash : Screen("onboarding")
}

val screens = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Collection,
    Screen.List,
    Screen.Splash
)