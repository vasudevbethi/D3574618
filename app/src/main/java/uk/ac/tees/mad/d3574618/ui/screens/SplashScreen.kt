package uk.ac.tees.mad.d3574618.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination

object SplashScreenDestination : NavigationDestination {
    override val route = "splash" // Route for navigating to the splash screen
    override val titleRes: Int = R.string.app_name // Title resource for the splash screen
}

@Composable
fun SplashScreen(onFinish: () -> Unit) {

    // Animation duration for texts
    val animDuration = remember {
        Animatable(0f)
    }

    // Column layout for the splash screen content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Background color for the splash screen
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.height(100.dp)) // Spacer for layout

        // Loader animation component
        LoaderAnimation(
            modifier = Modifier.size(400.dp), anim = R.raw.exchange_reusable
        )

        // App name text
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 32.sp, // Font size
            fontWeight = FontWeight.W700, // Font weight
            textAlign = TextAlign.Center, // Text alignment
            lineHeight = 35.sp, // Line height
            modifier = Modifier
                .alpha(animDuration.value) // Alpha animation for text
                .scale(animDuration.value) // Scale animation for text
        )

        Spacer(modifier = Modifier.height(5.dp)) // Spacer for layout

        // Subtitle text
        Text(
            text = "Join the reusable revolution.",
            fontSize = 18.sp, // Font size
            fontWeight = FontWeight.Bold, // Font weight
            modifier = Modifier
                .alpha(animDuration.value) // Alpha animation for text
                .scale(animDuration.value) // Scale animation for text
        )
    }

    // Launch effect for animation and navigation
    LaunchedEffect(key1 = true) {
        animDuration.animateTo(
            1f,
            animationSpec = tween(1500)
        ) // Animation to scale the splash screen elements
        delay(2000L) // Delay before navigating to the next screen
        onFinish()
    }
}

@Composable
fun LoaderAnimation(modifier: Modifier, anim: Int) {
    // Retrieve Lottie composition
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(anim))

    // Lottie animation component
    LottieAnimation(
        composition = composition,
        modifier = modifier
    )
}