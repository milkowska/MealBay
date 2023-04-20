package uk.ac.aber.dcs.cs39440.mealbay.ui.collection

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Divider
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs39440.mealbay.storage.COLLECTION_ID
import uk.ac.aber.dcs.cs39440.mealbay.storage.COLLECTION_NAME
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.maxCharsLengthForCollection
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.minCharsLength
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

/**
 * This composable function is a top-level entry point for the Collection Screen feature that displays available collection
 * or empty screen if there are none.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 */
@Composable
fun CollectionScreenTopLevel(
    navController: NavHostController,
) {
    CollectionScreen(navController)
}

/**
 * This screen is used to create or view existing private to the user collections. The user can press
 * on any to see the contents. These are fetched from firebase.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to retrieve user ID, and check if the collection is empty or not.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectionScreen(
    navController: NavHostController,
    // modifier: Modifier,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    // Getting user ID saved by view model
    val userId = dataViewModel.getString(CURRENT_USER_ID)

    // Observing if the user's collection is empty
    val isUserCollectionEmpty by dataViewModel.isUserCollectionEmpty.observeAsState(initial = true)

    // Set up a modal bottom sheet state for adding a new collection
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            dataViewModel.checkUserCollectionEmpty(userId)
        }
    }

    // Handle dismissing when the sheet is visible
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheet() },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        scrimColor = MaterialTheme.colorScheme.surfaceTint,
    ) {
        TopLevelScaffold(
            navController = navController,
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Display the user's collections if they are not empty
                    if (!isUserCollectionEmpty) {
                        if (userId != null) {
                            DisplayCollections(
                                userId = userId,
                                onDeleteClick = { collectionId ->
                                    deleteCollection(userId, collectionId)
                                },
                                onCollectionClick = { collectionId ->
                                    navController.navigate(Screen.ColDisplay.route)
                                },
                                dataViewModel = dataViewModel,
                            )
                        }
                    } else {
                        //If private collections are empty then display an empty screen
                        EmptyCollectionScreen()
                    }

                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            coroutineScope.launch {
                                if (sheetState.isVisible) sheetState.hide()
                                else sheetState.show()
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.create_collection),
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}

/**
 * This composable function represents an empty screen UI if the collections are not created by the user.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmptyCollectionScreen(
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    val coroutineScope = rememberCoroutineScope()

    // Handle dismissing when the sheet is visible
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheet() },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        scrimColor = MaterialTheme.colorScheme.surfaceTint,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 19.dp)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.recipebook),
                contentDescription = stringResource(id = R.string.no_data_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            )

            Text(
                text = stringResource(R.string.no_collections),
                fontSize = 21.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.click_to_create),
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * This composable function represents a Modal Bottom Sheet content where the user can add the collection name.
 *
 * @param dataViewModel The DataViewModel used to retrieve the user ID.
 */
@Composable
fun BottomSheet(dataViewModel: DataViewModel = hiltViewModel()) {

    val context = LocalContext.current
    var collectionName by rememberSaveable { mutableStateOf("") }
    var isErrorInTextField by remember {
        mutableStateOf(false)
    }

    // Getting the current user ID
    val userID = dataViewModel.getString(CURRENT_USER_ID)

    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.create_collection),
            fontSize = 21.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = collectionName,
            label = {
                Text(text = stringResource(R.string.name_this_collection))
            },
            onValueChange = {
                if (it.length <= maxCharsLengthForCollection) {
                    collectionName = it
                    isErrorInTextField = collectionName.isEmpty()
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFF9C4234),
                textColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.width(360.dp),
            singleLine = true,
            isError = isErrorInTextField,
            trailingIcon = {
                Text(
                    text = "${maxCharsLengthForCollection - collectionName.length}",
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
        )

        Spacer(modifier = Modifier.height(100.dp))

        FilledTonalButton(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            enabled = collectionName.isNotEmpty(),
            onClick = {
                if (collectionName.trim().length < minCharsLength) {
                    Toast.makeText(context, "The collection name is too short.", Toast.LENGTH_LONG)
                        .show()
                    isErrorInTextField = true
                } else {
                    if (userID != null) {
                        savePrivateCollection(userID, collectionName)
                    }
                    // Clearing collection name after saving it to database
                    collectionName = ""
                }
            }, modifier = Modifier
                .width(220.dp)
                .height(50.dp)
        ) {
            Text(
                stringResource(R.string.save),
                fontFamily = Railway,
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}

/**
 *  This function saves a new private collection to Firebase Firestore.
 *
 *  @param userId The current user ID.
 *  @param name The collection name to be saved.
 */
fun savePrivateCollection(userId: String, name: String) {
    // Get an instance of the Firestore database.
    val db = FirebaseFirestore.getInstance()
    // Create a HashMap with the collection name.
    val collectionName = hashMapOf("name" to name)

    // Add the new collection to the Firestore database where path is users->userId->collections->collectionName.
    db.collection("users")
        .document(userId)
        .collection("collections")
        .add(collectionName)
        .addOnSuccessListener { documentReference ->
            Log.d(
                "savePrivateCollection",
                "DocumentSnapshot added with ID: ${documentReference.id}"
            )
        }
        .addOnFailureListener { e ->
            Log.w("savePrivateCollection", "Error adding a document.", e)
        }
}

/**
 *  This function deletes a private collection given the current user id and a collection ID.
 *
 *  @param userId The current user ID.
 *  @param collectionId The collection ID to be deleted.
 */
fun deleteCollection(userId: String, collectionId: String) {
    val db = FirebaseFirestore.getInstance()

    // Deletes a collection in the database
    db.collection("users")
        .document(userId)
        .collection("collections")
        .document(collectionId)
        .delete()
        .addOnSuccessListener {
            Log.d("deleteCollection", "DocumentSnapshot successfully deleted!")
        }
        .addOnFailureListener { e ->
            Log.w("deleteCollection", "Error deleting document", e)
        }
}

/**
 * This function is displaying the available collections if they exists, the size and name of them. New collection can
 * still be created here.
 *
 * @param userId The current user ID.
 * @param onDeleteClick A function that is called when the user clicks the delete button for a collection and passes the ID of the collection to be deleted as a parameter.
 * @param onCollectionClick A function that is called when the user clicks on a collection. Passes the ID of the collection to be opened as a parameter.
 * @param dataViewModel The DataViewModel instance used to save collection information.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DisplayCollections(
    userId: String,
    onDeleteClick: (String) -> Unit,
    onCollectionClick: (String) -> Unit,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    val collections = remember { mutableStateOf(listOf<Pair<DocumentSnapshot, Int>>()) }
    val isLoading = remember { mutableStateOf(true) }
    val openAlertDialog = remember { mutableStateOf(false) }
    val selectedCollectionId = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            it != ModalBottomSheetValue.HalfExpanded
        })

    // Fetching collections from firebase first
    LaunchedEffect(userId) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("collections")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("DisplayCollections", "Error fetching collections", error)
                } else {
                    value?.let { querySnapshot ->
                        coroutineScope.launch {
                            val fetchedCollections =
                                querySnapshot.documents.map { documentSnapshot ->
                                    val collectionSize =
                                        getCollectionSize(userId, documentSnapshot.id)
                                    documentSnapshot to collectionSize
                                }
                            collections.value = fetchedCollections
                            isLoading.value = false
                        }
                    }
                }
            }
    }

    // Adding a Circular Progress Indicator if the data is still loading/fetching
    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF9C4234))
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(collections.value) { (documentSnapshot, collectionSize) ->

                    val collectionName = documentSnapshot.getString("name") ?: "Unnamed"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                onCollectionClick(documentSnapshot.id)
                                dataViewModel.saveString(collectionName, COLLECTION_NAME)
                                dataViewModel.saveString(documentSnapshot.id, COLLECTION_ID)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text(text = collectionName, fontSize = 19.sp)
                        }

                        IconButton(onClick = {
                            openAlertDialog.value = true
                            selectedCollectionId.value = documentSnapshot.id
                        }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Delete Collection",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Divider()
                }
            }

            FloatingActionButton(
                backgroundColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    coroutineScope.launch {
                        if (sheetState.isVisible) sheetState.hide()
                        else sheetState.show()
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.create_collection),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (openAlertDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        openAlertDialog.value = false
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.are_you_sure),
                            fontFamily = Railway,
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.pressing_confirm),
                            fontFamily = Railway,
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            onDeleteClick(selectedCollectionId.value)
                            openAlertDialog.value = false
                        }) {
                            Text(
                                text = stringResource(id = R.string.confirm),
                                fontFamily = Railway,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openAlertDialog.value = false }) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                fontFamily = Railway,
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * A getCollectionSize suspending function fetches the size of a collection given the collection ID and user ID.
 *
 * @param userId the ID of the user whose collection we are fetching.
 * @param collectionId the ID of the collection whose size we are fetching.
 * @return the size of the collection if it exists, otherwise 0.
 */
suspend fun getCollectionSize(userId: String, collectionId: String): Int {
    val firestore = Firebase.firestore
    val userCollectionsRef = firestore
        .collection("users")
        .document(userId)
        .collection("collections")

    return try {
        val snapshot = userCollectionsRef
            .document(collectionId)
            .collection("recipes")
            .get()
            .await()

        snapshot.size()
    } catch (e: Exception) {
        Log.e("GET_COLLECTION_SIZE", "Error fetching collection size", e)
        0
    }
}
