package uk.ac.aber.dcs.cs39440.mealbay.ui.login

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.storage.CURRENT_USER_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.EmailInput
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.PasswordInput
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = viewModel(),
    dataViewModel: DataViewModel = hiltViewModel()
) {
    val showLoginForm = rememberSaveable { mutableStateOf(true) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(scaffoldState = scaffoldState) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Image(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(25.dp)),
                    painter = painterResource(id = R.drawable.logorem),
                    contentDescription = stringResource(id = R.string.logo),
                    contentScale = ContentScale.Crop
                )

                // displays either the login form or the create account form based on the value of the showLoginForm variable
                if (showLoginForm.value) UserForm(
                    loading = false,
                    isCreateAccount = false
                ) { email, password ->

                    viewModel.signInWithEmailAndPassword(
                        email = email,
                        password = password,
                        onSuccess = { user ->
                            dataViewModel.saveString(
                                user.uid,
                                CURRENT_USER_ID
                            )
                            Log.d("userUID", " the user uid is ${user.uid}")
                            navController.navigate(Screen.Home.route)
                        },
                        onError = { error ->
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(error)
                            }
                        }
                    )
                }
                else {
                    UserForm(
                        loading = false,
                        isCreateAccount = true
                    ) { email, password ->
                        viewModel.createUserWithEmailAndPassword(
                            email, password,
                            onSuccess = { user ->
                                Log.d("UserForm", "User creation succeeded")
                                dataViewModel.saveString(user.uid, CURRENT_USER_ID)
                                navController.navigate(Screen.Home.route)
                            },
                            onError = { errorMessage ->
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    modifier = Modifier.padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val loginText =
                        if (showLoginForm.value) stringResource(id = R.string.new_user)
                        else stringResource(id = R.string.has_an_account)

                    val loginOrSignUp =
                        if (showLoginForm.value) stringResource(id = R.string.signup)
                        else stringResource(id = R.string.login)

                    Text(
                        loginText,
                        fontSize = 16.sp
                    )

                    Text(
                        loginOrSignUp,
                        modifier = Modifier
                            .clickable {
                                showLoginForm.value = !showLoginForm.value
                            }
                            .padding(start = 5.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, password -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim()
            .isNotEmpty() && (password.value.trim().length >= 6)
    }

    val modifier = Modifier
        .height(340.dp)
        .verticalScroll(rememberScrollState())

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isCreateAccount) Text(
            text = stringResource(id = R.string.create_account_description),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(25.dp)
        )

        EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
            passwordFocusRequest.requestFocus()
        })

        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            imeAction = ImeAction.Done,
            onDoneAction = {
                if (!valid) return@PasswordInput
                onDone(email.value.trim(), password.value.trim())
            }
        )

        SendButton(
            textId = if (isCreateAccount) stringResource(id = R.string.create) else stringResource(
                id = R.string.login
            ),
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
            // to make the keyboard go away when the button is clicked
            keyboardController?.hide()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SendButton(
    textId: String,
    loading: Boolean,
    validInputs: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
    ) {
        if (loading) CircularProgressIndicator(modifier = Modifier.size(30.dp))
        else Text(text = textId, modifier = Modifier.padding(5.dp))
    }
}

