package com.example.wearable

import com.example.data.model.WearableMetrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface WearableAdapter {
    val deviceName: String
    suspend fun connect(): Boolean
    suspend fun disconnect()
    suspend fun fetchLatestMetrics(): WearableMetrics
}

class MockWearableAdapter : WearableAdapter {
    override val deviceName: String = "Amazfit Band 7"
    private var isConnected = true

    override suspend fun connect(): Boolean {
        isConnected = true
        return true
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun fetchLatestMetrics(): WearableMetrics {
        val randomStepIncrement = (5..35).random()
        val randomHr = (70..78).random()
        return WearableMetrics(
            id = "current",
            isConnected = isConnected,
            deviceName = deviceName,
            heartRate = randomHr,
            sleepDurationMinutes = 380, // 6h 20m
            sleepScore = 85,
            steps = 3482 + randomStepIncrement,
            stressScore = 38,
            activeMinutes = 32,
            lastSyncedTimestamp = System.currentTimeMillis()
        )
    }
}

/**
 * AmazfitAdapter: Architecture boundary adapter for Zepp OS / Amazfit Bluetooth LE sync.
 * Adheres to standard mobile health sync protocols without modifying device firmware.
 */
class AmazfitAdapter(private val fallbackAdapter: WearableAdapter = MockWearableAdapter()) : WearableAdapter {
    override val deviceName: String = "Amazfit Band 7 (Zepp Bridge)"

    override suspend fun connect(): Boolean {
        // Future production integration through Zepp Open Platform / BLE GATT Health profiles
        return fallbackAdapter.connect()
    }

    override suspend fun disconnect() {
        fallbackAdapter.disconnect()
    }

    override suspend fun fetchLatestMetrics(): WearableMetrics {
        return fallbackAdapter.fetchLatestMetrics()
    }
}

class WearableService(private val adapter: WearableAdapter = MockWearableAdapter()) {
    private val _connectionStatus = MutableStateFlow(true)
    val connectionStatus = _connectionStatus.asStateFlow()

    suspend fun syncData(): WearableMetrics {
        val metrics = adapter.fetchLatestMetrics()
        _connectionStatus.value = metrics.isConnected
        return metrics
    }

    suspend fun toggleConnection(): Boolean {
        val newStatus = !_connectionStatus.value
        if (newStatus) {
            adapter.connect()
        } else {
            adapter.disconnect()
        }
        _connectionStatus.value = newStatus
        return newStatus
    }
}
