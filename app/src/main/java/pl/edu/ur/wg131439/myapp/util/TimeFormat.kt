package pl.edu.ur.wg131439.myapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormat {
    private val dt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl", "PL"))
    private val t = SimpleDateFormat("HH:mm", Locale("pl", "PL"))

    fun formatDateTime(epochMs: Long): String = dt.format(Date(epochMs))
    fun formatTime(epochMs: Long): String = t.format(Date(epochMs))
}
