package timisongdev.mytasks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import timisongdev.mytasks.ui.theme.MyTasksTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTasksTheme {
                Scaffold {
                    Greeting()
                }
            }
        }
    }
}

fun pageLevels(level: Int): List<String> {
    return when (level) {
        1 -> listOf (
            "Type your Email",
            "Type your Name",
            "Type your Password",
            "Type Password again"
        )
        2 -> listOf (
            "Type your INN",
            "Type number card",
            "Type your phone",
            "Additional phone"
        )
        else -> listOf (
            "Error of level page",
            "Send me screen with",
            "This page",
            "Thanks."
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var checkPass by remember { mutableStateOf("") }

    var inn by remember { mutableStateOf("") }
    var card by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var adphone by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var pageLevel by remember { mutableIntStateOf(1) }

    val material = MaterialTheme.colorScheme

    var isClickedF by remember { mutableStateOf(false) }

    var isClickedS by remember { mutableStateOf(false) }

    val firstBox by animateColorAsState(
        targetValue = if (isClickedF) material.primary else material.primaryContainer, label = ""
    )
    val secondBox by animateColorAsState(
        targetValue = if (isClickedS) material.primary else material.primaryContainer, label = ""
    )

    var pickHeight by remember { mutableStateOf(0.dp) }
    val sheetState = rememberBottomSheetScaffoldState(bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false))

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
    var checkKeyVisibility: Boolean by remember { mutableStateOf(false) }
    var pageVis by remember { mutableStateOf(true) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val context = LocalContext.current

    val activity = (LocalContext.current as? Activity)

    Column (
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column (
            Modifier
                .padding(8.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Do you have account?",
                Modifier
                    .padding(8.dp),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Button(onClick = {
                scope.launch {
                    sheetState.bottomSheetState.expand()
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
                    sheetState.bottomSheetState.expand()
                }
                pageLevel = 1
                pickHeight = 100.dp
                mode = "Login"
            },
            ) {
                Text (
                    text = "Log"
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            Column (
                Modifier
                    .heightIn(min = 100.dp, max = screenHeight - 100.dp)
                    .padding(8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    buildAnnotatedString {
                        append("Welcome to ")
                        withStyle(
                            style = SpanStyle(
                                brush = Brush.linearGradient(colors = listOf(color1, color2))
                            )
                        ) {
                            append(mode)
                        }
                        append(" page!")
                    },
                    Modifier
                        .padding(8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(
                    Modifier
                        .padding(24.dp)
                )
                AnimatedVisibility(
                    visible = pageVis,
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Column {
                        if (pageLevel < 3) {
                            TextField(
                                modifier = Modifier
                                    .width(300.dp),
                                value = if (pageLevel == 1) email else inn,
                                onValueChange =
                                {
                                    newText -> if (pageLevel == 1) {
                                        email = newText
                                    } else {
                                        inn = newText
                                    }
                                },
                                label =
                                {
                                    Text(
                                        text = pageLevels(pageLevel).getOrNull(0) ?: ""
                                    )
                                },
                                singleLine = true,
                                keyboardOptions =
                                if (pageLevel == 1) {
                                    KeyboardOptions(keyboardType = KeyboardType.Email)
                                } else {
                                    KeyboardOptions(keyboardType = KeyboardType.Number)
                                },
                                leadingIcon =
                                if (pageLevel == 1) {
                                    {
                                        Icon (
                                            Icons.Outlined.Email,
                                            contentDescription = "Email"
                                        )
                                    }
                                } else {
                                    {
                                        Icon (
                                            painter = painterResource(id = R.drawable.ic_inn),
                                            contentDescription = "INN"
                                        )
                                    }
                                }
                            )
                            if (mode == "Register") {
                                TextField(
                                    modifier = Modifier
                                        .width(300.dp),
                                    value = if (pageLevel == 1) name else card,
                                    onValueChange =
                                    {
                                        newText -> if (pageLevel == 1) {
                                            name = newText
                                        } else {
                                            card = newText
                                        }
                                    },
                                    label =
                                    {
                                        Text(
                                            text = pageLevels(pageLevel).getOrNull(1) ?: ""
                                        )
                                    },
                                    singleLine = true,
                                    keyboardOptions =
                                    if (pageLevel == 1) {
                                        KeyboardOptions(keyboardType = KeyboardType.Text)
                                    } else {
                                        KeyboardOptions(keyboardType = KeyboardType.Number)
                                    },
                                    leadingIcon = if (pageLevel == 1) {
                                        {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_name),
                                                contentDescription = "Name"
                                            )
                                        }
                                    } else {
                                        {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_credit_card),
                                                contentDescription = "Card"
                                            )
                                        }
                                    }
                                )
                            }
                            TextField(
                                modifier = Modifier
                                    .width(300.dp),
                                value = if (pageLevel == 1) password else phone,
                                onValueChange =
                                {
                                    newText -> if (pageLevel == 1) {
                                        password = newText
                                    } else {
                                        phone = newText
                                    }
                                },
                                label =
                                {
                                    Text(
                                        text = pageLevels(pageLevel).getOrNull(2) ?: ""
                                    )
                                },
                                singleLine = true,
                                visualTransformation = if (passwordVisibility || pageLevel > 1) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions =
                                if (pageLevel == 1) {
                                    KeyboardOptions(keyboardType = KeyboardType.Password)
                                } else {
                                    KeyboardOptions(keyboardType = KeyboardType.Phone)
                                },
                                leadingIcon = if (pageLevel == 1) {
                                    {
                                        Icon(Icons.Outlined.Lock, contentDescription = "Password")
                                    }
                                } else {
                                    {
                                        Icon(Icons.Outlined.Phone, contentDescription = "Phone")
                                    }
                                },
                                trailingIcon = if (pageLevel == 1) {
                                    {
                                        val image = if (passwordVisibility) {
                                            R.drawable.ic_visibility
                                        } else {
                                            R.drawable.ic_visibility_off
                                        }
                                        IconButton(onClick = {
                                            passwordVisibility = !passwordVisibility
                                        }) {
                                            Icon(
                                                painter = painterResource(id = image),
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                } else null
                            )
                            if (mode == "Register") {
                                TextField(
                                    modifier = Modifier
                                        .width(300.dp),
                                    value = if (pageLevel == 1) checkPass else adphone,
                                    onValueChange =
                                    {
                                        newText -> if (pageLevel == 1) {
                                            checkPass = newText
                                        } else {
                                            adphone = newText
                                        }
                                    },
                                    label =
                                    {
                                        Text(
                                            text = pageLevels(pageLevel).getOrNull(3) ?: ""
                                        )
                                    },
                                    singleLine = true,
                                    visualTransformation = if (checkPassVisibility || pageLevel > 1) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions =
                                    if (pageLevel == 1) {
                                        KeyboardOptions(keyboardType = KeyboardType.Password)
                                    } else {
                                        KeyboardOptions(keyboardType = KeyboardType.Phone)
                                    },
                                    leadingIcon = if (pageLevel == 1) {
                                        {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_enhanced_encryption),
                                                contentDescription = "Password"
                                            )
                                        }
                                    } else {
                                        {
                                            Icon(
                                                Icons.Outlined.Warning,
                                                contentDescription = "Additional phone"
                                            )
                                        }
                                    },
                                    trailingIcon = if (pageLevel == 1) {
                                        {
                                            val image = if (checkPassVisibility) {
                                                R.drawable.ic_visibility
                                            } else {
                                                R.drawable.ic_visibility_off
                                            }
                                            IconButton(onClick = {
                                                checkPassVisibility = !checkPassVisibility
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = image),
                                                    contentDescription = ""
                                                )
                                            }
                                        }
                                    } else null
                                )
                            }
                            TextField(
                                modifier = Modifier
                                    .width(300.dp),
                                value = key,
                                onValueChange =
                                {
                                        newText -> key = newText
                                },
                                label =
                                {
                                    Text(
                                        text = "Key (for admins)"
                                    )
                                },
                                singleLine = true,
                                visualTransformation = if (checkKeyVisibility || pageLevel > 1) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Build,
                                        contentDescription = "Admin key"
                                    )
                                },
                                trailingIcon = {
                                    val image = if (checkKeyVisibility) {
                                        R.drawable.ic_visibility
                                    } else {
                                        R.drawable.ic_visibility_off
                                    }
                                    IconButton(onClick = {
                                        checkKeyVisibility = !checkKeyVisibility
                                    }) {
                                        Icon(
                                            painter = painterResource(id = image),
                                            contentDescription = ""
                                        )
                                    }
                                }
                            )
                        } else {
                            Row (
                                Modifier
                                    .wrapContentSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Column (
                                    Modifier
                                        .padding(8.dp)
                                        .width(200.dp)
                                        .height(200.dp)
                                        .weight(1F)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(firstBox)
                                        .clickable {
                                            isClickedF = true
                                            isClickedS = false
                                        }
                                        .wrapContentSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon (
                                        painter = painterResource(R.drawable.ic_footprint), contentDescription = "",
                                        Modifier
                                            .size(82.dp)
                                            .padding(8.dp),
                                        tint = if (isClickedF) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text (
                                        text = "Walking",
                                        Modifier
                                            .padding(8.dp),
                                        color = if (isClickedF) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 18.sp
                                    )
                                }
                                Column (
                                    Modifier
                                        .padding(8.dp)
                                        .width(200.dp)
                                        .height(200.dp)
                                        .weight(1F)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(secondBox)
                                        .clickable {
                                            isClickedS = true
                                            isClickedF = false
                                        }
                                        .wrapContentSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon (
                                        painter = painterResource(R.drawable.ic_pedal_bike), contentDescription = "",
                                        Modifier
                                            .size(82.dp)
                                            .padding(8.dp),
                                        tint = if (isClickedS) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text (
                                        text = "Biker",
                                        Modifier
                                            .padding(8.dp),
                                        color = if (isClickedS) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        if (mode == "Register") {
                            scope.launch {
                                if (pageLevel != 3) {
                                    pageVis = !pageVis
                                    delay(500)
                                    pageLevel += 1
                                    pageVis = true
                                } else {
                                    context.startActivity(Intent(context, Workspace::class.java))
                                    activity?.finish()
                                }
                            }
                        } else {
                            // Login check
                            context.startActivity(Intent(context, Workspace::class.java))
                            activity?.finish()
                        }
                    },
                    Modifier
                        .padding(8.dp)
                )
                {
                    Text (
                        text = "Enter"
                    )
                }
                if (pageLevel > 1) {
                    Button(
                        onClick = {
                            pageVis = !pageVis
                            scope.launch {
                                delay(500)
                                if (pageLevel != 1)
                                    pageLevel -= 1
                                pageVis = true
                            }
                        },
                        Modifier
                            .padding(8.dp)
                    )
                    {
                        Text (
                            text = "Back"
                        )
                    }
                }
                Text (
                    text = "Close Tab",
                    Modifier
                        .padding(8.dp)
                        .clickable {
                            scope.launch {
                                sheetState.bottomSheetState.hide()
                            }
                        },
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        Modifier
            .heightIn(min = 100.dp, max = 500.dp)
            .padding(8.dp),
        scaffoldState = sheetState,
        sheetPeekHeight = pickHeight,
        sheetSwipeEnabled = true,
        content = {
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyTasksTheme {
        Greeting()
    }
}