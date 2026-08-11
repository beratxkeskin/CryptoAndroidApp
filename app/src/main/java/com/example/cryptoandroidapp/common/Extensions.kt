package com.example.cryptoandroidapp.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Kripto para miktarını kullanıcı dostu olarak biçimlendirir.
 * Tam sayılar için (Örn: 10.0 -> "10 ETH") küsürat gösterilmez.
 * Ondalıklı sayılar için (Örn: 0.25 -> "0.25 ETH", 0.0042 -> "0.0042 BTC") sadece gerektiği kadar basamak gösterilir.
 */
fun Double.formatCryptoAmount(symbol: String): String {
    val uppercaseSymbol = symbol.uppercase(Locale.ROOT)
    return if (this % 1.0 == 0.0) {
        String.format(Locale.US, "%,.0f %s", this, uppercaseSymbol)
    } else {
        val df = DecimalFormat("#,##0.####", DecimalFormatSymbols(Locale.US))
        "${df.format(this)} $uppercaseSymbol"
    }
}
