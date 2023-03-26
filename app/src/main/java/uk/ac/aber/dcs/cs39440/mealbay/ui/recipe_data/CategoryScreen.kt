package uk.ac.aber.dcs.cs39440.mealbay.ui.recipe_data

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.ui.graphics.Color
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.NEW_RECIPE_CATEGORY

@Composable
fun CategoryScreen(
    navController: NavController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = stringResource(id = R.string.category),
            fontSize = 22.sp
        )
        Text(
            text = stringResource(id = R.string.which_category),
            fontSize = 18.sp
        )

        Divider(
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        val categories = remember {
            listOf(
                "Breakfast",
                "Beverage",
                "Dessert",
                "Dinner",
                "Lunch",
                "Salad",
                "Soup",
                "Vegan",
                "Vegetarian"
            )
        }
        val selectedCategory = remember { mutableStateOf<String?>(null) }

        LazyColumn {
            items(categories) { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = category)
                    Checkbox(
                        checked = category == selectedCategory.value,
                        onCheckedChange = {
                            if (it) {
                                selectedCategory.value = category
                            } else {
                                selectedCategory.value = null
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF9C4234)
                        ))
                }
            }
        }

        ElevatedButton(
            onClick = {
                selectedCategory.value?.let {
                    dataViewModel.saveString(it, NEW_RECIPE_CATEGORY)
                }
            },
            enabled = selectedCategory.value != null,
            modifier = Modifier
                .width(180.dp)
                .height(50.dp),
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}