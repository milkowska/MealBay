package uk.ac.aber.dcs.cs39440.mealbay.ui.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(navController: NavHostController) {
    var recipe_name  by rememberSaveable { mutableStateOf("") }
    var total_time  by rememberSaveable { mutableStateOf("") }

    var isErrorInTextField by remember {
        mutableStateOf(false)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create new recipe",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Explore.route) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color(0xFFFFDAD4)
            )
        }) {

        Image(
            painter = painterResource(id = R.drawable.peoplecooking),
            contentDescription = "People cooking image",
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .padding(25.dp),
            //contentScale = ContentScale.Crop
        )

        TextField(
            value = recipe_name,
            label = {
                Text(text = stringResource(R.string.name_of_new_recipe))
            },
            onValueChange = {
                recipe_name = it
                isErrorInTextField = recipe_name.isEmpty()
            },
            singleLine = true,
            isError = isErrorInTextField,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.name_of_new_recipe)
                )
            }
        )
        TextField(
            value = total_time,
            label = {
                Text(text = stringResource(R.string.total_time_of_new_recipe))
            },
            onValueChange = {
                total_time = it
                isErrorInTextField = total_time.isEmpty()
            },
            singleLine = true,
            isError = isErrorInTextField,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.total_time_of_new_recipe)
                )
            }
        )


        Text(
            text = stringResource(R.string.difficulty_of_new_recipe),
            fontSize = 18.sp
        )

        Text(
            text = stringResource(R.string.rating_of_new_recipe),
            fontSize = 18.sp
        )





        ElevatedButton(onClick = {

        }) {
            Text(stringResource(R.string.next))
        }
    }


}

@Composable
private fun AddIngredientsScreen() {
    TODO("Not yet implemented")
}
