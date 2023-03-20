package uk.ac.aber.dcs.cs39440.mealbay.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore


class LoginScreenViewModel : ViewModel() {
    // val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    //internally
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading


    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess() // goes home
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthInvalidUserException -> {
                        // handle invalid user exception
                        onError("Invalid email address")
                        Log.d("FBA", "Invalid email address")
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        // handle invalid credentials exception
                        onError("Invalid password")
                        Log.d("FBA", "Invalid password")
                    }
                    is RuntimeExecutionException -> {
                        // handle runtime exception
                        if (exception.cause is FirebaseAuthInvalidUserException) {
                            onError("Invalid email address")
                        } else {
                            onError("Error signing in")
                        }
                    }
                    else -> {
                        // handle other exceptions
                        onError("Error signing in")
                    }
                }
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

    private fun createUser(displayName: String?,) {
        val userId = auth.currentUser?.uid
        val user = mutableMapOf<String, Any>()
        user["user_id"] = userId.toString()
        user["displayName"] = displayName.toString()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }
}