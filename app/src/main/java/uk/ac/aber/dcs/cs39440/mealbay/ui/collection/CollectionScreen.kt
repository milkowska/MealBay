package uk.ac.aber.dcs.cs39440.mealbay.ui.collection

import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import uk.ac.aber.dcs.cs39440.mealbay.storage.COLLECTION_ID
import uk.ac.aber.dcs.cs39440.mealbay.storage.COLLECTION_NAME
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

@Composable
fun CollectionScreenTopLevel(
    navController: NavHostController,
) {
    CollectionScreen(navController, modifier = Modifier)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectionScreen(
    navController: NavHostController,
    modifier: Modifier,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    val isUserCollectionEmpty by dataViewModel.isUserCollectionEmpty.observeAsState(initial = true)
    val userId = dataViewModel.getString(CURRENT_USER_ID)

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

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheet() },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
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
                                //  coroutineScope = coroutineScope,
                                // sheetState = sheetState
                            )
                        }
                    } else {
                        EmptyCollectionScreen(coroutineScope, sheetState)
                    }

                    FloatingActionButton(
                        backgroundColor = (Color(0xFFFFDAD4)),
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
                            contentDescription = stringResource(id = R.string.create_collection)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmptyCollectionScreen(
    //dataViewModel: DataViewModel = hiltViewModel(),
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheet() },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.nodata),
                contentDescription = stringResource(id = R.string.no_data_image),
                modifier = Modifier
                    .width(320.dp)
                    .height(320.dp)
                    .padding(25.dp),
            )

            Text(
                text = stringResource(R.string.no_collections),
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.click_to_create),
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(80.dp))

            FloatingActionButton(
                backgroundColor = (Color(0xFFFFDAD4)),
                onClick = {
                    coroutineScope.launch {
                        if (sheetState.isVisible) sheetState.hide()
                        else sheetState.show()
                    }
                },
                modifier = Modifier
                    .padding(10.dp)
                    .width(200.dp)
            ) {
                Text(text = stringResource(id = R.string.create_collection))
            }
        }
    }
}

@Composable
fun BottomSheet(dataViewModel: DataViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var collectionName by rememberSaveable { mutableStateOf("") }
    var isErrorInTextField by remember {
        mutableStateOf(false)
    }
    val userID = dataViewModel.getString(CURRENT_USER_ID)
    val maxCharsLonger = 34
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = stringResource(id = R.string.create_collection),
            fontSize = 23.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = collectionName,
            label = {
                Text(text = stringResource(R.string.name_this_collection))
            },
            onValueChange = {
                if (it.length <= maxCharsLonger) {
                    collectionName = it
                    isErrorInTextField = collectionName.isEmpty()
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFF9C4234)),
            modifier = Modifier.width(360.dp),
            singleLine = true,
            isError = isErrorInTextField,
            trailingIcon = {
                Text(
                    text = "${maxCharsLonger - collectionName.length}",
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(100.dp))

        FilledTonalButton(
            enabled = collectionName.isNotEmpty(),
            onClick = {
                if (collectionName.trim() == "") {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_LONG).show()
                    isErrorInTextField = true
                } else {
                    if (userID != null) {
                        savePrivateCollection(userID, collectionName)
                    }
                    collectionName = ""
                }
            }, modifier = Modifier
                .width(220.dp)
                .height(50.dp)
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

fun savePrivateCollection(userId: String, name: String) {
    val db = FirebaseFirestore.getInstance()
    val collectionData = hashMapOf("name" to name)

    db.collection("users")
        .document(userId)
        .collection("collections")
        .add(collectionData)
        .addOnSuccessListener { documentReference ->
            Log.d(
                "savePrivateCollection",
                "DocumentSnapshot added with ID: ${documentReference.id}"
            )
        }
        .addOnFailureListener { e ->
            Log.w("savePrivateCollection", "Error adding document", e)
        }
}

fun deleteCollection(userId: String, collectionId: String) {
    val db = FirebaseFirestore.getInstance()
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
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded })

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
    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
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
                            Text(
                                text = if (collectionSize == 1) "$collectionSize recipe" else "$collectionSize recipes",
                                fontSize = 14.sp
                            )
                        }

                        IconButton(onClick = {
                            openAlertDialog.value = true
                            selectedCollectionId.value = documentSnapshot.id
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete Collection")
                        }


                    }
                    Divider()
                }
            }
            FloatingActionButton(
                backgroundColor = (Color(0xFFFFDAD4)),
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
                    contentDescription = stringResource(id = R.string.create_collection)
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
                            fontSize = 22.sp
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.pressing_confirm),
                            fontFamily = Railway,
                            fontSize = 16.sp
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
                                fontSize = 16.sp
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openAlertDialog.value = false }) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                fontFamily = Railway,
                                fontSize = 16.sp
                            )
                        }
                    }
                )
            }
        }
    }
}

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
