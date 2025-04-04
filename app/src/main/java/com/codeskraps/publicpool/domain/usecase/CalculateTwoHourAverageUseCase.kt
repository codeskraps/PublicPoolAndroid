package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.model.ChartDataPoint

class CalculateTwoHourAverageUseCase { // Allow Koin/Hilt injection

    // Define the window size (2 hours = 12 * 10 minutes)
    private val windowSize = 12

    operator fun invoke(tenMinuteData: List<ChartDataPoint>): List<ChartDataPoint> {
        if (tenMinuteData.size < windowSize) {
            // Not enough data to calculate a full 2-hour average
            return emptyList()
        }

        // Ensure data is sorted by timestamp, although API seems to provide it sorted
        val sortedData = tenMinuteData.sortedBy { it.timestamp }

        val twoHourAverageData = mutableListOf<ChartDataPoint>()

        // Calculate the rolling average
        for (i in (windowSize - 1) until sortedData.size) {
            // Get the window of points (current point + previous 11)
            val window = sortedData.subList(i - windowSize + 1, i + 1)

            // Calculate the average hash rate for the window
            val averageHashRate = window.map { it.hashRate }.average()

            // Use the timestamp of the *last* point in the window for the averaged point
            val timestamp = sortedData[i].timestamp

            if (!averageHashRate.isNaN()) { // Avoid adding NaN if window is empty (shouldn't happen here)
                 twoHourAverageData.add(
                    ChartDataPoint(
                        timestamp = timestamp,
                        hashRate = averageHashRate
                    )
                )
            }
        }

        return twoHourAverageData
    }
} 