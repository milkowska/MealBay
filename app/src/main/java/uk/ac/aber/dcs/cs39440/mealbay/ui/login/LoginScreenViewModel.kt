package uk.ac.aber.dcs.cs39440.mealbay.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginScreenViewModel : ViewModel() {
    // val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    //internally
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {

            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            Log.d(
                                "FB",
                                "signInWithEmailAndPassword: WORKS ${task.result.toString()}"
                            )

                            home()
                        } else {
                            Log.d(
                                "FB",
                                "signInWithEmailAndPassword: NOT SUCCESSFUL  ${task.result.toString()}"
                            )
                        }
                    }

            } catch (e: Exception) {
                Log.d("FB", "signInWithEmailAndPassword: ${e.message}")
            }

        }

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName =
                            task.result?.user?.email?.split('@')
                                ?.get(0) // displays an array eg. arr[0] = test arr[1] = @gmail.com
                        createUser(displayName)
                        home()
                    } else {
                        Log.d("FB", "createUserWithEmailAndPassword: ${task.result.toString()}")
                    }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = mutableMapOf<String, Any>()
        user["user_id"] = userId.toString()
        user["displayName"] = displayName.toString()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }
}