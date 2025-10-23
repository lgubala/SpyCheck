package com.example.spycheck.ui.main.demos.wifi

data class LocationResult(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double? = null,
    val provider: ApiProvider
)