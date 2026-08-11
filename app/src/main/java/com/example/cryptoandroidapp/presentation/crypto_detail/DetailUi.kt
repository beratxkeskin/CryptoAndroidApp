package com.example.cryptoandroidapp.presentation.crypto_detail

import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

internal val DetailBackground = Color(0xFF030812)
internal val DetailBorder = Color(0xFF202943)
internal val DetailText = Color(0xFFF5F4FF)
internal val DetailMuted = Color(0xFFA9A9C0)
internal val DetailPurple = Color(0xFF914CFF)
internal val DetailGreen = Color(0xFF00D87D)
internal val DetailRed = Color(0xFFFF5C7A)

internal fun formatUsd(value: Double): String = NumberFormat.getCurrencyInstance(Locale.US).apply {
    maximumFractionDigits = if (abs(value) < 1) 4 else 2
}.format(value)

internal fun formatUsdChange(price: Double, percentage: Double): String =
    formatUsd(price * percentage / (100 + percentage))

internal fun compactUsd(value: Double): String = when {
    value >= 1_000_000_000_000 -> String.format(Locale.US, "$%.2f T", value / 1_000_000_000_000)
    value >= 1_000_000_000 -> String.format(Locale.US, "$%.2f B", value / 1_000_000_000)
    value >= 1_000_000 -> String.format(Locale.US, "$%.2f M", value / 1_000_000)
    else -> formatUsd(value)
}

internal fun compactNumber(value: Double): String = when {
    value >= 1_000_000_000_000 -> String.format(Locale.US, "%.2f T", value / 1_000_000_000_000)
    value >= 1_000_000_000 -> String.format(Locale.US, "%.2f B", value / 1_000_000_000)
    value >= 1_000_000 -> String.format(Locale.US, "%.2f M", value / 1_000_000)
    value >= 1_000 -> String.format(Locale.US, "%.2f K", value / 1_000)
    else -> String.format(Locale.US, "%.2f", value)
}

internal fun formatChartPrice(value: Double): String {
    val fractionDigits = when {
        value == 0.0 -> 2
        abs(value) < 0.01 -> 6
        abs(value) < 1 -> 4
        else -> 2
    }
    return NumberFormat.getCurrencyInstance(Locale.US).apply {
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }.format(value)
}
