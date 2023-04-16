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
import androidx.compose.material3.AlertDialog
import uk.ac.aber.dcs.cs39440.mealbay.storage.NEW_RECIPE_PHOTO
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.minCharsLength


/**
 * This composable function is displaying the screen where the user can interact and add ingredients of the custom recipe
 * he is currently creating. The list of ingredients is refreshed and shown on the screen while the user is adding them. The
 * user can come back to the previous screen if an arrow icon is pressed on the top app bar and can go to further screen when
 * a save button is clicked and then confirmed about the changes.
 *
 * @param navController The navigation controller used for navigating between screens in the app.
 * @param dataViewModel The DataViewModel used to save the ingredients data.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IngredientsScreen(
    navController: NavController,
    dataViewModel: DataViewModel = hiltViewModel()
) {

    // Handling a sheet state for Bottom Sheet, as either hidden or half-expanded
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()
    val ingredientsList = remember { mutableStateListOf<String>() }
    val openAlertDialog = remember { mutableStateOf(false) }
    val openAlertDialogOnSave = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.ingredients),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Displaying an alert dialog to prevent the user to leave without saving the data
                        openAlertDialog.value = true
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

        // setting a back button handler to dismiss the bottom sheet when it is visible
        BackHandler(sheetState.isVisible) {
            coroutineScope.launch { sheetState.hide() }
        }

        // Show a modal bottom sheet when the FAB is clicked to collect an ingredient data
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = { ModalBottomSheet(ingredientsList) },
            modifier = Modifier.fillMaxSize(),
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                //Displaying the ingredient list content as a scrollable column which is updated on each ingredient creation
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

                // This alert dialog is displayed when the user clicks an arrow icon on the top bar to warn him the data will be cleared if he proceeds
                if (openAlertDialog.value) {

                    AlertDialog(
                        onDismissRequest = {
                            openAlertDialog.value = false
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
                                    openAlertDialog.value = false
                                    //Navigating to a previous screen
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
                                    openAlertDialog.value = false
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

                // This alert dialog is displayed when the user clicks a save button to warn the data can not be modified later if he proceeds
                if (openAlertDialogOnSave.value) {

                    AlertDialog(
                        onDismissRequest = {
                            openAlertDialog.value = false
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
                                    openAlertDialog.value = false
                                    //Saving ingredients using dataViewModel
                                    dataViewModel.saveStringList(
                                        ingredientsList,
                                        NEW_RECIPE_INGREDIENTS
                                    )
                                    dataViewModel.saveString(
                                        "https://cdn.pixabay.com/photo/2020/09/02/08/19/dinner-5537679_960_720.png",
                                        NEW_RECIPE_PHOTO
                                    )
                                    //Navigating to the preparation screen
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
                                    openAlertDialog.value = false
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
                            openAlertDialogOnSave.value = true
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

/**
 * This composable function contains the content of the modal bottom sheet. It is called when the Floating Action
 * Button is clicked and the data is collected if the user enters it into a text field and saves it.
 */
@Composable
fun ModalBottomSheet(ingredientsList: SnapshotStateList<String>) {
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
                if (ingredient.trim() == "" || ingredient.trim().length < minCharsLength) {
                    Toast.makeText(context, "The ingredient is too short.", Toast.LENGTH_LONG)
                        .show()
                    isErrorInTextField = true
                } else {
                    ingredientsList.add(ingredient)
                    ingredient = "" // clearing an ingredient value once added to a list
                }
            },
            modifier = Modifier
                .width(220.dp)
                .height(50.dp),

            ) {
            Text(stringResource(R.string.save))
        }
    }
}