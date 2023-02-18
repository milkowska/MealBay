package uk.ac.aber.dcs.cs39440.mealbay.ui.components


import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.screens


/**
 * Implementation of the navigation bar.
 */
@Composable
fun MainPageNavigationBar(
    navController: NavController
) {
    val icons = mapOf(
        Screen.Home to IconGroup(
            filledIcon = painterResource(id = R.drawable.ic_filled_home_21),
            outlinedIcon = painterResource(R.drawable.ic_outline_home_21),
            label = stringResource(id = R.string.home_icon)
        ),
        Screen.Explore to IconGroup(
            filledIcon = painterResource(R.drawable.ic_icons8_search),
            outlinedIcon = painterResource(R.drawable.ic_outline_search_21),
            label = stringResource(id = R.string.search_icon)
        ),
        Screen.Collection to IconGroup(
            filledIcon = painterResource(R.drawable.ic_filled_bookmarks_21),
            outlinedIcon = painterResource(R.drawable.ic_outline_bookmarks_21),
            label = stringResource(id = R.string.collection_icon)
        ),
        Screen.List to IconGroup(
            filledIcon = painterResource(R.drawable.ic_filled_shopping_cart_21),
            outlinedIcon = painterResource(R.drawable.ic_outline_shopping_cart_21),
            label = stringResource(id = R.string.list_icon)
        ),
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            val labelText = icons[screen]!!.label
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = (
                                if (isSelected)
                                    icons[screen]!!.filledIcon
                                else
                                    icons[screen]!!.outlinedIcon),
                        contentDescription = labelText
                    )
                },
                label = { Text(text = labelText) },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}