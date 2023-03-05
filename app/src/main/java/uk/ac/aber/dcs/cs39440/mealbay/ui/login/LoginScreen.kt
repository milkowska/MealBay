package uk.ac.aber.dcs.cs39440.mealbay.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.EmailInput
import uk.ac.aber.dcs.cs39440.mealbay.ui.components.PasswordInput

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
            UserForm(loading = false, isCreateAccount = false) { email, password ->
                Log.d("Form", "LoginScreen: $email $password")
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
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val modifier = Modifier
        .height(240.dp)
        .verticalScroll(rememberScrollState())

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
            passwordFocusRequest.requestFocus()
        })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim())
            }
        )
        SendButton(
            textId = if (isCreateAccount) "Create Account" else "Login",
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
        }
    }
}

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

