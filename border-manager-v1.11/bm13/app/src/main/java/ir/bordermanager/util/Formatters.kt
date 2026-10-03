package ir.bordermanager.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    private val nf = NumberFormat.getNumberInstance(Locale.US)
    fun number(value: Long): String = nf.format(value)
    fun number(value: Int): String = nf.format(value)
    fun weightKg(value: Double): String = if (value % 1000.0 == 0.0) "${number((value / 1000).toLong())} تن" else "${nf.format(value)} کیلو"
    fun toman(value: Long): String = "${number(value)} تومان"
    /** Kept as an alias so older callers do not silently change their stored data. */
    fun rial(value: Long): String = toman(value)
}
