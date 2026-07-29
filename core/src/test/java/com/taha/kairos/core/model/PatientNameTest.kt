package com.taha.kairos.core.model

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class PatientNameTest {

    @Test
    fun capitalizesEverySpaceSeparatedWord() {
        assertEquals(
            "John Michael Doe",
            "john michael doe".toCapitalizedPatientName(Locale.ENGLISH),
        )
    }

    @Test
    fun capitalizesWordsAfterHyphensAndApostrophes() {
        assertEquals(
            "Jean-Luc O'Connor D’Angelo",
            "jean-luc o'connor d’angelo".toCapitalizedPatientName(Locale.ENGLISH),
        )
    }

    @Test
    fun preservesExistingLetterCaseAndSpacing() {
        assertEquals(
            "  John  McDONALD ",
            "  john  McDONALD ".toCapitalizedPatientName(Locale.ENGLISH),
        )
    }

    @Test
    fun supportsUnicodeLettersAndCombiningMarks() {
        assertEquals(
            "Élise E\u0301mile",
            "élise e\u0301mile".toCapitalizedPatientName(Locale.ENGLISH),
        )
    }
}

