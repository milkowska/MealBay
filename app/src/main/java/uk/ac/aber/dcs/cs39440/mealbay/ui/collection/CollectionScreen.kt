package uk.ac.aber.dcs.cs39440.mealbay.ui.collection

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedButton
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
import uk.ac.aber.dcs.cs39440.mealbay.storage.COLLECTION_EMPTY
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold

import com.google.firebase.firestore.ListenerRegistration

@Composable
fun CollectionScreenTopLevel(
    navController: NavHostController,
) {
    CollectionScreen(navController, modifier = Modifier)
}

@Composable
fun CollectionScreen(
    navController: NavHostController,
    modifier: Modifier,
    dataViewModel: DataViewModel = hiltViewModel()
) {

    val isUserCollectionEmpty by dataViewModel.isUserCollectionEmpty.observeAsState(initial = true)
    var userId = dataViewModel.getString(CURRENT_USER_ID)

    LaunchedEffect(userId) {
        if (userId != null) {
            dataViewModel.checkUserCollectionEmpty(userId)
        }
    }

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
                if (!isUserCollectionEmpty) {
                    if (userId != null) {
                        DisplayCollections(
                            userId = userId,
                            onDeleteClick = { collectionId ->
                                deleteCollection(userId, collectionId)
                            }
                        )
                    }
                } else {
                    EmptyCollectionScreen(dataViewModel)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmptyCollectionScreen(dataViewModel: DataViewModel = hiltViewModel()) {
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
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = collectionName,
            label = {
                Text(text = stringResource(R.string.name_this_collection))
            },
            onValueChange = {
                collectionName = it
                isErrorInTextField = collectionName.isEmpty()
            },
            modifier = Modifier.width(360.dp),
            singleLine = true,
            isError = isErrorInTextField,
        )

        Spacer(modifier = Modifier.height(100.dp))

        ElevatedButton(
            enabled = collectionName.isNotEmpty(),
            onClick = {
                if (collectionName.trim() == "") {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_LONG).show()
                    isErrorInTextField = true
                } else {

                    if (userID != null) {
                        savePrivateCollection(userID, collectionName)
                       // dataViewModel.saveBoolean(COLLECTION_EMPTY, false)

                    }
                    collectionName = ""
                }
            }, modifier = Modifier
                .width(180.dp)
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

@Composable
fun DisplayCollections(
    userId: String,
    onDeleteClick: (String) -> Unit
) {
    val collections = remember { mutableStateOf(listOf<DocumentSnapshot>()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("collections")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("DisplayCollections", "Error fetching collections", error)
                } else {
                    value?.let {
                        collections.value = it.documents
                        isLoading.value = false
                    }
                }
            }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(collections.value) { documentSnapshot ->
                val collectionName = documentSnapshot.getString("name") ?: "Unnamed"
                val collectionSize = 0 // You need to fetch the collection size from Firestore

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = collectionName, fontSize = 18.sp)
                        Text(text = "$collectionSize recipes", fontSize = 14.sp)
                    }

                    IconButton(onClick = { onDeleteClick(documentSnapshot.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Collection")
                    }
                }
            }
        }
    }
}
