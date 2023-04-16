package uk.ac.aber.dcs.cs39440.mealbay.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import uk.ac.aber.dcs.cs39440.mealbay.model.MealViewModel
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import java.time.LocalTime
import androidx.compose.runtime.getValue
import java.time.LocalDateTime
import java.util.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.platform.LocalContext
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_CATEGORY
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.DrawerValue
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.aber.dcs.cs39440.mealbay.data.User
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_NAME
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * This is a composable function that is a top-level entry point for the Home Screen feature, which displays the main
 * screen of the app.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to retrieve the current user ID.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenTopLevel(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    HomeScreen(navController, dataViewModel)
}

/**
 * This composable function is used to display the data on the Home screen of the app. It contains a Top app bar which
 * has an icon that can be used to log the current user out. This screen includes plenty of composables to represent
 * other functionalities of the app and data.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to save recipe ID or category ID.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {

    val mealViewModel = viewModel<MealViewModel>()
    val currentHour = remember { mutableStateOf(LocalTime.now().hour) }
    val currentMinute = remember { mutableStateOf(LocalTime.now().minute) }
    val mealOfTheDay by mealViewModel.mealOfTheDay.observeAsState(null)
    val currentDayOfWeek = remember { mutableStateOf(LocalDateTime.now().dayOfWeek) }
    val formattedDayOfWeek = currentDayOfWeek.value.toString().toLowerCase(Locale.getDefault())
        .capitalize(Locale.getDefault())
    val userID = dataViewModel.getString(CURRENT_USER_ID)
    var recipeId: String?
    val categories = remember {
        listOf(
            "Breakfast",
            "Beverage",
            "Dessert",
            "Dinner",
            "Lunch",
            "Salad",
            "Soup",
            "Vegan",
            "Vegetarian"
        )
    }
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (userID != null) {
        getUserByUserID(
            userID,
            onSuccess = { user ->
                // Handle the retrieved user data
                val displayName = user?.displayName ?: "Unknown"
                dataViewModel.saveString(displayName, "CURRENT_USER_NAME")
                Log.d("FirebaseExample", "Display name: $displayName")
            },
            onFailure = { exception ->
                // Handle the error
                Log.e("FirebaseExample", "Error fetching user: ${exception.message}")
            }
        )
    }
    // Updates the current hour, minute, and day of the week values every minute.
    LaunchedEffect(Unit) {
        val updateJob = launch {
            while (true) {
                delay(60_000L)
                val now = LocalTime.now()
                currentHour.value = now.hour
                currentMinute.value = now.minute
                currentDayOfWeek.value = LocalDateTime.now().dayOfWeek
            }
        }

        // If the current time is exactly midnight (00:00) a new meal is fetched from the server
        if (currentHour.value == 0 && currentMinute.value == 0) {
            mealViewModel.fetchMealOfTheDay()
        }
    }

    ModalDrawer(
        drawerContent = {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hello ${dataViewModel.getString(CURRENT_USER_NAME)}!",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(15.dp))

                Text(text = stringResource(R.string.have_you_on_board), fontSize = 14.sp)

                Image(
                    painter = painterResource(id = R.drawable.welcome),
                    contentDescription = stringResource(id = R.string.logo),
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = stringResource(R.string.press_to_log_out), fontSize = 14.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut().run {

                            navController.navigate(Screen.Login.route)
                            Toast.makeText(
                                context,
                                "You have been logged out.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out),
                        fontFamily = Railway
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Image(
                    painter = painterResource(id = R.drawable.person_looking),
                    contentDescription = stringResource(id = R.string.logo),
                    modifier = Modifier
                        .height(210.dp),
                    contentScale = ContentScale.Crop
                )

                Button(
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.close),
                        fontFamily = Railway
                    )
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = false,
        scrimColor = Color(0x66000000),
    ) {
        TopLevelScaffold(
            navController = navController,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.open_drawer)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(id = R.string.meal_bay),
                            modifier = Modifier
                                .padding(end = 60.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    backgroundColor = Color(0xFFFFDAD4)
                )


                /*  TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut().run {

                            navController.navigate(Screen.Login.route)
                            Toast.makeText(
                                context,
                                "You have been logged out.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout_icon),
                            contentDescription = stringResource(id = R.string.logout)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.meal_bay),
                        modifier = Modifier
                            .padding(end = 60.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                backgroundColor = Color(0xFFFFDAD4)
            )*/
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
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            contentPadding = PaddingValues(horizontal = 16.dp)
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

                                Spacer(modifier = Modifier.height(10.dp))
                                // Displaying the proposed meal for the day
                                Text(
                                    text = "Meal for this $formattedDayOfWeek",
                                    modifier = Modifier
                                        .padding(20.dp),
                                    fontSize = 20.sp,
                                    color = Color(0xFF9C4234)
                                )

                                Text(
                                    text = stringResource(id = R.string.meal_for_today),
                                    modifier = Modifier
                                        .padding(bottom = 4.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                ConstraintLayout(
                                    modifier = Modifier
                                        .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            mealOfTheDay?.id?.let {
                                                recipeId = it
                                                dataViewModel.saveString(recipeId!!, RECIPE_ID)
                                            }
                                            navController.navigate(Screen.Recipe.route)
                                        }
                                ) {
                                    val (photo, title, rating, progress) = createRefs()

                                    Box(
                                        modifier = Modifier
                                            .constrainAs(progress) {
                                                top.linkTo(parent.top)
                                                start.linkTo(parent.start)
                                                end.linkTo(parent.end)
                                                bottom.linkTo(parent.bottom)
                                            }
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (mealOfTheDay == null) {
                                            CircularProgressIndicator()
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .constrainAs(photo) {
                                                start.linkTo(parent.start)
                                                top.linkTo(parent.top, 5.dp)
                                            }
                                    ) {
                                        mealOfTheDay?.photo?.let {
                                            Image(
                                                painter = rememberImagePainter(it),
                                                contentDescription = "Recipe Image",
                                                modifier = Modifier
                                                    .height(140.dp)
                                                    .width(165.dp)
                                                    .fillMaxSize()
                                                    .clip(shape = RoundedCornerShape(10.dp))
                                                    .padding(top = 20.dp),
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
                                            textAlign = TextAlign.Center
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
                                                    )
                                                },
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Divider(
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 20.dp)
                                )


                                Text(
                                    text = stringResource(id = R.string.explore),
                                    modifier = Modifier
                                        .padding(bottom = 12.dp),
                                    fontSize = 20.sp,
                                    color = Color(0xFF9C4234)
                                )

                                Text(
                                    text = stringResource(id = R.string.search_description),
                                    modifier = Modifier
                                        .padding(bottom = 4.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.people),
                                    contentDescription = stringResource(id = R.string.home_pic),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(330.dp)
                                        .padding(25.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = stringResource(id = R.string.search_description_three),
                                    modifier = Modifier
                                        .padding(bottom = 20.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.navbar_explore),
                                    contentDescription = stringResource(id = R.string.navigation_bar_picture),
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.FillWidth
                                )

                                Text(
                                    text = stringResource(id = R.string.search_description_two),
                                    modifier = Modifier
                                        .padding(top = 20.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Divider(
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 15.dp)
                                )

                                Text(
                                    text = stringResource(id = R.string.add_your_own),
                                    modifier = Modifier
                                        .padding(bottom = 12.dp),
                                    fontSize = 20.sp,
                                    color = Color(0xFF9C4234)
                                )

                                Text(
                                    text = stringResource(id = R.string.add_one),
                                    modifier = Modifier
                                        .padding(bottom = 20.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.food_paper),
                                    contentDescription = stringResource(id = R.string.food),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 20.dp),
                                    contentScale = ContentScale.FillWidth
                                )

                                Divider(
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )

                                Text(
                                    text = stringResource(id = R.string.make_your_own),
                                    modifier = Modifier
                                        .padding(bottom = 12.dp),
                                    fontSize = 20.sp,
                                    color = Color(0xFF9C4234)
                                )

                                Text(
                                    text = stringResource(id = R.string.collection_feature_description_one),
                                    modifier = Modifier
                                        .padding(bottom = 20.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.board),
                                    contentDescription = stringResource(id = R.string.board),
                                    modifier = Modifier
                                        .width(190.dp)
                                        .height(230.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = stringResource(id = R.string.collection_feature_description_two),
                                    modifier = Modifier
                                        .padding(bottom = 20.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.navbar_collection),
                                    contentDescription = stringResource(id = R.string.navigation_bar_picture),
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.FillWidth
                                )

                                Text(
                                    text = stringResource(id = R.string.collection_feature_description_three),
                                    modifier = Modifier
                                        .padding(top = 12.dp, bottom = 12.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Divider(
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 15.dp)
                                )

                                Text(
                                    text = stringResource(id = R.string.create_your_own),
                                    modifier = Modifier
                                        .padding(bottom = 12.dp),
                                    fontSize = 20.sp,
                                    color = Color(0xFF9C4234)
                                )

                                Text(
                                    text = stringResource(id = R.string.shopping_list_feature_description),

                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.tiny_chef_t),
                                    contentDescription = stringResource(id = R.string.list_picture),
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.FillWidth
                                )

                                Text(
                                    text = stringResource(id = R.string.shopping_list_feature_description_two),
                                    modifier = Modifier
                                        .padding(bottom = 20.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.navbar_s),
                                    contentDescription = stringResource(id = R.string.navigation_bar_picture),
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.FillWidth
                                )

                                Text(
                                    text = stringResource(id = R.string.shopping_list_feature_description_three),
                                    modifier = Modifier
                                        .padding(top = 20.dp),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Divider(
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 15.dp)
                                )

                                Text(
                                    text = stringResource(id = R.string.discover),
                                    modifier = Modifier
                                        .padding(bottom = 20.dp),
                                    fontSize = 20.sp,
                                    color = Color(0xFF9C4234)
                                )
                            }
                            items(categories.chunked(2)) { categoryRow ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    categoryRow.forEach { category ->
                                        ElevatedButton(
                                            onClick = {
                                                dataViewModel.saveString(category, CURRENT_CATEGORY)

                                                navController.navigate(Screen.Filtered.route)
                                                Log.d("CategoryClicked", "category is $category")

                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(8.dp)
                                        ) {
                                            Text(text = category)
                                        }
                                    }
                                }
                            }
                        }
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

fun getUserByUserID(userID: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .document(userID)
        .get()
        .addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)
            onSuccess(user)
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}
