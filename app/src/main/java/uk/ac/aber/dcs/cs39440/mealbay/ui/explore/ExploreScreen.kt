package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.CircularProgressBar


@Composable
fun ExploreScreenTopLevel(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()

) {
    ExploreScreen(navController, dataViewModel = dataViewModel)
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
fun ExploreScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {

    TopLevelScaffold(
        navController = navController,
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
        {

            var context = LocalContext.current
            var recipeList = mutableStateListOf<Recipe?>()
            var db: FirebaseFirestore = FirebaseFirestore.getInstance()
            // on below line getting data from our database


            db.collection("recipes").get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    // after getting the data we are calling on success method and inside this method we are checking
                    // if the received query snapshot is empty or not.
                    if (!queryDocumentSnapshots.isEmpty) {

                        // if the snapshot is not empty we are hiding our progress bar and adding our data in a list.
                        //TODO circularprogressbar!!

                        val list = queryDocumentSnapshots.documents
                        for (d in list) {
                            val r: Recipe? = d.toObject(Recipe::class.java)
                            if (r != null) {
                                r.id = d.id
                            }
                            recipeList.add(r)
                        }

                    } else {
                        // if the snapshot is empty
                        Toast.makeText(
                            context,
                            "No data found in Database",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Fail to get the data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            firebaseUI(LocalContext.current, recipeList, navController, dataViewModel)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun firebaseUI(
    context: Context,
    recipeList: SnapshotStateList<Recipe?>,
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var recipe_id: String?

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            // on below line we are setting data for each item.
            itemsIndexed(recipeList) { index, item ->

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {

                    ConstraintLayout(
                        modifier = Modifier
                            .padding(top = 5.dp, start = 8.dp)
                            .fillMaxWidth()
                            .clickable {
                                recipeList[index]?.id?.let {
                                    recipe_id = it
                                    dataViewModel.saveString(recipe_id!!, RECIPE_ID)
                                }
                                navController.navigate(Screen.Recipe.route)
                            }
                    ) {
                        val (photo, title, rating) = createRefs()
                        Box(
                            modifier = Modifier
                                .constrainAs(photo) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top, 5.dp)

                                }
                        ) {
                            // Display the recipe photo
                            recipeList[index]?.photo?.let {
                                Image(
                                    painter = rememberImagePainter(it),
                                    contentDescription = "Recipe Image",
                                    modifier = Modifier
                                        .height(120.dp)
                                        .width(155.dp)
                                        .fillMaxSize()
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .padding(top = 10.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // Define the recipe title constraints
                        recipeList[index]?.title?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .constrainAs(title) {
                                        start.linkTo(photo.end, 4.dp)
                                        end.linkTo(parent.end)
                                        top.linkTo(parent.top, 25.dp)
                                    },
                                fontSize = 20.sp
                            )
                        }
                        // Define the recipe rating constraints
                        recipeList[index]?.rating?.let {
                            Text(
                                text = "Rating: $it",
                                modifier = Modifier
                                    .padding(
                                        top = 2.dp,
                                        start = 4.dp,
                                        end = 4.dp,
                                        bottom = 4.dp
                                    )
                                    .constrainAs(rating) {
                                        start.linkTo(photo.end, 4.dp)
                                        end.linkTo(parent.end)
                                        top.linkTo(title.bottom, 2.dp)
                                    },
                                fontSize = 16.sp,
                            )
                        }

                    }

                }


            }
        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ElevatedButton(
                onClick = {
                },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .width(180.dp)
            ) {
                Text(stringResource(R.string.add_filter))
            }

            ElevatedButton(
                onClick = {
                    navController.navigate(Screen.Create.route)
                }, modifier = Modifier
                    .padding(start = 16.dp)
                    .width(180.dp)
            ) {
                Text(stringResource(R.string.create_new))
            }
        }

    }
}


/*

fun saveNewRecipe(recipe: Recipe) {
    val document = firestore.collection("recipes").document()
    val set = document.set(recipe)
        set.addOnSuccessListener {
            Log.d("FB", "new recipe saved")
        }
        set.addOnFailureListener {
            Log.d("FB", "save failed")
        }
}
*/


/*
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
}*//*
        }*/
