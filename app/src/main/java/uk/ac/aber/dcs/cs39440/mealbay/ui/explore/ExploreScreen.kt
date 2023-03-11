package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold



@Composable
fun ExploreScreenTopLevel(
    navController: NavHostController,
) {
    ExploreScreen(navController, modifier = Modifier)
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
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
            var context = LocalContext.current
            // on below line creating variable for list of data.
            var recipeList = mutableStateListOf<Recipe?>()
            // on below line creating variable for freebase database
            // and database reference.
            var db: FirebaseFirestore = FirebaseFirestore.getInstance()

            // on below line getting data from our database
            db.collection("recipes").get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    // after getting the data we are calling
                    // on success method
                    // and inside this method we are checking
                    // if the received query snapshot is empty or not.
                    if (!queryDocumentSnapshots.isEmpty) {
                        // if the snapshot is not empty we are
                        // hiding our progress bar and adding
                        // our data in a list.
                        // loadingPB.setVisibility(View.GONE)
                        val list = queryDocumentSnapshots.documents
                        for (d in list) {
                            // after getting this list we are passing that
                            // list to our object class.
                            val r: Recipe? = d.toObject(Recipe::class.java)
                            // and we will pass this object class inside
                            // our arraylist which we have created for list view.
                            recipeList.add(r)
                        }
                    } else {
                        // if the snapshot is empty we are displaying
                        // a toast message.
                        Toast.makeText(
                            context,
                            "No data found in Database",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // if we don't get any data or any error
                // we are displaying a toast message
                // that we donot get any data
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Fail to get the data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            // on below line we are calling method to display UI
            firebaseUI(LocalContext.current, recipeList)

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
    }*/
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun firebaseUI(context: Context, recipeList: SnapshotStateList<Recipe?>) {

    // on below line creating a column
    // to display our retrieved list.
    Column(
        // adding modifier for our column
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        // on below line adding vertical and
        // horizontal alignment for column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // on below line we are
        // calling lazy column
        // for displaying listview.
        LazyColumn {
            // on below line we are setting data
            // for each item of our listview.
            itemsIndexed(recipeList) { index, item ->
                // on below line we are creating
                // a card for our list view item.





                Card(
                    /*onClick = {
                        // inside on click we are
                        // displaying the toast message.
                        Toast.makeText(
                            context,
                            recipeList [index]?.title + " selected..",
                            Toast.LENGTH_SHORT
                        ).show()
                    },*/
                    // on below line we are adding
                    // padding from our all sides.
                    modifier = Modifier.padding(8.dp),

                    // on below line we are adding
                    // elevation for the card. //elevation = 6.sp
                ) {
                    // on below line we are creating
                    // a row for our list view item.
                    Column(
                        // for our row we are adding modifier
                        // to set padding from all sides.
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        // on below line inside row we are adding spacer
                        Spacer(modifier = Modifier.width(5.dp))
                        // on below line we are displaying course name.
                        recipeList[index]?.title?.let {
                            Text(
                                // inside the text on below line we are
                                // setting text as the language name
                                // from our modal class.
                                text = it,

                                // on below line we are adding padding
                                // for our text from all sides.
                                modifier = Modifier.padding(4.dp),

                                // on below line we are adding
                                // color for our text
                                textAlign = TextAlign.Center,

                                )
                        }
                        // adding spacer on below line.
                        Spacer(modifier = Modifier.height(5.dp))

                        // on below line displaying text for course duration
                        recipeList[index]?.rating?.let {
                            Text(
                                // inside the text on below line we are
                                // setting text as the language name
                                // from our modal class.
                                text = it,

                                // on below line we are adding padding
                                // for our text from all sides.
                                modifier = Modifier.padding(4.dp),

                                // on below line we are
                                // adding color for our text

                                textAlign = TextAlign.Center,

                                )
                        }
                        // adding spacer on below line.
                        Spacer(modifier = Modifier.width(5.dp))

                        // on below line displaying text for course description
                        recipeList[index]?.photo?.let {
                            Image(
                                painter = rememberImagePainter(it),
                                contentDescription = "Recipe Image",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(shape = RoundedCornerShape(4.dp))
                            )

/*
                            Text(
                                // inside the text on below line we are
                                // setting text as the language name
                                // from our modal class.
                                text = it,

                                // on below line we are adding padding
                                // for our text from all sides.
                                modifier = Modifier.padding(4.dp),

                                // on below line we are adding color for our text

                                textAlign = TextAlign.Center,

                                )*/
                        }
                    }
                }
            }

        }
    }
}

