package timisongdev.mytasks

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.*
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import timisongdev.mytasks.ui.theme.MyTasksTheme

class Workspace : ComponentActivity() {

    companion object {
        var isInit = mutableStateOf(false)
        var startLocation = ""
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

    val mapView = remember { MapView(context) }

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
        "Support"
    )

    val cells = remember{ mutableIntStateOf(2) }
    val openCell = remember { mutableIntStateOf(-1) }

    Column (
        Modifier.fillMaxSize(),
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
                    openCell = openCell,
                    mapView = mapView
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "SetJavaScriptEnabled")
@Composable
fun GridItem(title: String, index: Int, cells: MutableIntState, openCell: MutableIntState, mapView: MapView) {

    val expanded = remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val currency = "$"

    val mapMode = remember { mutableStateOf("map") }

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
        null,
        R.drawable.ic_last_order,
        null,
        R.drawable.ic_slots,
        R.drawable.ic_support_agent
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
        animationSpec = tween(300), label = ""
    )

    val exWidth by animateDpAsState(
        targetValue = if (expanded.value) screenWidth else 200.dp,
        animationSpec = tween(300), label = ""
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
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
            )
            if (index != 4 && index != 6) {
                if (expanded.value) {
                    // Получаем данные для текущего индекса
                    val list = indexToListMap[index] ?: emptyList<Any>()
                    Column (
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
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
                                    painter = painterResource(icons.getOrNull(index) ?: R.drawable.ic_visibility_off),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(32.dp)
                                )
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
                } else {
                    Row(
                        Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(icons.getOrNull(index) ?: R.drawable.ic_visibility_off),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
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
                }
            } else {
                when (index) {
                    4 -> {
                        if (expanded.value) {
                            Button(onClick = {
                                if (mapMode.value == "route"){
                                    mapMode.value = "map"
                                } else {
                                    mapMode.value = "route"
                                }
                            }) {
                                Text(
                                    "Route / Map"
                                )
                            }
                        }
                        if (expanded.value && mapMode.value == "map") {
                            AndroidView(
                                factory = {
                                    mapView.apply {
                                        map.isRotateGesturesEnabled = true
                                        map.isTiltGesturesEnabled = true
                                        map.isScrollGesturesEnabled = true
                                        showUserLocation()
                                    }
                                },
                                Modifier
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .height(screenHeight)
                            )
                        } else {
                            if (expanded.value && mapMode.value == "route") {
                                AndroidView(
                                    factory = { context ->
                                        WebView(context).apply {
                                            settings.javaScriptEnabled = true
                                            webViewClient = WebViewClient()
                                            settings.useWideViewPort = true
                                            settings.loadWithOverviewMode = true
                                            settings.javaScriptCanOpenWindowsAutomatically = true
                                            webChromeClient = object : WebChromeClient() {
                                                override fun onGeolocationPermissionsShowPrompt(
                                                    origin: String?,
                                                    callback: GeolocationPermissions.Callback?
                                                ) {
                                                    callback?.invoke(origin, true, false)
                                                }
                                            }
                                            webViewClient = object : WebViewClient() {
                                                @SuppressLint("QueryPermissionsNeeded")
                                                @Deprecated("Deprecated in Java")
                                                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                                    url?.let {
                                                        val uri = Uri.parse(it)
                                                        if (it.startsWith("intent://")) {
                                                            try {
                                                                // Попытка распарсить intent из URI
                                                                val intent = Intent.parseUri(it, Intent.URI_INTENT_SCHEME)
                                                                val appPackageName = intent.data?.schemeSpecificPart

                                                                // Проверка наличия активности для этого intent
                                                                val packageManager = view?.context?.packageManager
                                                                val resolveInfo = packageManager?.resolveActivity(intent, PackageManager.MATCH_ALL)

                                                                if (resolveInfo != null) {
                                                                    // Приложение установлено — запускаем его
                                                                    context.startActivity(intent)
                                                                } else {
                                                                    // Если приложение не установлено — открываем Google Play или fallback URL
                                                                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                                                    if (fallbackUrl != null) {
                                                                        // Безопасно вызываем loadUrl через view?.loadUrl
                                                                        view?.loadUrl(fallbackUrl)
                                                                    } else {
                                                                        try {
                                                                            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                                                                            view?.context?.startActivity(playStoreIntent)
                                                                        } catch (e: Exception) {
                                                                            // На случай если Google Play недоступен, можно показать ошибку
                                                                            e.printStackTrace()
                                                                        }
                                                                    }
                                                                }
                                                                return true
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        } else {
                                                            try {
                                                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                                                view?.context?.startActivity(intent)
                                                                return true
                                                            } catch (e: ActivityNotFoundException) {
                                                                return false
                                                            }
                                                        }
                                                    }
                                                    return false // Оставляем стандартное поведение для остальных URL
                                                }
                                            }
                                            val url = "https://yandex.ru/maps/?rtext=${Workspace.startLocation}~55.755814,37.617635&rtt=pd"
                                            loadUrl(url)
                                        }
                                    },
                                    Modifier
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .height(screenHeight)
                                )
                            } else {
                                Text(
                                    "Map. Closed",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    6 -> {
                        Button(onClick = {}) {
                            Text("Start work")
                        }
                    }
                }
            }
        }
    }
}

fun MapView.showUserLocation() {
    if (Workspace.isInit.value) {
        val locationManager: LocationManager = MapKitFactory.getInstance().createLocationManager()

        locationManager.requestSingleUpdate(object : LocationListener {
            override fun onLocationUpdated(location: com.yandex.mapkit.location.Location) {

                val userLocation = Point(location.position.latitude, location.position.longitude)
                map.move(
                    CameraPosition(
                        userLocation,
                        /* zoom = */ 17.0f,
                        /* azimuth = */ 150.0f,
                        /* tilt = */ 30.0f
                    )
                )
            }

            override fun onLocationStatusUpdated(status: LocationStatus) {
            }
        })
    }
}