package uk.ac.aber.dcs.cs39440.mealbay.ui.onboarding

import android.annotation.SuppressLint
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.aber.dcs.cs39440.mealbay.R


@SuppressLint("RememberReturnType")
@Preview
@Composable
fun SplashScreen(navController: NavController = NavController(context = LocalContext.current)) {

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 950,
                easing = {
                    OvershootInterpolator(6f).getInterpolation(it)
                })
        )
        delay(2000L)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale.value),
        shape = RectangleShape
    )
    {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                modifier = Modifier
                    .size(340.dp)
                    .clip(RoundedCornerShape(25.dp)),
                painter = painterResource(id = R.drawable.logosmall),
                contentDescription = stringResource(id = R.string.logo),
                contentScale = ContentScale.Crop
            )
        }
    }
}