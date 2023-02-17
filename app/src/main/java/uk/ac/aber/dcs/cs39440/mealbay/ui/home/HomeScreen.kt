package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold

@Composable
fun HomeScreenTopLevel(
    navController: NavHostController,
) {
    HomeScreen(navController, modifier = Modifier)
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier,
) {
    TopLevelScaffold(
        navController = navController,
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

            }
        }
    }
}
