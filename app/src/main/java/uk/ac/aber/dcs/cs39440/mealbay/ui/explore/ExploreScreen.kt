package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway


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
            val (isLoading, setIsLoading) = remember { mutableStateOf(true) }
            val (recipeList, setRecipeList) = remember { mutableStateOf(listOf<Recipe>()) }
            val context = LocalContext.current

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(29.dp)
                    )
                }
            }

            FirebaseFetcher(
                onSuccess = { queryDocumentSnapshots ->
                    setIsLoading(false)
                    val list = queryDocumentSnapshots.documents
                    val recipes = list.mapNotNull { documentSnapshot ->
                        val r: Recipe? = documentSnapshot.toObject(Recipe::class.java)
                        if (r != null) {
                            r.id = documentSnapshot.id
                        }
                        r
                    }
                    setRecipeList(recipes)
                },
                onFailure = {
                    setIsLoading(false)
                    Toast.makeText(
                        context,
                        "Fail to get the data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            if (!isLoading) {
                firebaseUI(LocalContext.current, recipeList, navController, dataViewModel)
            }
        }
    }
}

@Composable
fun FirebaseFetcher(
    onSuccess: (QuerySnapshot) -> Unit,
    onFailure: () -> Unit
) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    DisposableEffect(Unit) {
        val listenerRegistration =
            db.collection("recipesready").addSnapshotListener { value, error ->
                if (error != null) {
                    onFailure()
                } else if (value != null && !value.isEmpty) {
                    onSuccess(value)
                }
            }

        onDispose {
            listenerRegistration.remove()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun firebaseUI(
    context: Context,
    recipeList: List<Recipe>,
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var recipeId: String?

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn {
                    // setting data for each item
                    itemsIndexed(recipeList) { index, item ->

                        ConstraintLayout(
                            modifier = Modifier
                                .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    recipeList[index]?.id?.let {
                                        recipeId = it
                                        dataViewModel.saveString(recipeId!!, RECIPE_ID)
                                        Log.d("TEST", "$recipeId")

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
                            recipeList[index]?.title?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .constrainAs(title) {
                                            start.linkTo(photo.end, 16.dp)
                                            end.linkTo(parent.end)
                                            top.linkTo(photo.top, margin = 16.dp)
                                            width = Dimension.fillToConstraints
                                        },
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

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
                                            start.linkTo(title.start)
                                            end.linkTo(title.end)
                                            top.linkTo(
                                                title.bottom,
                                                0.dp
                                            )
                                        },
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
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
            ) {

                ElevatedButton(
                    onClick = {
                        navController.navigate(Screen.Create.route)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp)
                        .weight(0.5f)

                ) {
                    Text(
                        stringResource(R.string.create_new),
                        fontFamily = Railway
                    )
                }

                ElevatedButton(
                    onClick = {
                        navController.navigate(Screen.Custom.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp)
                        .weight(0.5f)
                ) {
                    Text(
                        stringResource(R.string.your_recipes),
                        fontFamily = Railway,
                        )
                }


            }
        }
    }
}
