package uk.ac.aber.dcs.cs39440.mealbay.ui.collection

import android.annotation.SuppressLint
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.COLLECTION_NAME

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDisplayScreen(
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var collection = dataViewModel.getString(COLLECTION_NAME)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (collection != null) {
                        Text(
                            text = collection,
                            fontSize = 20.sp
                        )
                    }

                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color(0xFFFFDAD4)
            )
        }
    ) {

    }
}