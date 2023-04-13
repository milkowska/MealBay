package uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list

import android.util.Log
import  androidx.compose.runtime.mutableStateListOf
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextField
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.minCharsLength
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * This is a composable function that is a top-level entry point for the Shopping List feature. It retrieves the current
 * id of the user that is saved using data view model.
 *
 * @param navController The navigation controller for navigating between screens.
 * @param dataViewModel The DataViewModel used to retrieve the current user ID.
 */
@Composable
fun ListScreenTopLevel(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
) {
    val userId = dataViewModel.getString(CURRENT_USER_ID)
    if (userId != null) {
        ListScreen(navController, userId = userId)
    }
}

/**
 *  This composable function uses a modal bottom sheet that contains a text field where the user can enter a shopping list
 *  value. The value must be at least 3 characters long to be successfully added to a list.
 *  @param shoppingList The current user's shopping list as a SnapshotStateList of strings.
 *  @param userId The current user ID.
 *  @param onListChanged The callback function to be called when the shopping list is changed.
 */
@Composable
fun BottomSheet(
    shoppingList: SnapshotStateList<String>,
    userId: String,
    onListChanged: () -> Unit,
) {
    val context = LocalContext.current
    var item by rememberSaveable { mutableStateOf("") }
    var isErrorInTextField by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = stringResource(id = R.string.add_an_item),
            fontSize = 23.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        TextField(
            value = item,
            label = {
                Text(text = stringResource(R.string.add_an_item))
            },
            onValueChange = {
                item = it
                isErrorInTextField = item.isEmpty()
            },
            modifier = Modifier.width(360.dp),
            singleLine = true,
            isError = isErrorInTextField,
        )

        Spacer(modifier = Modifier.height(100.dp))

        ElevatedButton(
            enabled = item.isNotEmpty(),
            onClick = {
                if (item.trim() == "") {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_LONG).show()
                    isErrorInTextField = true
                } else if (item.trim().length < minCharsLength) {
                    Toast.makeText(context, "The item length is too short!", Toast.LENGTH_LONG)
                        .show()
                    isErrorInTextField = true
                } else {
                    shoppingList.add(item)
                    saveShoppingList(userId, shoppingList)
                    item = ""
                    onListChanged()
                }
            }, modifier = Modifier
                .width(220.dp)
                .height(50.dp)
        ) {
            Text(stringResource(R.string.add), fontFamily = Railway)
        }
    }
}

/**
 * This composable function is used to display the shopping list UI. The list can be cleared at any time and it is
 * saved into a private collection in firebase datastore.
 *
 * @param navController The navigation controller for navigating between screens.
 * @param userId The current user ID.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListScreen(
    navController: NavHostController,
    userId: String
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()
    val shoppingList = remember { mutableStateListOf<String>() }
    val openDialogOnSave = remember { mutableStateOf(false) }
    var emptyList by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Handle back press when the bottom sheet is open
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    LaunchedEffect(userId) {
        fetchShoppingList(userId, shoppingList) { emptyList = shoppingList.isEmpty() }
    }
    // Function to update the empty list state
    fun updateEmptyListState() {
        emptyList = shoppingList.isEmpty()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BottomSheet(
                shoppingList,
                userId,
                onListChanged = ::updateEmptyListState
            )
        },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        TopLevelScaffold(
            navController = navController,
        ) { innerPadding ->

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {

                val lazyColumnHeight = maxHeight - 100.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(lazyColumnHeight)

                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Show an empty cart image and message if the list is empty
                        if (emptyList) {
                            item {
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentSize(align = Alignment.Center)
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .size(300.dp),

                                        painter = painterResource(id = R.drawable.emptycart),
                                        contentDescription = stringResource(id = R.string.shopping_list_is_empty),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            item {

                                Spacer(modifier = Modifier.height(40.dp))

                                Text(
                                    text = stringResource(id = R.string.empty_shopping_list),
                                    fontSize = 21.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }
                        } else {
                            item {
                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = stringResource(id = R.string.my_shopping_list),
                                    fontSize = 21.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )

                                Divider(
                                    startIndent = 2.dp,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(top = 5.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            items(shoppingList) { item ->
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color(0xFF9C4234))) {
                                            append("  • ")
                                        }
                                        append(item)
                                    },
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(start = 15.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(80.dp)

                    ) {
                        FloatingActionButton(
                            backgroundColor = (Color(0xFFFFDAD4)),
                            onClick = {
                                coroutineScope.launch {
                                    if (sheetState.isVisible) sheetState.hide()
                                    else sheetState.show()
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 15.dp)

                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add an item")
                        }

                        ElevatedButton(
                            onClick = {
                                openDialogOnSave.value = true
                            },
                            enabled = shoppingList.isNotEmpty(),
                            modifier = Modifier
                                .width(220.dp)
                                .height(50.dp)
                                .padding(start = 30.dp, end = 25.dp)
                                .align(Alignment.BottomStart),
                        ) {
                            Text(
                                text = stringResource(id = R.string.clear),
                                fontFamily = Railway
                            )
                        }
                    }
                }
            }
            if (openDialogOnSave.value) {

                AlertDialog(
                    onDismissRequest = {
                        openDialogOnSave.value = false
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.are_you_sure),
                            fontFamily = Railway
                        )
                    },
                    text = {
                        Text(
                            stringResource(R.string.warning_three),
                            fontFamily = Railway,
                            fontSize = 15.sp
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                clearShoppingList(userId)
                                shoppingList.clear()
                                updateEmptyListState()
                                openDialogOnSave.value = false
                                Toast.makeText(
                                    context,
                                    "Your shopping list has been cleared.",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                        ) {
                            Text(
                                stringResource(R.string.confirm),
                                fontFamily = Railway
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openDialogOnSave.value = false
                            },
                        ) {
                            Text(
                                stringResource(R.string.cancel),
                                fontFamily = Railway
                            )
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

/**
 *  fetchShoppingList function is executed asynchronously to retrieve the user's shopping list from the Firestore
 *  database and updates a local SnapshotStateList object representing the shopping list. The function first retrieves
 *  a reference to the user's default shopping list document in Firestore using the user ID then it attaches a snapshot
 *  listener to this document to listen for any changes made to the shopping list.
 *
 * @param userId The current user ID.
 * @param shoppingList The shopping list as a SnapshotStateList of strings.
 * @param onListChanged The callback function to be called when the shopping list changes.
 *
 * @return Unit
 */
suspend fun fetchShoppingList(
    userId: String,
    shoppingList: SnapshotStateList<String>,
    onListChanged: () -> Unit,
) = withContext(Dispatchers.IO) {
    val db = FirebaseFirestore.getInstance()
    val listRef = db.collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("defaultShoppingList")

    listRef.addSnapshotListener { snapshot, error ->
        if (error != null) {
            Log.w("fetchShoppingList", "Listen failed.", error)
            return@addSnapshotListener
        }

        if (snapshot != null && snapshot.exists()) {
            val list = snapshot.get("items") as? List<String>
            if (list != null) {
                shoppingList.clear()
                shoppingList.addAll(list)
                onListChanged()
            }
        } else {
            Log.d("fetchShoppingList", "Current data: null")
        }
    }
}

/**
 * saveShoppingList function saves a shopping list for a specific user by updating the "items" field in the document of the
 * user's default shopping list in Firestore. It takes in the user ID and a SnapshotStateList of strings representing
 * the shopping list.
 *
 * @param userId The current user ID.
 * @param shoppingList The shopping list as a SnapshotStateList of strings.
 */
fun saveShoppingList(userId: String, shoppingList: SnapshotStateList<String>) {

    val db = FirebaseFirestore.getInstance()

    //Path reference to a private collection storing a shopping list
    val listRef = db.collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("defaultShoppingList")

    listRef.update("items", shoppingList)
        .addOnSuccessListener {
            Log.d("saveShoppingList", "Shopping list updated successfully")
        }
        .addOnFailureListener { exception ->
            Log.w("saveShoppingList", "Error updating shopping list", exception)
        }
}

/**
 *  clearShoppingList function clears the shopping list of a user with the given userId. It first gets a reference to
 *  the user's shopping list in Firestore, then updates the "items" field to an empty list.
 *
 *  @param userId The current user ID.
 */
fun clearShoppingList(userId: String) {
    val db = FirebaseFirestore.getInstance()
    val listRef = db.collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("defaultShoppingList")

    listRef.update("items", emptyList<String>())
        .addOnSuccessListener {
            Log.d("clearShoppingList", "Shopping list cleared successfully")
        }
        .addOnFailureListener { exception ->
            Log.w("clearShoppingList", "Error clearing shopping list", exception)
        }
}