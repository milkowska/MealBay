package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.explore.MealViewModel
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import java.time.LocalTime
import androidx.compose.runtime.getValue
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenTopLevel(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    HomeScreen(navController, modifier = Modifier, dataViewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier,
    dataViewModel: DataViewModel = hiltViewModel()

) {
    val mealViewModel = viewModel<MealViewModel>()
    val currentHour = remember { mutableStateOf(LocalTime.now().hour) }
    val currentMinute = remember { mutableStateOf(LocalTime.now().minute) }
    val mealOfTheDay by mealViewModel.mealOfTheDay.observeAsState(null)
    val currentDayOfWeek = remember { mutableStateOf(LocalDateTime.now().dayOfWeek) }
    val formattedDayOfWeek = currentDayOfWeek.value.toString().toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())

    var recipeId: String?

    LaunchedEffect(Unit) {
        val updateJob = launch {
            while (true) {
                delay(60_000L) // Update every minute
                val now = LocalTime.now()
                currentHour.value = now.hour
                currentMinute.value = now.minute
                currentDayOfWeek.value = LocalDateTime.now().dayOfWeek
            }
        }

        if (currentHour.value == 0 && currentMinute.value == 0) {
            mealViewModel.fetchMealOfTheDay()
        }
    }
    TopLevelScaffold(
        navController = navController,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.meal_bay)) },
                navigationIcon = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut().run {
                            navController.navigate(Screen.Login.route)
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout_icon),
                            contentDescription = stringResource(id = R.string.logout)
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
                        Spacer(modifier = Modifier.height(15.dp))

                        Text(
                            text = "Meal for this $formattedDayOfWeek",
                            modifier = Modifier
                                .padding(2.dp),
                            fontSize = 20.sp
                        )

                        ConstraintLayout(
                            modifier = Modifier
                                .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    mealOfTheDay?.id?.let {
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
                                mealOfTheDay?.photo?.let {
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

                            mealOfTheDay?.title?.let {
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
                                    textAlign = TextAlign.Center // Add text alignment to center the title
                                )
                            }

                            mealOfTheDay?.rating?.let {
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
                                            ) // Remove top margin to align the rating directly below the title
                                        },
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center // Add text alignment to center the rating
                                )
                            }
                        }

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
            }
        }
    }
}
