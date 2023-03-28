package uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data

import androidx.compose.material3.TextField
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway
import androidx.compose.material3.TextButton
import androidx.hilt.navigation.compose.hiltViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.NEW_RECIPE_INGREDIENTS
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IngredientsScreen(
    navController: NavController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()
    val ingredientsList = remember { mutableStateListOf<String>() }
    val openDialog = remember { mutableStateOf(false) }
    var openDialogOnSave = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ingredients",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        openDialog.value = true
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                backgroundColor = Color(0xFFFFFFFF)
            )
        }) {
        BackHandler(sheetState.isVisible) {
            coroutineScope.launch { sheetState.hide() }
        }

        // Show a modal bottom sheet when the FAB is clicked
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = { BottomSheetHere(ingredientsList) },
            modifier = Modifier.fillMaxSize(),
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = stringResource(R.string.add_at_least_one),
                    fontSize = 20.sp
                )

                Divider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                //Displaying the ingredient list content as a scrollable column.
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ingredientsList) { ingredient ->
                        Text(
                            text = ingredient,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (openDialog.value) {

                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.are_you_sure),
                                fontFamily = Railway
                            )
                        },
                        text = {
                            Text(
                                stringResource(R.string.warning),
                                fontFamily = Railway,
                                fontSize = 15.sp
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openDialog.value = false
                                    navController.popBackStack()
                                },
                            ) {
                                Text(
                                    stringResource(R.string.proceed),
                                    fontFamily = Railway
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openDialog.value = false
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
                if (openDialogOnSave.value) {

                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.are_you_done),
                                fontFamily = Railway
                            )
                        },
                        text = {
                            Text(
                                stringResource(R.string.warning_two),
                                fontFamily = Railway,
                                fontSize = 15.sp
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openDialog.value = false
                                    dataViewModel.saveStringList(ingredientsList, NEW_RECIPE_INGREDIENTS)
                                    navController.navigate(route = Screen.Preparation.route)
                                },
                            ) {
                                Text(
                                    stringResource(R.string.proceed),
                                    fontFamily = Railway
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openDialog.value = false
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
                        enabled = ingredientsList.isNotEmpty(), // button is enabled once the ingredient list is created and not empty.
                        modifier = Modifier
                            .width(220.dp)
                            .height(50.dp),
                    ) {
                        Text(text = stringResource(id = R.string.save))
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
                        Icon(Icons.Filled.Add, contentDescription = "Add Ingredient")
                    }
                }
            }
        }
    }
}


@Composable
fun BottomSheetHere(ingredientsList: SnapshotStateList<String>) {
    val context = LocalContext.current
    var ingredient by rememberSaveable { mutableStateOf("") }
    var isErrorInTextField by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = stringResource(id = R.string.add_ingredient),
            fontSize = 23.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = ingredient,
            label = {
                Text(text = stringResource(R.string.add_ingredient))
            },
            onValueChange = {
                ingredient = it
                isErrorInTextField = ingredient.isEmpty()
            },
            modifier = Modifier.width(360.dp),
            singleLine = true,
            isError = isErrorInTextField,
        )

        Spacer(modifier = Modifier.height(100.dp))

        ElevatedButton(
            enabled = ingredient.isNotEmpty(),
            onClick = {
                if (ingredient.trim() == "") {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_LONG).show()
                    isErrorInTextField = true
                } else {
                    ingredientsList.add(ingredient)
                    ingredient = ""

                }
            }, modifier = Modifier
                .width(220.dp)
                .height(50.dp)
        ) {
            Text(stringResource(R.string.save))
        }
    }
}