package com.sarfrazqureshi.clipvault.util

import com.sarfrazqureshi.clipvault.db.ClipType

/**
 * Decides which folder a piece of copied text belongs to:
 * NUMBER, LINK, ADULT (locked), or plain TEXT.
 */
object ClipClassifier {

    private val phoneRegex = Regex("^\\+?[0-9][0-9\\-\\s()]{6,16}[0-9]$")
    private val urlRegex = Regex("(https?://|www\\.)\\S+", RegexOption.IGNORE_CASE)

    // Generic keyword list used only to flag adult content into the locked folder.
    // Edit this list to add/remove keywords as you like.
    private val adultKeywords = listOf(
        "porn", "xxx", "xvideos", "xnxx", "adult", "18+", "nsfw",
        "onlyfans", "hentai", "escort", "cam4", "pornhub"
    )

    fun classify(raw: String): ClipType {
        val text = raw.trim()
        if (text.isEmpty()) return ClipType.TEXT

        val isLink = urlRegex.containsMatchIn(text)

        return when {
            isLink && adultKeywords.any { text.contains(it, ignoreCase = true) } -> ClipType.ADULT
            isLink -> ClipType.LINK
            phoneRegex.matches(text.replace(" ", "").replace("-", "")) -> ClipType.NUMBER
            else -> ClipType.TEXT
        }
    }
}
