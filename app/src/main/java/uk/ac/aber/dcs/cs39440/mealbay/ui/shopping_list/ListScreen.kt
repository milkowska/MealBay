package uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.mutableStateListOf

import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextField
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.ShoppingListViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.storage.NEW_RECIPE_INGREDIENTS
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data.BottomSheetHere
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway


@Composable
fun ListScreenTopLevel(
    navController: NavHostController,
    shoppingListViewModel: ShoppingListViewModel = viewModel(),
    dataViewModel: DataViewModel = hiltViewModel(),
) {
    val userId = dataViewModel.getString(CURRENT_USER_ID)
    if (userId != null) {
        ListScreen(userId = userId)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.my_shopping_list),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                backgroundColor = Color(0xFFFFFFFF)
            )
        }) {
        BackHandler(sheetState.isVisible) {
            coroutineScope.launch { sheetState.hide() }
        }

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = { BottomSheetHere(shoppingList, userId) },
            modifier = Modifier.fillMaxSize(),
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (shoppingList.isEmpty()) {

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
                    Spacer(modifier = Modifier.height(80.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(shoppingList) { ingredient ->
                            Text(
                                text = ingredient,
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
                        enabled = shoppingList.isNotEmpty(), // button is enabled once the ingredient list is created and not empty.
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

            }
        }

    }
}


@Composable
fun BottomSheetHere(
    shoppingList: SnapshotStateList<String>,
    userId: String
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
                Text(text = stringResource(R.string.add_ingredient))
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
                }
            }, modifier = Modifier
                .width(220.dp)
                .height(50.dp)
        ) {
            Text(stringResource(R.string.add))
        }
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
/*

fun refreshShoppingList(userId: String, shoppingList: MutableStateList<String>) {
    val db = FirebaseFirestore.getInstance()
    val shoppingListRef = db.collection("users")
        .document(userId)
        .collection("shoppingList")
        .document("default")
    shoppingListRef.get()
        .addOnSuccessListener { documentSnapshot ->
            val list = documentSnapshot.get("list") as List<String>?
            shoppingList.clear()
            if (list != null) {
                shoppingList.addAll(list)
            }
        }
        .addOnFailureListener { e ->
            Log.w("refreshShoppingList", "Error refreshing shopping list for user $userId", e)
        }
}
*/



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyScreenContent(
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Shopping list",
            fontSize = 27.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            modifier = Modifier
                .size(260.dp),
            painter = painterResource(id = R.drawable.emptycart),
            contentDescription = stringResource(id = R.string.shopping_list_is_empty),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Your shopping list is currently empty. Click on the add button to add an ingredient.",
            fontSize = 21.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

    }
}


