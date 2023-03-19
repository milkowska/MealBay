package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
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
}*/
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier,
) {


    TopLevelScaffold(
        navController = navController,
        topBar = {
            TopAppBar(
                title = { Text(text = "Meal Bay") },
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
        },

        ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {


            val latestRecipe = remember { mutableStateOf<Recipe?>(null) }
            val collectionRef = FirebaseFirestore.getInstance().collection("recipes")


            collectionRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val latestDocument = documents.documents[0]
                        latestRecipe.value = latestDocument.toObject(Recipe::class.java)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("ERR", "Error getting documents: ", exception)
                }

            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.logosmall),
                            contentDescription = stringResource(id = R.string.logo),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(330.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 25.dp,
                                        bottomEnd = 25.dp
                                    )
                                ),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = stringResource(id = R.string.meal_of_the_day),
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 20.sp
                        )

                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.latest),
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 20.sp
                        )

                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.explore),
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 20.sp
                        )
                        Image(
                            painter = painterResource(id = R.drawable.people),
                            contentDescription = stringResource(id = R.string.home_pic),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(330.dp)
                                .padding(10.dp),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = stringResource(id = R.string.home_description),
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 18.sp
                        )

                        FilledTonalButton(
                            onClick = {
                                navController.navigate(Screen.Explore.route)
                            }, modifier = Modifier
                                .padding(start = 16.dp)
                                .width(180.dp)
                        ) {
                            Text(stringResource(R.string.take_me_there))
                        }

                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.check_the_latest_collection),
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 18.sp
                        )
                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.create_your_own),
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 18.sp
                        )

                        Image(
                            painter = painterResource(id = R.drawable.listpic),
                            contentDescription = stringResource(id = R.string.list_picture),
                            modifier = Modifier
                                .width(200.dp)
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        FilledTonalButton(
                            onClick = {
                                navController.navigate(Screen.List.route)
                            }, modifier = Modifier
                                .padding(start = 16.dp)
                                .width(180.dp)
                        ) {
                            Text(stringResource(R.string.take_me_there))
                        }

                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.discover),
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 18.sp
                        )

                        OutlinedButton(
                            onClick = {

                            }, modifier = Modifier
                                .padding(start = 16.dp)
                                .width(180.dp)
                        ) {
                            Text(stringResource(R.string.breakfast))
                        }

                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }

                if (latestRecipe.value != null) {
                    LatestRecipe(latestRecipe.value!!)
                }

                // Add a scrollbar on the right side of the screen
            }
        }
    }
}

@Composable
fun LatestRecipe(recipe: Recipe) {
    Text(
        text = "Latest Recipe: ${recipe.title}",

        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    Log.d("REC", "${recipe.title}")
}
