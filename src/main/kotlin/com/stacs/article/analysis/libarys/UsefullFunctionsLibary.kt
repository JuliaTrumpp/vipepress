package com.stacs.article.analysis.libarys

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class UsefullFunctionsLibary {  // todo rename useful...

    fun doubleToIsoString(timestamp: Double?, withTime: Boolean): String {
        if (timestamp != 0.0 || timestamp != null) {
            val instant = timestamp?.let { java.time.Instant.ofEpochSecond(it.toLong()) }
            if (withTime) {
                java.time.format.DateTimeFormatter.ISO_INSTANT.format(instant)
            } else {
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.format(instant?.atZone(java.time.ZoneOffset.UTC))
            }
        }
        return "Unknown"
    }

    fun limitStingLength(text: String?, MAX_LENGTH: Int = 350): String {
        return if (text?.length ?: 0 > MAX_LENGTH) {
            text?.take(MAX_LENGTH) + "..."
        } else {
            text ?: "Not available"
        }
    }

    fun textContainsUrl(text: String): Boolean {
        val regex = """\bhttps?://\S+""".toRegex()  // Regex for a URL

        return text.contains(regex) == false
    }

    fun removeAfterLastDash(input: String): String {
        val lastDashIndex = input.lastIndexOf(" -")
        return if (lastDashIndex != -1) {
            input.substring(0, lastDashIndex).trim()
        } else {
            input.trim()
        }
    }

    fun parsePublishedDate(publishedAt: String): LocalDate {
        return try {
            // When ISO 8601-Timestamp
            Instant.parse(publishedAt).atOffset(ZoneOffset.UTC).toLocalDate()
        } catch (e: Exception) {
            try {
                // When UNIX-Timestamp
                Instant.ofEpochSecond(publishedAt.toLong()).atOffset(ZoneOffset.UTC).toLocalDate()
            } catch (e: Exception) {
                // Fallback: current Date
                LocalDate.now(ZoneOffset.UTC)
            }
        }
    }

    fun getCurrentDateString() : String{
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun parseUtcToDate(utcTimestamp: Double?): LocalDate {
        if(utcTimestamp == null){return LocalDate.now()}        // todo do differently
        val epochMilli = (utcTimestamp * 1000).toLong()
        return Instant.ofEpochMilli(epochMilli)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
    }
}