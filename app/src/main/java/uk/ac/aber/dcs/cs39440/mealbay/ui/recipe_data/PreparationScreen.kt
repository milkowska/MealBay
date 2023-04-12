package uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data

import androidx.compose.material3.TextField
import androidx.compose.material3.AlertDialog
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.storage.*
import androidx.compose.material3.TextButton

import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PreparationScreen(
    navController: NavController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var preparationDetails by rememberSaveable { mutableStateOf("") }
    var preparationList by rememberSaveable { mutableStateOf(emptyList<String>()) }
    val openDialogOnSave = remember { mutableStateOf(false) }

    fun updateFirstElement(element: String) {
        if (preparationList.isEmpty()) {
            preparationList = listOf(element)
        } else {
            preparationList = preparationList.toMutableList().apply { this[0] = element }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.preparation),
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.add_preparation),
            fontSize = 20.sp
        )
        Divider(
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        TextField(
            value = preparationDetails,
            onValueChange = { preparationDetails = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(300.dp),
            label = { Text(stringResource(id = R.string.enter_details)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

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
                enabled = preparationDetails.isNotEmpty(), // button is enabled once the ingredient list is created and not empty.
                modifier = Modifier
                    .width(220.dp)
                    .height(50.dp),
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
        if (openDialogOnSave.value) {

            AlertDialog(
                onDismissRequest = {
                    openDialogOnSave.value = false
                },
                title = {
                    Text(
                        text = stringResource(R.string.are_you_done),
                        fontFamily = Railway
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.warning_four),
                        fontFamily = Railway,
                        fontSize = 15.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialogOnSave.value = false
                            updateFirstElement(preparationDetails)
                            dataViewModel.saveStringList(preparationList, NEW_RECIPE_PREPARATION)
                            navController.navigate(route = Screen.Category.route)
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
    }
}
