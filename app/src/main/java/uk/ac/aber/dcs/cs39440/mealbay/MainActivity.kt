package uk.ac.aber.dcs.cs39440.mealbay

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.Navigation
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.MealBayTheme

/*@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealBayTheme {
                MealBayApp()
            }
        }
    }
}*/

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var currentRoute: MutableState<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealBayTheme {
                currentRoute = remember { mutableStateOf("") }
                MealBayApp(currentRoute)
            }
        }
    }

    override fun onBackPressed() {
        if (currentRoute.value == Screen.Home.route) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealBayApp(currentRoute: MutableState<String>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Navigation(currentRoute)
    }
}

