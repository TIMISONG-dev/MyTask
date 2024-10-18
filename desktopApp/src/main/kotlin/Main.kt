import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var checkPass by remember { mutableStateOf("") }

        var inn by remember { mutableStateOf("") }
        var card by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var adphone by remember { mutableStateOf("") }
        var pageLevel by remember { mutableIntStateOf(1) }

        var isClickedF by remember { mutableStateOf(false) }

        var isClickedS by remember { mutableStateOf(false) }

        var pickHeight by remember { mutableStateOf(0.dp) }

        val infiniteTransition = rememberInfiniteTransition(label = "")

        val color1 by infiniteTransition.animateColor(
            initialValue = Color.Red,
            targetValue = Color.Blue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        val color2 by infiniteTransition.animateColor(
            initialValue = Color.Blue,
            targetValue = Color.Red,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        val scope = rememberCoroutineScope()
        var mode by remember { mutableStateOf("Login") }

        var passwordVisibility: Boolean by remember { mutableStateOf(false) }
        var checkPassVisibility: Boolean by remember { mutableStateOf(false) }
        var pageVis by remember { mutableStateOf(true) }

        Column (
            Modifier
                .background(Color.White)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Column (
                Modifier
                    .padding(8.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.LightGray)
                    .wrapContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Do you have account?",
                    Modifier
                        .padding(8.dp),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    scope.launch {
                        // sheetState.bottomSheetState.expand()
                    }
                    pickHeight = 100.dp
                    mode = "Register"
                },
                ) {
                    Text (
                        text = "Reg"
                    )
                }
                Text(
                    text = "or",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Button(onClick = {
                    scope.launch {
                        pageLevel = 1
                        // sheetState.bottomSheetState.expand()
                    }
                    pageLevel = 1
                    pickHeight = 100.dp
                    mode = "Login"
                }) {
                    Text (
                        text = "Log"
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
