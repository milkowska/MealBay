package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold


@Composable
fun ExploreScreenTopLevel(
    navController: NavHostController,
) {
    ExploreScreen(navController, modifier = Modifier)
}

@Composable
fun ExploreScreen(
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
