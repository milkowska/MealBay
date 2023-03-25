package uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data

import androidx.compose.material3.TextField


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.Divider
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.R


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IngredientsScreen(navController: NavController) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()
    val ingredientsList = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = { BottomSheetHere(ingredientsList) },
            modifier = Modifier.fillMaxSize(),
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(70.dp))
                Text(
                    text = stringResource(R.string.ingredients),
                    fontSize = 24.sp
                )
                Text(
                    text = stringResource(R.string.add_at_least_one),
                    fontSize = 20.sp
                )
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

                Row(
                    modifier = Modifier.padding(16.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    ElevatedButton(
                        onClick = {
                            //navigate to prep
                        },
                        enabled = ingredientsList.isNotEmpty(),
                        modifier = Modifier
                            .width(180.dp)
                            .height(50.dp),

                        ) {
                        Text(text = stringResource(id = R.string.next))
                    }

                    FloatingActionButton(
                        backgroundColor = (Color(0xFFFFDAD4)),
                        onClick = {
                            coroutineScope.launch {
                                if (sheetState.isVisible) sheetState.hide()
                                else sheetState.show()
                            }
                        },

                     /*   modifier = Modifier
                            .width(200.dp)
                            .height(50.dp),*/
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
                    //ingredient = ""

                    //TODO save
                }
            }, modifier = Modifier
                .width(180.dp)
        ) {
            Text(stringResource(R.string.save))
        }

    }
}