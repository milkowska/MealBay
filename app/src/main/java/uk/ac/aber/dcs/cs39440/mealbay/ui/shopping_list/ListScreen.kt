package uk.ac.aber.dcs.cs39440.mealbay.ui.shopping_list

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold


@Composable
fun ListScreenTopLevel(
    navController: NavHostController,
) {
    ListScreen(navController, modifier = Modifier)
}

@Composable
fun ListScreen(
    navController: NavHostController,
    modifier: Modifier,
) {
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
                EmptyScreenContent()
            }
        }
    }
}


@Composable
private fun EmptyScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        val openAlertDialog = remember { mutableStateOf(false) }
        val context = LocalContext.current

        Text(
            text = "Shopping list",
            fontSize = 27.sp,
            modifier = modifier,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            modifier = Modifier
                .size(230.dp),
            painter = painterResource(id = R.drawable.emptycart),
            contentDescription = stringResource(id = R.string.shopping_list_is_empty),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Your shopping list is currently empty. Click on the add button to add an ingredient.",
            fontSize = 20.sp,
            modifier = modifier
                .padding(20.dp),
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

