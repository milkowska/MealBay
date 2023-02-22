package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold


@Composable
fun ExploreScreenTopLevel(
    navController: NavHostController,
) {
    ExploreScreen(navController, modifier = Modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavHostController,
    modifier: Modifier,
) {

    var displayBoxCard by remember { mutableStateOf(false) }
    TopLevelScaffold(
        navController = navController,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    displayBoxCard = true
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
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
                if (displayBoxCard) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(16.dp),
                          //  elevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Hello, this is a box card!")
                                Button(
                                    onClick = { displayBoxCard = false },
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    Text("Close")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
