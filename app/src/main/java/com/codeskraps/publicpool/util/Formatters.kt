package com.codeskraps.publicpool.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.* // Required for Locale
import kotlin.math.pow
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Suffixes for large numbers (SI prefixes)
private val suffixes = charArrayOf(' ', 'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y')

/**
 * Formats a large double value into a more readable string with SI prefixes (k, M, G, T, etc.).
 * Handles NaN, Infinity, and Zero gracefully.
 * Uses appropriate precision.
 */
fun formatLargeNumber(number: Double, precision: Int = 2): String {
    if (number.isNaN() || number.isInfinite() || number == 0.0) {
        return "0"
    }

    // Use BigDecimal for accurate length calculation, especially for scientific notation
    val numString = number.toBigDecimal().toPlainString()
    val integerChars = numString.takeWhile { it != '.' }.length

    // Determine the correct tier (0 for < 1000, 1 for k, 2 for M, etc.)
    val tier = ((integerChars - 1).coerceAtLeast(0)) / 3

    if (tier == 0) {
        // For numbers less than 1000, format with commas and precision
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.maximumFractionDigits = precision
        format.minimumFractionDigits = 0 // Don't force trailing zeros if not needed
        format.roundingMode = RoundingMode.FLOOR // Or RoundingMode.HALF_UP
        return format.format(number)
    }

    // Ensure tier doesn't exceed available suffixes
    val safeTier = tier.coerceAtMost(suffixes.size - 1)
    val suffix = suffixes[safeTier]
    val scale = 1000.0.pow(safeTier) // Use 1000 for SI prefixes
    val scaled = number / scale

    val format = NumberFormat.getNumberInstance(Locale.US)
    format.maximumFractionDigits = precision
    format.minimumFractionDigits = 0
    format.roundingMode = RoundingMode.FLOOR // Or RoundingMode.HALF_UP
    val formattedScaled = format.format(scaled)

    // Add space only if suffix is not empty (i.e., not tier 0)
    return "$formattedScaled${if (suffix != ' ') " $suffix" else ""}"
}

/**
 * Formats a hash rate (in H/s) into a readable string with appropriate SI units (KH/s, MH/s, etc.).
 * Handles NaN, Infinity, and Zero gracefully.
 */
fun formatHashRate(hashps: Double, precision: Int = 2): String {
    if (hashps.isNaN() || hashps.isInfinite() || hashps == 0.0) {
        return "0 H/s"
    }

    val units = listOf("H/s", "KH/s", "MH/s", "GH/s", "TH/s", "PH/s", "EH/s", "ZH/s", "YH/s")
    var tier = 0
    var scaledHashps = hashps

    // Divide by 1000 until the number is manageable or we run out of units
    while (scaledHashps >= 1000.0 && tier < units.size - 1) {
        scaledHashps /= 1000.0
        tier++
    }

    val format = NumberFormat.getNumberInstance(Locale.US)
    format.maximumFractionDigits = precision
    format.minimumFractionDigits = 0
    format.roundingMode = RoundingMode.FLOOR // Or RoundingMode.HALF_UP
    val formattedScaled = format.format(scaledHashps)

    return "$formattedScaled ${units[tier]}"
}

/**
 * Formats a BigDecimal value with specified precision, returning "0" if null.
 */
fun formatBigDecimal(value: BigDecimal?, precision: Int = 2): String {
    if (value == null) return "0"
    val format = NumberFormat.getNumberInstance(Locale.US)
    format.maximumFractionDigits = precision
    format.minimumFractionDigits = 0
    format.roundingMode = RoundingMode.FLOOR
    return format.format(value)
}

// --- Added from ui/utils ---

fun formatDifficulty(difficulty: Double?): String {
    if (difficulty == null || difficulty == 0.0) return "0.0"
    // Using the logic similar to formatLargeNumber but specific for difficulty formatting
    // Assuming k, M, G, T are appropriate suffixes for difficulty
    val numString = difficulty.toBigDecimal().toPlainString()
    val integerChars = numString.takeWhile { it != '.' }.length
    val tier = ((integerChars - 1).coerceAtLeast(0)) / 3

    if (tier == 0) {
        // Format with precision, no suffix
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.maximumFractionDigits = 2 // Adjust precision as needed
        format.minimumFractionDigits = 0
        format.roundingMode = RoundingMode.FLOOR
        return format.format(difficulty)
    }

    val safeTier = tier.coerceAtMost(suffixes.size - 1) // Use existing suffixes array
    val suffix = suffixes[safeTier]
    val scale = 1000.0.pow(safeTier)
    val scaled = difficulty / scale

    val format = NumberFormat.getNumberInstance(Locale.US)
    format.maximumFractionDigits = 2 // Adjust precision as needed
    format.minimumFractionDigits = 0
    format.roundingMode = RoundingMode.FLOOR
    val formattedScaled = format.format(scaled)

    return "$formattedScaled${if (suffix != ' ') "$suffix" else ""}" // No space before suffix based on sample (4.37k)
}

// Basic relative time formatter - enhance with a library like ThreeTenABP or java.time if needed
fun formatRelativeTime(isoDateTimeString: String?): String {
    if (isoDateTimeString == null) return "N/A"
    return try {
        val dateTime = OffsetDateTime.parse(isoDateTimeString)
        val now = OffsetDateTime.now(dateTime.offset) // Compare using the same offset
        val duration = Duration.between(dateTime, now)

        when {
            duration.seconds < 60 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} min ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE) // Fallback to date
        }
    } catch (e: DateTimeParseException) {
        "Invalid date" // Handle parsing error
    }
}

fun calculateUptime(isoDateTimeString: String?): String {
     if (isoDateTimeString == null) return "N/A"
    return try {
        val startTime = OffsetDateTime.parse(isoDateTimeString)
        val now = OffsetDateTime.now(startTime.offset)
        val duration = Duration.between(startTime, now)

        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        // val seconds = duration.seconds % 60

        buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0 || days > 0) append("${hours}h ") // Show hours if days > 0
            append("${minutes}m") // Always show minutes
           // append("${seconds}s") // Optionally add seconds
        }

    } catch (e: DateTimeParseException) {
        "Invalid date" // Handle parsing error
    }
} 