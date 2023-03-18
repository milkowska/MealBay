package uk.ac.aber.dcs.cs39440.mealbay.ui.components


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * a Template for the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopLevelScaffold(
    navController: NavHostController,
    floatingActionButton: @Composable () -> Unit = { },
    topBar: @Composable () -> Unit = { },
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
){
    Scaffold(
        topBar = topBar,
        bottomBar = {
            MainPageNavigationBar(navController)
        },
        floatingActionButton = floatingActionButton,

        content = { innerPadding ->
            pageContent(innerPadding)
        }
    )
}