package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uk.ac.aber.dcs.cs39440.mealbay.R
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.data.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen
import uk.ac.aber.dcs.cs39440.mealbay.ui.theme.Railway
import androidx.compose.material3.MaterialTheme

// Maximum allowed characters for the collection name
const val maxCharsLengthForCollection = 34

// Minimum allowed characters for the collection name
const val minCharsLength = 3

// Maximum allowed characters for the total time
const val maxTotalTimeCharsLength = 26

// Maximum allowed characters for the recipe name
const val maxRecipeNameCharsLength = 52

/**
 * This composable function creates an email input field in a form.
 *
 * @param modifier The modifier to be applied to the email input field.
 * @param emailState The mutable state containing the value of the email input field.
 * @param labelId The string resource ID to be used as the label for the email input field.
 * @param enabled Whether the email input field is enabled and can be interacted with.
 * @param imeAction The IME action to be used for the email input field.
 * @param onAction The keyboard actions to be used for the email input field.
 */
@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    TextInputField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction, // To be performed when the user presses the action button on the email input field. This action will move the focus to the next input field in the form
        onAction = onAction,
    )
}

/**
 * This composable function creates a text field that is visible on a login page to handle the user's login data.
 *
 * @param modifier The modifier to be applied to the input field.
 * @param valueState A mutable state that holds the value of the input field.
 * @param labelId The string resource ID to be used as the label for the input field.
 * @param enabled A boolean value indicating whether the input field is enabled or not.
 * @param isSingleLine A boolean value indicating whether the input field should allow only one line of text.
 * @param keyboardType The type of keyboard to be used for the input field.
 * @param imeAction The IME action to be used for the input field. The default value is ImeAction.Next.
 * @param onAction The keyboard actions for the input field. The default value is KeyboardActions.Default.
 */
@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = {
            valueState.value = it
        },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        modifier = modifier
            .padding(all = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.primary,
            errorCursorColor = MaterialTheme.colorScheme.error,
            trailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction
    )
}

/**
 * This composable function creates a text field that is visible on a login page to handle the user's password data.
 * @param modifier Modifier for styling.
 * @param passwordState MutableState to store password input.
 * @param labelId Resource ID for the label of the input field.
 * @param enabled Boolean value to determine if the input field is enabled.
 * @param passwordVisibility MutableState to toggle password visibility.
 * @param imeAction ImeAction to set keyboard's action button. Default is Done.
 * @param onDoneAction Lambda to execute when done action is triggered on the keyboard.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onDoneAction: () -> Unit = {}
) {
    val visualTransformation = if (passwordVisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = {
            passwordState.value = it
        },
        label = { Text(text = labelId) },
        singleLine = true,
        modifier = modifier
            .padding(all = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),

        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.primary,
            errorCursorColor = MaterialTheme.colorScheme.error,
            trailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        visualTransformation = visualTransformation,
        trailingIcon = { PasswordVisibility(passwordVisibility = passwordVisibility) },
        keyboardActions = KeyboardActions(onDone = {
            if (passwordState.value.isNotEmpty()) {
                onDoneAction()
            }
            keyboardController?.hide()
        }),
    )
}

/**
 * This composable function creates an IconButton with an icon to toggle password visibility.
 *
 * @param passwordVisibility the state that tracks whether the password field is currently visible or not
 */
@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value = !visible }) {
        Icon(
            imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = if (visible) "Hide password" else "Show password"
        )
    }
}

/**
 * This composable function is a structure of the recipe list data and how it is displayed on the screen. It takes a recipeList
 * as a parameter to display this list as a Column of "rows" that contain an image of the recipe, title and its difficulty
 * values using ConstraintLayout.
 *
 * @param recipeList the list of Recipes to display.
 * @param navController the NavHostController responsible for navigating between screens.
 * @param dataViewModel a DataViewModel used for saving the id of the selected recipe.
 * @param showButtons a Boolean flag indicating whether or not to show the create and explore buttons at the bottom of the screen.
 * @param userId the current user ID.
 * @param firestore firebase instance.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeList(
    recipeList: List<Recipe>,
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel(),
    showButtons: Boolean,
    userId: String,
    firestore: FirebaseFirestore = Firebase.firestore
) {
    var recipeId: String?
    val openAlertDialog = remember { mutableStateOf(false) }
    var temporaryRecipeId by rememberSaveable { mutableStateOf("") }
    var context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {

            LazyColumn {
                // setting data for each item
                itemsIndexed(recipeList) { index, item ->

                    ConstraintLayout(
                        modifier = Modifier
                            .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                recipeList[index].id?.let {
                                    recipeId = it
                                    dataViewModel.saveString(recipeId!!, RECIPE_ID)
                                    Log.d("TEST", "${recipeList[index].id}")
                                }
                                navController.navigate(Screen.Recipe.route)
                            }
                    ) {
                        val (photo, title, difficulty, delete) = createRefs()
                        Box(
                            modifier = Modifier
                                .constrainAs(photo) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top, 5.dp)

                                }
                        ) {
                            // Display the recipe photo
                            recipeList[index]?.photo?.let {
                                Image(
                                    painter = rememberImagePainter(it),
                                    contentDescription = "Recipe Image",
                                    modifier = Modifier
                                        .height(120.dp)
                                        .width(155.dp)
                                        .fillMaxSize()
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .padding(top = 10.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        recipeList[index]?.title?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .constrainAs(title) {
                                        start.linkTo(photo.end, 16.dp)
                                        end.linkTo(parent.end)
                                        top.linkTo(photo.top, margin = 16.dp)
                                        width = Dimension.fillToConstraints
                                    },
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        recipeList[index]?.difficulty?.let {
                            Text(
                                text = "Difficulty: $it",
                                modifier = Modifier
                                    .padding(
                                        top = 2.dp,
                                        start = 4.dp,
                                        end = 4.dp,
                                        bottom = 4.dp
                                    )
                                    .constrainAs(difficulty) {
                                        start.linkTo(title.start)
                                        end.linkTo(title.end)
                                        top.linkTo(
                                            title.bottom,
                                            0.dp
                                        )
                                    },
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (showButtons) {
                            recipeList[index]?.id?.let { recipeId ->
                                IconButton(
                                    onClick = {
                                        temporaryRecipeId = recipeId
                                        openAlertDialog.value = true
                                    },
                                    modifier = Modifier
                                        .constrainAs(delete) {
                                            start.linkTo(difficulty.start)
                                            end.linkTo(difficulty.end)
                                            top.linkTo(difficulty.bottom, 1.dp)
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete recipe"
                                    )
                                }
                            }
                            if (openAlertDialog.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        openAlertDialog.value = false
                                    },
                                    title = {
                                        Text(
                                            text = stringResource(R.string.are_you_sure),
                                            fontFamily = Railway,
                                            fontSize = 22.sp
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = stringResource(R.string.pressing_confirm_delete),
                                            fontFamily = Railway,
                                            fontSize = 16.sp
                                        )
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            temporaryRecipeId.let {
                                                firestore
                                                    .collection("users")
                                                    .document(userId)
                                                    .collection("privateRecipes")
                                                    .document(it)
                                                    .delete()
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            "RecipeList",
                                                            "Recipe deleted successfully"
                                                        )
                                                    }
                                            }
                                            openAlertDialog.value = false
                                            Toast.makeText(
                                                context,
                                                "The recipe has been deleted.",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }) {
                                            Text(
                                                text = stringResource(id = R.string.confirm),
                                                fontFamily = Railway,
                                                fontSize = 16.sp
                                            )
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { openAlertDialog.value = false }) {
                                            Text(
                                                text = stringResource(id = R.string.cancel),
                                                fontFamily = Railway,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showButtons) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {

                ElevatedButton(
                    onClick = {
                        navController.navigate(Screen.Create.route)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp)
                        .weight(0.5f)

                ) {
                    Text(
                        stringResource(R.string.create_new),
                        fontFamily = Railway
                    )
                }

                ElevatedButton(
                    onClick = {
                        navController.navigate(Screen.Explore.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp)
                        .weight(0.5f)
                ) {
                    Text(
                        stringResource(R.string.other_recipes),
                        fontFamily = Railway,
                    )
                }
            }
        }
    }
}