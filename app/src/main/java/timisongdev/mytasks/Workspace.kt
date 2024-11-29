package timisongdev.mytasks

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.*
import timisongdev.mytasks.ui.theme.MyTasksTheme

class Workspace : ComponentActivity() {

    companion object {
        var isInit = mutableStateOf(false)

        val nextStep = mutableStateOf(false)

        val dragAnim = mutableStateOf(false)
        var startLocation = ""

        fun getMap(context: Context): MapView {
            val mapView = MapView(context)

            return mapView
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isInit.value) {
            MapKitFactory.setApiKey(App.MAPKIT_API_KEY)
            MapKitFactory.initialize(this)
            isInit.value = true
        }
        enableEdgeToEdge()
        setContent {
            MyTasksTheme {
                Scaffold {
                    Work()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Work() {

    val context = LocalContext.current

    // Разрешение на геолокацию для карты
    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            location?.let {
                val startLocation = "${it.latitude},${it.longitude}"
                Workspace.startLocation = startLocation
            }
        }

    val title = listOf(
        "Payments",
        "Donate",
        "Stars",
        "Steps",
        "Map",
        "Last order",
        "Work",
        "Slots",
        "Support",
        "Profile"
    )

    val cells = remember{ mutableIntStateOf(2) }
    val openCell = remember { mutableIntStateOf(-1) }

    Column (
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(cells.intValue),
            modifier = Modifier
                .fillMaxHeight(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center
        ) {
            items(title.size) { index ->
                GridItem(
                    title = title[index],
                    index = index,
                    cells = cells,
                    openCell = openCell
                )
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition", "SetJavaScriptEnabled")
@Composable
fun GridItem(title: String, index: Int, cells: MutableIntState, openCell: MutableIntState) {

    val expanded = remember { mutableStateOf(false) }

    // Скобелевская улица, 19
    // Чечерский проезд, 51
    val workerLocation = remember { mutableStateOf(Workspace.startLocation) }
    val orderLocation = remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val defaultOnColor = MaterialTheme.colorScheme.surface

    val context = LocalContext.current

    val currency = "$"

    val pay = listOf(
        1000,
        2000,
        102,
        130,
        1000,
        2000,
        102,
        130,
        1000,
        2000,
        102,
        130
    )

    val donate = listOf(
        500,
        102,
        370
    )

    val stars = listOf(
        3,
        10,
        9
    )

    val steps = listOf(
        20000,
        10231,
        11984
    )

    val orders = listOf(
        "Москва, Проспект Буденного, 22к1",
        "Щербинка, Главная улица, 1к1"
    )

    val slots = listOf(
        "10:00 - 18:00",
        "13:32 - 19:58"
    )

    val icons = listOf(
        R.drawable.ic_payments,
        R.drawable.ic_fastfood_near,
        R.drawable.ic_star_half,
        R.drawable.ic_steps,
        R.drawable.ic_map,
        R.drawable.ic_last_order,
        R.drawable.ic_start_work,
        R.drawable.ic_slots,
        R.drawable.ic_support_agent,
        R.drawable.ic_account
    )

    val indexToListMap = mapOf(
        0 to pay,
        1 to donate,
        2 to stars,
        3 to steps,
        5 to orders,
        7 to slots
    )

    val exHeight by animateDpAsState(
        targetValue = if (expanded.value) screenHeight else 200.dp,
        animationSpec = tween(300),
        label = "expanded height"
    )

    val exWidth by animateDpAsState(
        targetValue = if (expanded.value) screenWidth else 200.dp,
        animationSpec = tween(300),
        label = "expanded width"
    )

    // Показываем либо один выбранный tile Grid или все tiles
    if (openCell.intValue == -1 || openCell.intValue == index) {
        Column(
            Modifier
                .padding(8.dp)
                .height(exHeight)
                .width(exWidth)
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .clickable {
                    expanded.value = !expanded.value
                    if (expanded.value) {
                        cells.intValue = 1
                        openCell.intValue = index
                    } else {
                        cells.intValue = 2
                        openCell.intValue = -1
                    }
                }
                .animateContentSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = if (expanded.value) 20.sp else 16.sp,
                color = defaultOnColor,
                modifier = Modifier
                    .padding(8.dp)
            )
            if (expanded.value) {
                Workspace.nextStep.value = false
                val list = indexToListMap[index] ?: emptyList<Any>()
                Column (
                    Modifier
                        .then(if (index != 8) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    // Support
                    if (index == 8) {
                        val viewModel: Chat.ChatViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return Chat.ChatViewModel(context) as T
                            }
                        })

                        Chat.ChatBlank(messages = viewModel.messages) { message ->
                            viewModel.sendMessage(message)
                        }
                    }
                    if (index == 9) {
                        Text (
                            "Hello, User. Your information:",
                            fontSize = 18.sp,
                            color = defaultOnColor
                        )
                    }
                    // YandexMap
                    if (index == 4) {
                        YandexMap.Mapa()
                    }
                    if (index == 6) {
                        TextField(
                            value = workerLocation.value,
                            onValueChange = {
                                    newText -> workerLocation.value = newText
                            },
                            Modifier
                                .fillMaxWidth(),
                            label = {
                                Text (
                                    "Worker location"
                                )
                            }
                        )
                        TextField(
                            value = orderLocation.value,
                            onValueChange = {
                                    newText -> orderLocation.value = newText
                            },
                            Modifier
                                .fillMaxWidth(),
                            label = {
                                Text (
                                    "Order location"
                                )
                            },
                            supportingText = {
                                Text (
                                    "Пример: Москва, Южное Бутово, Чечерский проезд, 51"
                                )
                            }
                        )
                        Button(onClick = {
                            GlobalScope.launch(Dispatchers.Main) {
                                if (Working.compareLocations(workerLocation.value, orderLocation.value, App.GEO_API_KEY)) {
                                    expanded.value = !expanded.value
                                    openCell.intValue = 4
                                    cells.intValue = 1
                                    YandexMap.mapMode.value = "route"
                                    Workspace.nextStep.value = true
                                }
                            }
                        }) {
                            Text(
                                "Start work"
                            )
                        }
                    } else {
                        Spacer(Modifier.padding(8.dp))
                        list.forEach { item ->
                            Row(
                                Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .width(300.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        icons.getOrNull(index) ?: R.drawable.ic_visibility_off
                                    ),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(32.dp)
                                )
                                // Some lists
                                if (index < 4 || index == 5 || index == 7) {
                                    Text(
                                        text = "$item",
                                        Modifier
                                            .padding(8.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = if (index == 5) 18.sp else 24.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (index == 0 || index == 1) {
                                        Text(
                                            text = currency,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.padding(8.dp))
                        }
                    }
                }
            } else {
                Row(
                    Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(icons.getOrNull(index) ?: R.drawable.ic_visibility_off),
                        contentDescription = null,
                        tint = defaultOnColor,
                        modifier = Modifier.size(64.dp)
                    )
                    if (index in 0..3) {
                        Text(
                            text = when (index) {
                                0 -> pay[pay.size - 1].toString()
                                1 -> donate[donate.size - 1].toString()
                                2 -> stars[stars.size - 1].toString()
                                3 -> steps[steps.size - 1].toString()
                                else -> "Error lol"
                            },
                            Modifier.padding(5.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = defaultOnColor
                        )
                        if (index == 0 || index == 1) {
                            Text(
                                text = currency,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = defaultOnColor
                            )
                        }
                    }
                }
                if (Workspace.nextStep.value) {
                    Text (
                        "Tap to open tab!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = defaultOnColor
                    )
                }
            }
        }
    }
}