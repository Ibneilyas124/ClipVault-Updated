package com.sarfrazqureshi.clipvault.util

import com.sarfrazqureshi.clipvault.db.ClipType

/**
 * Decides which folder a piece of copied text belongs to.
 * Rule: pure digits/symbols (no letters, any language) => NUMBER.
 * Digits mixed with letters => TEXT. Recognized web links => LINK/ADULT.
 */
object ClipClassifier {

    // Requires a real domain shape (word.word) so plain paragraphs
    // containing "www" or stray dots don't get misdetected as links.
    private val urlRegex = Regex(
        "(^|\\s)(https?://\\S+|www\\.[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}\\S*)",
        RegexOption.IGNORE_CASE
    )

    private val adultKeywords = listOf(
        "porn", "xxx", "xvideos", "xnxx", "adult", "18+", "nsfw",
        "onlyfans", "hentai", "escort", "cam4", "pornhub"
    )

    fun classify(raw: String): ClipType {
        val text = raw.trim()
        if (text.isEmpty()) return ClipType.TEXT

        if (urlRegex.containsMatchIn(text)) {
            return if (adultKeywords.any { text.contains(it, ignoreCase = true) })
                ClipType.ADULT
            else
                ClipType.LINK
        }

        val hasDigit = text.any { it.isDigit() }
        val hasLetter = text.any { it.isLetter() }

        return if (hasDigit && !hasLetter) ClipType.NUMBER else ClipType.TEXT
    }
}
