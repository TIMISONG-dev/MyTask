package timisongdev.mytasks

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import coil.compose.rememberImagePainter
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timisongdev.mytasks.ui.theme.MyTasksTheme

class Workspace : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(App.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

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
    val showMap = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    var steps = 0
    var starts = 3
    val donate = 500
    val pay = 1000
    val currency = "$"

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
                        if (index == 4) {
                            scope.launch {
                                delay(300)
                                showMap.value = true
                            }
                        } else {
                            showMap.value = false
                        }
                    } else {
                        cells.intValue = 2
                        openCell.intValue = -1
                        showMap.value = false
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
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
            )
            if (index != 4 && index != 6) {
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
                                0 -> pay.toString()
                                1 -> donate.toString()
                                2 -> starts.toString()
                                3 -> steps.toString()
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
            } else {
                if (index == 4) {
                    if (showMap.value){
                        AndroidView(
                            factory = { context ->
                                MapView(context).apply {
                                    map.isRotateGesturesEnabled = true
                                    showUserLocation()
                                }
                            },
                            Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(24.dp))
                        )
                    } else {
                        val mapUrl = "https://static-maps.yandex.ru/1.x/?ll=37.620070,55.753630&size=450,450&z=10&l=map"
                        Image(
                            painter = rememberImagePainter(data = mapUrl),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(24.dp))
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

fun MapView.showUserLocation() {
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
