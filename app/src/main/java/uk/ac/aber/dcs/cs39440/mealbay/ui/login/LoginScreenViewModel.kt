package uk.ac.aber.dcs.cs39440.mealbay.ui.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore


class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    //internally
    private val _loading = MutableLiveData(false)

    fun setUserId(userId: String?) {
        _userId.value = userId
    }


    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    onSuccess(user) // goes home
                } else {
                    onError("Error signing in")
                }
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthInvalidUserException -> {
                        // handle invalid user exception
                        onError("Invalid email address")
                        Log.d("FBA", "Invalid email address!")
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        // handle invalid credentials exception
                        onError("Invalid credentials!")
                        Log.d("FBA", "Invalid password")
                    }
                    is RuntimeExecutionException -> {
                        // handle runtime exception
                        if (exception.cause is FirebaseAuthInvalidUserException) {
                            onError("Invalid email address!")
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

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        if (user != null) {
                            val displayName = user.email?.split('@')?.get(0)
                            createUser(displayName)
                            onSuccess(user)
                        }
                    } else {
                        val errorMessage = when (val exception = task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                "Invalid email address!"
                            }
                            else -> {
                                "Error creating user!"
                            }
                        }
                        onError(errorMessage)
                    }
                    _loading.value = false
                }
        } else {
            Log.d("createUserWith", "Function called while loading")
        }
    }

    private fun createUser(displayName: String?) {
        val authUserId = auth.currentUser?.uid
        if (authUserId != null) {
            val user = mutableMapOf<String, Any>()
            user["user_id"] = authUserId
            user["displayName"] = displayName.toString()

            FirebaseFirestore.getInstance().collection("users")
                .document(authUserId) // Set the user document ID as the Firebase Auth user ID
                .set(user)
                .addOnSuccessListener {
                    Log.d("createUser", "User document created with ID: $authUserId")
                }
                .addOnFailureListener { e ->
                    Log.w("createUser", "Error creating user document", e)
                }
        } else {
            Log.w("createUser", "Firebase Auth user ID is null")
        }
    }
}