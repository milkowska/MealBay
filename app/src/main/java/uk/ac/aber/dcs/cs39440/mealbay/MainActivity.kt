package uk.ac.aber.dcs.cs39440.mealbay

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.Navigation
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.MealBayTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealBayTheme {

                /*      val db = FirebaseFirestore.getInstance()
                      val user: MutableMap<String, Any> = HashMap()
                      user["firstName"] = "Robert"
                      user["lastName"] = "Barski"

                          db.collection("users")
                              .add(user)
                              .addOnSuccessListener {
                                  Log.d("FB", "onCreate: ${it.id}")
                              }.addOnFailureListener {
                                  Log.d("FB", "onCreate: $it")
                              }*/
                MealBayApp()
            }
        }
    }
}

@Composable
fun MealBayApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Navigation()
    }
}

