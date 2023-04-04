package uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.ShoppingListViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway


@Composable
fun ListScreenTopLevel(
    navController: NavHostController,
    shoppingListViewModel: ShoppingListViewModel = viewModel(),
    dataViewModel: DataViewModel = hiltViewModel(),
) {
    val userId = dataViewModel.getString(CURRENT_USER_ID)
    if (userId != null) {
        ListScreen(navController, userId = userId)
    }
}

@Composable
fun BottomSheetHere(
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
        Spacer(modifier = Modifier.height(20.dp))

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
                } else if (item.trim().length < 3) {
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
            Text(stringResource(R.string.add))
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
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

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    LaunchedEffect(userId) {
        fetchShoppingList(userId, shoppingList) { emptyList = shoppingList.isEmpty() }
    }

    fun updateEmptyListState() {
        emptyList = shoppingList.isEmpty()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheetHere(shoppingList, userId, onListChanged = ::updateEmptyListState) },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        TopLevelScaffold(
            navController = navController,
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (emptyList) {
                    Text(
                        text = "The list is empty",
                    )
                } else {
                    LazyColumn {
                        items(shoppingList) { item ->
                            Text(text = item,)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    FloatingActionButton(
                        backgroundColor = (Color(0xFFFFDAD4)),
                        onClick = {
                            coroutineScope.launch {
                                if (sheetState.isVisible) sheetState.hide()
                                else sheetState.show()
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add an item")
                    }

                    ElevatedButton(
                        onClick = {
                            shoppingList.clear()
                            clearShoppingList(userId)
                            updateEmptyListState()
                            openDialogOnSave.value = false
                        },
                        enabled = shoppingList.isNotEmpty(),
                        modifier = Modifier
                            .width(220.dp)
                            .height(50.dp)
                            .align(Alignment.BottomStart),
                    ) {
                        Text(text = stringResource(id = R.string.clear_all))
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}


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

fun saveShoppingList(userId: String, shoppingList: SnapshotStateList<String>) {
    val db = FirebaseFirestore.getInstance()
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


/*
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
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

    LaunchedEffect(Unit) {
        checkDefaultShoppingListEmpty(
            userId = userId,
            shoppingList = shoppingList,
            onSuccess = { isEmpty ->
                emptyList = isEmpty
            },
            onFailure = { exception ->
                Log.e("ShoppingListScreen", "Error checking default shopping list", exception)
            }
        )
    }

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheetHere(shoppingList, userId,  onListChanged = { }) },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        TopLevelScaffold(
            navController = navController,
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (emptyList) {

                    Spacer(modifier = Modifier.height(60.dp))

                    Image(
                        modifier = Modifier
                            .size(260.dp),
                        painter = painterResource(id = R.drawable.emptycart),
                        contentDescription = stringResource(id = R.string.shopping_list_is_empty),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = stringResource(id = R.string.empty_shopping_list),
                        fontSize = 21.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                } else {

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(id = R.string.my_shopping_list),
                        fontSize = 21.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Divider(startIndent = 2.dp, thickness = 1.dp)
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(shoppingList) { item ->
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.Bottom
                ) {

                    ElevatedButton(
                        onClick = {
                            openDialogOnSave.value = true

                        },
                        enabled = shoppingList.isNotEmpty(),
                        modifier = Modifier
                            .width(220.dp)
                            .height(50.dp),
                    ) {
                        Text(text = stringResource(id = R.string.clear_all))
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
                                        openDialogOnSave.value = false
                                        emptyList = true
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
                    // The floating button is used to show the bottom sheet when clicked.
                    FloatingActionButton(
                        backgroundColor = (Color(0xFFFFDAD4)),
                        onClick = {
                            coroutineScope.launch {
                                if (sheetState.isVisible) sheetState.hide()
                                else sheetState.show()
                            }

                        },
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add an item")
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))


            }
        }

    }
}

@Composable
fun BottomSheetHere(
    shoppingList: SnapshotStateList<String>,
    userId: String,
    onListChanged: () -> Unit
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
        Spacer(modifier = Modifier.height(20.dp))

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
                } else if (item.trim().length < 3) {
                    Toast.makeText(context, "The item length is too short!", Toast.LENGTH_LONG)
                        .show()
                    isErrorInTextField = true
                } else {
                    shoppingList.add(item)
                    saveShoppingList(userId, shoppingList)
                    item = ""
                    onListChanged() // call the onListChanged callback to refresh the screen
                }
            }, modifier = Modifier
                .width(220.dp)
                .height(50.dp)
        ) {
            Text(stringResource(R.string.add))
        }
    }
}

private suspend fun checkDefaultShoppingListEmpty(
    userId: String,
    shoppingList: MutableList<String>,
    onSuccess: (Boolean) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val collectionRef = Firebase.firestore
        .collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("defaultShoppingList")
        .collection("list")
    try {
        val querySnapshot = collectionRef.get().await()
        if (querySnapshot.isEmpty) {
            onSuccess(true)
        } else {
            for (document in querySnapshot.documents) {
                val item = document.getString("name")
                if (item != null) {
                    shoppingList.add(item)
                }
            }
            onSuccess(false)
        }
    } catch (e: Exception) {
        onFailure(e)
    }
}


fun saveShoppingList(userId: String, shoppingList: SnapshotStateList<String>) {
    val db = FirebaseFirestore.getInstance()
    val list = shoppingList.toList()
    val shoppingListRef = db.collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("defaultShoppingList") // document ID for the shopping list
    shoppingListRef.set(mapOf("list" to list))
        .addOnSuccessListener { documentReference ->
            Log.d("saveShoppingList", "Shopping list saved for user $userId")
        }
        .addOnFailureListener { e ->
            Log.w("saveShoppingList", "Error saving shopping list for user $userId", e)
        }
}

fun clearShoppingList(userId: String) {
    val db = FirebaseFirestore.getInstance()
    val shoppingListRef = db.collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("defaultShoppingList")
    shoppingListRef.update("list", listOf<String>())
        .addOnSuccessListener {
            Log.d("clearShoppingList", "Shopping list cleared for user $userId")
        }
        .addOnFailureListener { e ->
            Log.w("clearShoppingList", "Error clearing shopping list for user $userId", e)
        }
}
fun getShoppingList(
    userId: String,
    shoppingList: MutableList<String>,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val shoppingListRef = db.collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("defaultShoppingList")

    shoppingListRef.get()
        .addOnSuccessListener { documentSnapshot ->
            val list = documentSnapshot.data?.get("list") as? List<String>
            if (list != null) {
                shoppingList.clear()
                shoppingList.addAll(list)
            }
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}


*/
