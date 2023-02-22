package uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.ShoppingListItem
import uk.ac.aber.dcs.cs39440.mealbay.model.ShoppingListViewModel
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold


@Composable
fun ListScreenTopLevel(
    navController: NavHostController,
    shoppingListViewModel: ShoppingListViewModel = viewModel(),
) {
    val shoppingList by shoppingListViewModel.shoppingList.observeAsState(listOf())

    ListScreen(
        navController,
        shoppingList = shoppingList,
        doDelete = { shoppingListItem ->
            shoppingListViewModel.deleteShoppingListItem(
                shoppingListItem
            )
        },
        doInsert = { shoppingListItem ->
            shoppingListViewModel.insertShoppingListItem(
                shoppingListItem
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavHostController,
    shoppingList: List<ShoppingListItem> = listOf(),
    doDelete: (ShoppingListItem) -> Unit = {},
    doInsert: (ShoppingListItem) -> Unit = {},
) {
    var openDialog = remember { mutableStateOf(false) }
    var item by rememberSaveable { mutableStateOf("") }
    var context = LocalContext.current
    TopLevelScaffold(
        navController = navController,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openDialog.value = true
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
            val openAlertDialog = remember { mutableStateOf(false) }



            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                if (openDialog.value) {
                    Dialog(
                        onDismissRequest = {
                            openDialog.value = false
                        }
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(300.dp)
                                .height(250.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween

                            ) {
                                Text(
                                    text = stringResource(R.string.add_element),
                                    modifier = Modifier,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 23.sp,
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                AddElement(
                                    item = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp)
                                        .height(65.dp),
                                    update = {
                                        item = it
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                FilledTonalButton(modifier = Modifier
                                    .height(55.dp)
                                    .fillMaxWidth()
                                    .padding(start = 30.dp, end = 30.dp),
                                    enabled = item.isNotEmpty(),
                                    onClick = {
                                        if (item.trim() == "") {
                                            Toast.makeText(
                                                context,
                                                "Invalid input",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            doInsert(
                                                ShoppingListItem(
                                                    item = item.lowercase().trim()
                                                )
                                            )
                                            Toast.makeText(
                                                context,
                                                "New word pair has been added!",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            item = ""
                                        }
                                    }
                                )
                                {
                                    Text(
                                        text = stringResource(id = R.string.save),
                                        fontSize = 15.sp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                            }
                        }
                    }
                }
                if (shoppingList.isEmpty()) {
                    EmptyScreenContent(shoppingList)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Divider(
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Text(
                            text = stringResource(R.string.shopping_list),
                            modifier = Modifier,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 5.dp)
                        ) {

                            items(shoppingList) { item ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()

                                ) {
                                    Text(
                                        text = item.item,
                                        modifier = Modifier
                                            .padding(start = 8.dp, top = 10.dp, end = 10.dp)
                                            .weight(1f),
                                        fontSize = 18.sp,
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = stringResource(id = R.string.delete_icon),
                                        modifier = Modifier
                                            .clickable(
                                                onClick = {
                                                    doDelete(
                                                        ShoppingListItem(
                                                            item = item.item,
                                                            id = item.id
                                                        )
                                                    )
                                                }
                                            ),
                                        )
                                }
                                Divider(startIndent = 0.dp, thickness = 1.dp)
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(all = 35.dp)
                        ) {
                            ElevatedButton(modifier = Modifier
                                .height(60.dp)
                                .width(200.dp)
                                .weight(0.5f),
                                enabled = shoppingList.isNotEmpty(),
                                onClick = {
                                    openAlertDialog.value = true
                                }
                            )
                            {

                                Text(
                                    text = stringResource(id = R.string.clear_all),
                                    fontSize = 16.sp,
                                )
                            }

                            if (openAlertDialog.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        openAlertDialog.value = false
                                    },
                                    title = {
                                        Text(
                                            text = stringResource(R.string.clear_the_list),
                                        )
                                    },
                                    text = {
                                        Text(
                                            stringResource(R.string.confirm_clearing),
                                        )
                                    },
                                    confirmButton = { ConfirmButton(openAlertDialog, context) },
                                    dismissButton = { DismissButton(openAlertDialog) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyScreenContent(
    shoppingList: List<ShoppingListItem> = listOf()
) {

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = 35.dp)
        ) {
            ElevatedButton(modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(end = 80.dp),
                enabled = shoppingList.isNotEmpty(),
                onClick = { /* nothing happens because button is always disable for empty screen */ }
            )
            {
                Text(
                    text = stringResource(id = R.string.clear_all),
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun ConfirmButton(openAlertDialog: MutableState<Boolean>, context: Context) {
    TextButton(
        onClick = {
            openAlertDialog.value = false

            Toast.makeText(
                context,
                "The shopping list has been cleared.",
                Toast.LENGTH_SHORT
            ).show()
        },
    ) {
        Text(
            stringResource(R.string.delete),
        )
    }
}

@Composable
private fun DismissButton(openAlertDialog: MutableState<Boolean>) {
    TextButton(
        onClick = {
            openAlertDialog.value = false
        },
    ) {
        Text(
            stringResource(R.string.cancel),
        )
    }
}

@Composable
fun AddElement(
    item: String,
    modifier: Modifier,
    update: (String) -> Unit
) {
    OutlinedTextField(
        value = item,
        label = {
            Text(text = stringResource(R.string.add_element))
        },
        onValueChange = { update(it) },
        modifier = modifier
    )
}
