package com.example.spycheck.ui.main.demos.sneaky.wifi.utils

import com.example.spycheck.ui.main.demos.sneaky.wifi.ApiProvider
import com.example.spycheck.ui.main.demos.sneaky.wifi.LocationResult
import com.example.spycheck.ui.main.demos.sneaky.wifi.utils.WifiNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiLocationService {

    suspend fun locateViaApi(
        provider: ApiProvider,
        networks: List<WifiNetwork>,
        apiKey: String = ""
    ): Result<LocationResult> = withContext(Dispatchers.IO) {
        try {
            when (provider) {
                ApiProvider.GOOGLE -> locateViaGoogle(networks, apiKey)
                ApiProvider.MOZILLA -> locateViaMozilla(networks)
                ApiProvider.UNWIRED -> locateViaUnwired(networks, apiKey)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun locateViaGoogle(networks: List<WifiNetwork>, apiKey: String): Result<LocationResult> {
        if (apiKey.isEmpty()) {
            return Result.failure(Exception("Google API requires an API key"))
        }

        val url = URL("https://www.googleapis.com/geolocation/v1/geolocate?key=$apiKey")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonRequest = buildGoogleRequest(networks)

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonRequest)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                return parseGoogleResponse(response)
            } else {
                val errorResponse = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                return Result.failure(Exception("Google API error: $responseCode - $errorResponse"))
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun locateViaMozilla(networks: List<WifiNetwork>): Result<LocationResult> {
        val url = URL("https://location.services.mozilla.com/v1/geolocate?key=test")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonRequest = buildMozillaRequest(networks)

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonRequest)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                return parseMozillaResponse(response)
            } else {
                val errorResponse = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                return Result.failure(Exception("Mozilla API error: $responseCode - $errorResponse"))
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun locateViaUnwired(networks: List<WifiNetwork>, apiKey: String): Result<LocationResult> {
        if (apiKey.isEmpty()) {
            return Result.failure(Exception("Unwired Labs API requires an API key"))
        }

        val url = URL("https://us1.unwiredlabs.com/v2/process.php")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonRequest = buildUnwiredRequest(networks, apiKey)

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonRequest)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                return parseUnwiredResponse(response)
            } else {
                val errorResponse = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                return Result.failure(Exception("Unwired Labs API error: $responseCode - $errorResponse"))
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun buildGoogleRequest(networks: List<WifiNetwork>): String {
        val json = JSONObject()
        json.put("considerIp", false)

        val wifiArray = JSONArray()
        networks.take(10).forEach { network ->
            val wifiObj = JSONObject()
            wifiObj.put("macAddress", network.bssid)
            wifiObj.put("signalStrength", network.signalStrength)
            wifiObj.put("signalToNoiseRatio", 0)
            wifiArray.put(wifiObj)
        }

        json.put("wifiAccessPoints", wifiArray)
        return json.toString()
    }

    private fun buildMozillaRequest(networks: List<WifiNetwork>): String {
        val json = JSONObject()

        val wifiArray = JSONArray()
        networks.take(10).forEach { network ->
            val wifiObj = JSONObject()
            wifiObj.put("macAddress", network.bssid)
            wifiObj.put("signalStrength", network.signalStrength)
            wifiArray.put(wifiObj)
        }

        json.put("wifiAccessPoints", wifiArray)
        return json.toString()
    }

    private fun buildUnwiredRequest(networks: List<WifiNetwork>, apiKey: String): String {
        val json = JSONObject()
        json.put("token", apiKey)

        val wifiArray = JSONArray()
        networks.take(10).forEach { network ->
            val wifiObj = JSONObject()
            wifiObj.put("bssid", network.bssid)
            wifiObj.put("signal", network.signalStrength)
            wifiArray.put(wifiObj)
        }

        json.put("wifi", wifiArray)
        return json.toString()
    }

    private fun parseGoogleResponse(response: String): Result<LocationResult> {
        val json = JSONObject(response)
        val location = json.getJSONObject("location")
        val lat = location.getDouble("lat")
        val lng = location.getDouble("lng")
        val accuracy = json.optDouble("accuracy", 0.0)

        return Result.success(
            LocationResult(
                latitude = lat,
                longitude = lng,
                accuracy = if (accuracy > 0) accuracy else null,
                provider = ApiProvider.GOOGLE
            )
        )
    }

    private fun parseMozillaResponse(response: String): Result<LocationResult> {
        val json = JSONObject(response)
        val location = json.getJSONObject("location")
        val lat = location.getDouble("lat")
        val lng = location.getDouble("lng")
        val accuracy = json.optDouble("accuracy", 0.0)

        return Result.success(
            LocationResult(
                latitude = lat,
                longitude = lng,
                accuracy = if (accuracy > 0) accuracy else null,
                provider = ApiProvider.MOZILLA
            )
        )
    }

    private fun parseUnwiredResponse(response: String): Result<LocationResult> {
        val json = JSONObject(response)

        if (json.has("status") && json.getString("status") == "error") {
            val message = json.optString("message", "Unknown error")
            return Result.failure(Exception(message))
        }

        val lat = json.getDouble("lat")
        val lon = json.getDouble("lon")
        val accuracy = json.optDouble("accuracy", 0.0)

        return Result.success(
            LocationResult(
                latitude = lat,
                longitude = lon,
                accuracy = if (accuracy > 0) accuracy else null,
                provider = ApiProvider.UNWIRED
            )
        )
    }
}