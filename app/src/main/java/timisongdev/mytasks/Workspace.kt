package timisongdev.mytasks

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.navigation.automotive.NavigationFactory
import com.yandex.mapkit.navigation.automotive.NavigationListener
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.runtime.Error
import timisongdev.mytasks.ui.theme.MyTasksTheme

class Workspace : ComponentActivity() {

    companion object {
        var isInit = mutableStateOf(false)
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

    val title = listOf(
        "Payments",
        "Donate",
        "Stars",
        "Steps",
        "Map",
        "Last order",
        "Work",
        "Slots"
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
                    title = "${title[index]}, $index",
                    index = index,
                    cells = cells,
                    openCell = openCell
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GridItem(title: String, index: Int, cells: MutableIntState, openCell: MutableIntState) {

    val expanded = remember { mutableStateOf(false) }

    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val currency = "$"

    val pay = listOf(
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

    val starts = listOf(
        3,
        10,
        9
    )

    val steps = listOf(
        20000,
        10231,
        11984
    )

    val exHeight by animateDpAsState(
        targetValue = if (expanded.value) screenHeight else 200.dp,
        animationSpec = tween(300), label = ""
    )

    val exWidth by animateDpAsState(
        targetValue = if (expanded.value) screenWidth else 200.dp,
        animationSpec = tween(300), label = ""
    )

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
                    for (item in if (index == 0) pay else if (index == 1) donate else if (index == 2) starts else if (index == 3) steps else steps) {
                        Spacer(Modifier.padding(8.dp))
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
                                painter =
                                when (index) {
                                    0 -> painterResource(R.drawable.ic_payments)
                                    1 -> painterResource(R.drawable.ic_fastfood_near)
                                    2 -> painterResource(R.drawable.ic_star_half)
                                    3 -> painterResource(R.drawable.ic_steps)
                                    5 -> painterResource(R.drawable.ic_last_order)
                                    7 -> painterResource(R.drawable.ic_slots)
                                    else -> painterResource(R.drawable.ic_visibility_off)
                                },
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .size(32.dp)
                            )
                            if (index == 0 || index == 1 || index == 2 || index == 3) {
                                Text(
                                    text = when (index) {
                                        0 -> "$item"
                                        1 -> "$item"
                                        2 -> "$item"
                                        3 -> "$item"
                                        else -> "Error lol"
                                    },
                                    Modifier
                                        .padding(5.dp),
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
                    Row(
                        Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter =
                            when (index) {
                                0 -> painterResource(R.drawable.ic_payments)
                                1 -> painterResource(R.drawable.ic_fastfood_near)
                                2 -> painterResource(R.drawable.ic_star_half)
                                3 -> painterResource(R.drawable.ic_steps)
                                5 -> painterResource(R.drawable.ic_last_order)
                                7 -> painterResource(R.drawable.ic_slots)
                                else -> painterResource(R.drawable.ic_visibility_off)
                            },
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(64.dp)
                        )
                        if (index == 0 || index == 1 || index == 2 || index == 3) {
                            Text(
                                text = when (index) {
                                    0 -> pay[pay.size - 1].toString()
                                    1 -> donate[donate.size - 1].toString()
                                    2 -> starts[starts.size - 1].toString()
                                    3 -> steps[steps.size - 1].toString()
                                    else -> "Error lol"
                                },
                                Modifier
                                    .padding(5.dp),
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
                if (index == 4) {
                    if (expanded.value) {
                        Button(onClick = { BuildRoute(context) }) {
                            Text(
                                "Road"
                            )
                        }
                        AndroidView(
                            factory = { context ->
                                MapView(context).apply {
                                    map.isRotateGesturesEnabled = true
                                    map.isTiltGesturesEnabled = true
                                    map.isScrollGesturesEnabled = true
                                    showUserLocation()
                                }
                            },
                            Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(24.dp))
                        )
                    } else {
                        Text (
                            "Map. Closed"
                        )
                    }
                } else {
                    Text(
                        text = "Hello World"
                    )
                }
            }
        }
    }
}
fun BuildRoute(context: Context) {
    if (Workspace.isInit.value) {
        // Точки маршрута
        val requestPoints = listOf(
            RequestPoint(Point(25.190614, 55.265616), RequestPointType.WAYPOINT, null, null),
            RequestPoint(Point(25.187532, 55.275413), RequestPointType.WAYPOINT, null, null),
            RequestPoint(Point(25.189279, 55.282246), RequestPointType.WAYPOINT, null, null),
            RequestPoint(Point(25.196605, 55.280940), RequestPointType.WAYPOINT, null, null)
        )

        // Создание экземпляра Navigation
        val navigation = NavigationFactory.createNavigation(DrivingRouterType.COMBINED)
        val mapObj : MapObjectCollection
        val mapView : MapView = MapView(context)

        mapObj = mapView.mapWindow.map.mapObjects.addCollection()

        // Слушатель для отслеживания результатов
        val navigationListener = object : NavigationListener {
            override fun onRoutesRequested(p0: MutableList<RequestPoint>) {
                TODO("onRoutesRequested Not yet implemented")
            }

            override fun onAlternativesRequested(p0: DrivingRoute) {
                TODO("onAlternativesRequested Not yet implemented")
            }

            override fun onUriResolvingRequested(p0: String) {
                TODO("onUriResolvingRequested Not yet implemented")
            }

            override fun onRoutesBuilt() {
                val routes = navigation.routes
                val fastestRoute = routes[0]
                // Выводим маршрут на карту
                mapObj.addPolyline(fastestRoute.geometry)
            }

            override fun onRoutesRequestError(error: Error) {
                // Обработка ошибки
            }

            override fun onResetRoutes() {}
        }

        // Подписываемся на события навигации
        navigation.addListener(navigationListener)

        // Запрашиваем маршрут
        navigation.requestRoutes(requestPoints, null, 3)
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
                        Point(55.751225, 37.62954),
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