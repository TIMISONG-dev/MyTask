package timisongdev.mytasks

import android.os.CountDownTimer
import androidx.compose.runtime.remember
import okhttp3.OkHttpClient
import kotlinx.coroutines.*
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.*

class Working {
    // Mechanism for courier working

    companion object {
        var timerDuration = 300_000L
        val orderMode = "null"
        val taskMode = "null"
        val countOrders = 0
        val bonus = 1
        val status = 6

        var order: Pair<Double, Double>? = null

        private suspend fun getCoordinatesFromAddress(address: String, apiKey: String): Pair<Double, Double>? {
            return withContext(Dispatchers.IO) {
                val client = OkHttpClient()

                val url = "https://geocode-maps.yandex.ru/1.x/?geocode=${address.replace(" ", "+")}&format=json&apikey=$apiKey"

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")

                    val featureMember = jsonResponse
                        .getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")

                    if (featureMember.length() > 0) {
                        val geoObject = featureMember.getJSONObject(0).getJSONObject("GeoObject")

                        val pos = geoObject.getJSONObject("Point").getString("pos").split(" ")
                        val lat = pos[1].toDouble()
                        val lon = pos[0].toDouble()

                        return@withContext Pair(lat, lon)
                    }
                }

                return@withContext null
            }
        }

        private fun parseCoordinates(input: String): Pair<Double, Double>? {
            return try {
                val parts = input.split(",").map { it.trim() }
                if (parts.size == 2) {
                    val lat = parts[0].toDouble()
                    val lon = parts[1].toDouble()
                    Pair(lat, lon)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        suspend fun compareLocations(workerLocation: String, orderLocation: String, apiKey: String): Boolean {
            val workerCoordinates = parseCoordinates(workerLocation) ?: getCoordinatesFromAddress(workerLocation, apiKey)
            val orderCoordinates = parseCoordinates(orderLocation) ?: getCoordinatesFromAddress(orderLocation, apiKey)

            if (workerCoordinates == null || orderCoordinates == null) {
                return false
            }

            order = orderCoordinates

            val (workerLatitude, workerLongitude) = workerCoordinates
            val (orderLatitude, orderLongitude) = orderCoordinates

            val distance = calculateDistance(workerLatitude, workerLongitude, orderLatitude, orderLongitude)

            // Если расстояние больше 2 км, вернуть false
            return distance <= 2
        }

        private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371.0 // Радиус Земли в километрах
            val lat1Rad = Math.toRadians(lat1)
            val lon1Rad = Math.toRadians(lon1)
            val lat2Rad = Math.toRadians(lat2)
            val lon2Rad = Math.toRadians(lon2)

            val deltaLat = lat2Rad - lat1Rad
            val deltaLon = lon2Rad - lon1Rad

            val a = sin(deltaLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return R * c
        }

        fun startTimer(durationInMillis: Long, onTick: (Long) -> Unit) {
            object : CountDownTimer(durationInMillis, 1_000) {
                override fun onTick(millisUntilFinished: Long) {
                    onTick(millisUntilFinished)
                }

                override fun onFinish() {
                    onTick(0L)
                }
            }.start()
        }
    }
}