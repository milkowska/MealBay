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
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.Navigation
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.MealBayTheme

@AndroidEntryPoint
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealBayApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Navigation()
    }
}

