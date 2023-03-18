package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen

@Composable
fun HomeScreenTopLevel(
    navController: NavHostController,
) {
    HomeScreen(navController, modifier = Modifier)
}

/*@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier,
) {
   *//* TopAppBar(
        title = {
            Text(
                text = "Welcome! ${FirebaseAuth.getInstance().currentUser}",
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                FirebaseAuth.getInstance().signOut().run {
                    navController.navigate(Screen.Login.route)
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout_icon),
                    contentDescription = "Logout"
                )

            }
        },
        backgroundColor = Color(0xFFFFDAD4)
    )
*//*

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

                Text(
                    text = "Welcome! ${FirebaseAuth.getInstance().currentUser}",
                    fontSize = 20.sp
                )

            }
        }
    }
}*/@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier,
) {
    TopLevelScaffold(
        navController = navController,
        topBar = {
            TopAppBar(
                title = { Text(text = "My App") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            backgroundColor = Color(0xFFFFDAD4)
            )
        },

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
                Text(
                    text = "Welcome! ",
                    fontSize = 20.sp
                )
            }
        }
    }
}
