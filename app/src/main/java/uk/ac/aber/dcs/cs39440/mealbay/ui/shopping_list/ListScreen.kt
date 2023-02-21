package uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier,
        shoppingList = shoppingList,
        doDelete = { shoppingListItem ->
            shoppingListViewModel.deleteShoppingListItem(
                shoppingListItem
            )
        },
        shoppingListViewModel
    )
}

@Composable
fun ListScreen(
    navController: NavHostController,
    modifier: Modifier,
    shoppingList: List<ShoppingListItem> = listOf(),
    doDelete: (ShoppingListItem) -> Unit = {},
    shoppingListViewModel: ShoppingListViewModel
) {
    TopLevelScaffold(
        navController = navController,
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            val openAlertDialog = remember { mutableStateOf(false) }
            val context = LocalContext.current

            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
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
                                            .padding(start = 8.dp, top = 10.dp),
                                        fontSize = 18.sp,
                                    )
                                    //TODO add  delete icon in the list element
                                }

                                Divider(startIndent = 0.dp, thickness = 1.dp)
                            }
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
                            onClick = {
                                openAlertDialog.value = true
                            }
                        )
                        {
                            //TODO disable
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
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            openAlertDialog.value = false

                                            shoppingListViewModel.clearShoppingList()

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
                                },

                                dismissButton = {
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
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyScreenContent(

    shoppingList: List<ShoppingListItem> = listOf(),
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        val openAlertDialog = remember { mutableStateOf(false) }
        val context = LocalContext.current

        Text(
            text = "Shopping list",
            fontSize = 27.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            modifier = Modifier
                .size(230.dp),
            painter = painterResource(id = R.drawable.emptycart),
            contentDescription = stringResource(id = R.string.shopping_list_is_empty),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Your shopping list is currently empty. Click on the add button to add an ingredient.",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = 35.dp)
        ) {
            ElevatedButton(modifier = Modifier
                .height(60.dp)
                .width(200.dp)
                .weight(0.5f)
                .padding(end = 16.dp),
                enabled = shoppingList.isNotEmpty(),
                onClick = {
                    openAlertDialog.value = true
                }
            )
            {
                //TODO disable
                Text(
                    text = stringResource(id = R.string.clear_all),
                    fontSize = 16.sp,
                )
            }

            FloatingActionButton(
                onClick = { /* TODO */ },
                modifier = Modifier.padding(26.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
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
                    confirmButton = {
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
                    },

                    dismissButton = {
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
                )
            }
        }
    }
}
