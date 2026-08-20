package com.example.cryptoandroidapp.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Kripto para miktarını biçimlendirir.
 * Tam sayılar için (Örn: 10.0 -> "10 ETH") küsürat gösterilmez.
 * Ondalıklı sayılar için (Örn: 0.25 -> "0.25 ETH", 0.0042 -> "0.0042 BTC") basamaklar gösterilir.
 */
fun Double.formatCryptoAmount(symbol: String): String {
    val uppercaseSymbol = symbol.uppercase(Locale.ROOT) //Locale.ROOT, sembolün cihaz dilinden etkilenmemesini sağlar
    return if (this % 1.0 == 0.0) {
        String.format(Locale.US, "%,.0f %s", this, uppercaseSymbol)
    } else {
        val df = DecimalFormat("#,##0.####", DecimalFormatSymbols(Locale.US))
        "${df.format(this)} $uppercaseSymbol"
    }
}

/**
 * Büyük finansal rakamları compact (Trilyon, Milyar, Milyon, Bin) formatta gösterir ($1.23B, $45.60M vb.)
 */
fun Double.toCompactUsd(): String {
    return when {
        this >= 1.0e12 -> String.format(Locale.US, "$%,.2fT", this / 1.0e12)
        this >= 1.0e9 -> String.format(Locale.US, "$%,.2fB", this / 1.0e9)
        this >= 1.0e6 -> String.format(Locale.US, "$%,.2fM", this / 1.0e6)
        this >= 1.0e3 -> String.format(Locale.US, "$%,.1fK", this / 1.0e3)
        else -> String.format(Locale.US, "$%,.2f", this)
    }
}

/**
 * Dolar fiyatını akıllı (dinamik) olarak biçimlendirir:
 * - $1.00 ve üzeri coinler için: Standart 2 basamak ($63,400.40, $1.00)
 * - $0.10 - $0.99 arası coinler için: 4 basamak ($0.3845)
 * - $0.0001 - $0.0999 arası coinler için: 6 basamak ($0.001234)
 * - PEPE, SHIB, BONK gibi micro meme coinler için ($0.00000845): Anlamlı basamakları korur ($0.00000845)
 */
fun Double.toFormattedPrice(): String {
    if (this == 0.0) return "$0.00"
    val absValue = kotlin.math.abs(this)
    return when {
        absValue >= 1.0 -> String.format(Locale.US, "$%,.2f", this)
        absValue >= 0.1 -> String.format(Locale.US, "$%.4f", this)
        absValue >= 0.0001 -> String.format(Locale.US, "$%.6f", this)
        else -> {
            val df = DecimalFormat("$0.00000000", DecimalFormatSymbols(Locale.US))
            val raw = df.format(this)
            val trimmed = raw.dropLastWhile { it == '0' }
            if (trimmed.endsWith('.')) "${trimmed}00" else if (trimmed.length - trimmed.indexOf('.') <= 2) "${trimmed}0" else trimmed
        }
    }
}

/**
 * Yüzdelik değişimi biçimlendirir (Örn: 2.35 -> "%+.2f%")
 */
fun Double.toFormattedPercent(): String {
    return String.format(Locale.US, "%+.2f%%", this)
}
