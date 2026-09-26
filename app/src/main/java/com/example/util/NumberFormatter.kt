package com.example.util

import java.util.Locale

object NumberFormatter {
  fun formatCoins(value: Double): String {
    return formatCompact(value)
  }

  fun formatGoo(value: Double): String {
    return formatCompact(value)
  }

  fun formatCompact(value: Double): String {
    if (value < 0.0) return "0"
    if (value < 1000.0) {
      return if (value % 1.0 == 0.0) {
        value.toLong().toString()
      } else {
        String.format(Locale.US, "%.1f", value)
      }
    }

    val suffixes = arrayOf("", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp")
    var num = value
    var exp = 0
    while (num >= 1000.0 && exp < suffixes.size - 1) {
      num /= 1000.0
      exp++
    }

    return String.format(Locale.US, "%.2f %s", num, suffixes[exp])
  }
}
