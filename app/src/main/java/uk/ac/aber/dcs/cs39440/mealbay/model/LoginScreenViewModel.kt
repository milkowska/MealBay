package uk.ac.aber.dcs.cs39440.mealbay.model

import android.util.Log
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

/**
 * This is the ViewModel class for the LoginScreen.
 */
class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    // saving internally
    private val _loading = MutableLiveData(false)


    /**
     * This is a function that handles signing in with email and password using Firebase Authentication.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     * @param onSuccess The callback function to be called if sign-in is successful.
     * @param onError The callback function to be called if an error occurs during sign-in.
     */
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
                        // handling invalid user exception
                        onError("Invalid email address")
                        Log.d("FBA", "Invalid email address!!")
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        // handling invalid credentials exception
                        onError("Invalid credentials!")
                        Log.d("FBA", "Invalid password")
                    }
                    is RuntimeExecutionException -> {
                        // handling runtime exception
                        if (exception.cause is FirebaseAuthInvalidUserException) {
                            onError("Invalid email address!")
                        } else {
                            onError("Error signing in")
                        }
                    }
                    else -> {
                        // handling other exceptions
                        onError("Error signing in")
                    }

                }
            }
    }

    /**
     * a createUserWithEmailAndPassword function creates a new user account using an email address and password, and
     * calls a callback function depending on whether the operation was successful or not.It takes in the email and
     * password strings as well as two callback functions.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     * @param onSuccess The callback function to be called if the user account is created successfully, passing in a FirebaseUser object representing the newly created user.
     * @param onError The callback function to be called if the operation fails, passing in a String error message.
     *
     */
    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        if (_loading.value == false) {

            if (password.length < 6) {
                onError("Password is too short!")
                return
            }
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
                                "Invalid credentials!"
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

    /**
     * a private createUser function creates a user document in the Firestore database with the authenticated user's
     * ID and display name. If the user ID is not null, a mutable map is created with the user ID and display name,
     * and then added to the "users" collection in Firestore with the user ID as the document ID.
     *
     * @param displayName A name to display
     */
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