package uk.ac.aber.dcs.cs39440.mealbay.ui.collection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.TopLevelScaffold


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
                MainScreen()
            }
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
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

        sheetShape =  RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

          /*  Button(modifier = Modifier.
            padding(10.dp),
                onClick = {
                    coroutineScope.launch {
                        if (sheetState.isVisible) sheetState.hide()
                        else sheetState.show()
                    }
                }
            ) {
                Text(text = "Click to show bottom sheet")
            }
*/
            FloatingActionButton(
                onClick = { coroutineScope.launch {
                    if (sheetState.isVisible) sheetState.hide()
                    else sheetState.show()
                }  },
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
            }

        }
    }
}

@Composable
fun BottomSheet() {
    Column(
        modifier = Modifier.padding(32.dp)

    ) {
        Text(
            text = "Bottom sheet",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(200.dp))
        Text(
            text = "Click outside the bottom sheet to hide it",
            style = MaterialTheme.typography.body1
        )
    }
}