package uk.ac.aber.dcs.cs39440.mealbay.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import uk.ac.aber.dcs.cs39440.mealbay.model.DataViewModel
import uk.ac.aber.dcs.cs39440.mealbay.model.Recipe
import uk.ac.aber.dcs.cs39440.mealbay.storage.RECIPE_ID
import uk.ac.aber.dcs.cs39440.mealbay.ui.navigation.Screen


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
        imeAction = imeAction, //  to be performed when the user presses the action button on the email input field. This action will move the focus to the next input field in the form
        onAction = onAction,
    )

}

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
            textColor = Color(0xFF000000),
            errorCursorColor = Color(0xFF9C4234),
            unfocusedBorderColor = Color(0xFF000000),
            focusedBorderColor = Color(0xFF9C4234),
            focusedLabelColor = Color(0xFF9C4234),
            unfocusedLabelColor = Color(0xFF000000),
            cursorColor = Color(0xFF9C4234)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction
    )
}

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
            textColor = Color(0xFF000000),
            errorCursorColor = Color(0xFF9C4234),
            unfocusedBorderColor = Color(0xFF000000),
            focusedBorderColor = Color(0xFF9C4234),
            focusedLabelColor = Color(0xFF9C4234),
            unfocusedLabelColor = Color(0xFF000000),
            cursorColor = Color(0xFF9C4234)
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


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeList(
    context: Context,
    recipeList: List<Recipe>,
    navController: NavHostController,
    dataViewModel: DataViewModel = hiltViewModel()
) {
    var recipeId: String?

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        Log.d("recipeListSize", "Size: ${recipeList.size}")
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
                    val (photo, title, difficulty) = createRefs()
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
                        androidx.compose.material3.Text(
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
                        androidx.compose.material3.Text(
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
                }
            }
        }
    }
}





