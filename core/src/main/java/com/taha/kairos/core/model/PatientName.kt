package com.taha.kairos.core.model

import java.util.Locale

/**
 * Capitalizes the first letter of each name word while preserving the
 * remaining letters exactly as entered.
 *
 * Punctuation such as spaces, hyphens, and apostrophes starts a new word, so
 * names such as "jean-luc o'connor" become "Jean-Luc O'Connor".
 */
fun String.toCapitalizedPatientName(
    locale: Locale = Locale.getDefault(),
): String {
    if (isEmpty()) return this

    val result = StringBuilder(length)
    var startsWord = true
    var offset = 0
    while (offset < length) {
        val codePoint = codePointAt(offset)
        val character = String(Character.toChars(codePoint))
        when {
            Character.isLetter(codePoint) -> {
                result.append(if (startsWord) character.uppercase(locale) else character)
                startsWord = false
            }

            Character.isDigit(codePoint) -> {
                result.append(character)
                startsWord = false
            }

            Character.getType(codePoint) in COMBINING_MARK_TYPES -> {
                result.append(character)
            }

            else -> {
                result.append(character)
                startsWord = true
            }
        }
        offset += Character.charCount(codePoint)
    }
    return result.toString()
}

private val COMBINING_MARK_TYPES = setOf(
    Character.NON_SPACING_MARK.toInt(),
    Character.COMBINING_SPACING_MARK.toInt(),
    Character.ENCLOSING_MARK.toInt(),
)

