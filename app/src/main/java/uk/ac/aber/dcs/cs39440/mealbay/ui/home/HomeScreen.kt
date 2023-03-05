package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

            }
        }
    }
}
