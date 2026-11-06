package com.example.ui

import com.example.analyzer.TextMetricsResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Vienetiniai testai visoms duomenų ir būsenos klasėms:
 * - [TextMetricsResult]
 * - [HistoryRecord]
 * - [TextMetricsUiState]
 *
 * Užtikrina 100 % konstruktorių, metodų, savybių ir duomenų klasių funkcijų padengimą.
 */
class TextMetricsModelsTest {

    // =========================================================================
    // TextMetricsResult testai
    // =========================================================================

    @Test
    fun textMetricsResult_defaultValues_areZero() {
        // Arrange & Act
        val result = TextMetricsResult(
            sentencesCount = 1,
            wordsCount = 2,
            charactersCount = 10,
            numbersCount = 0
        )

        // Assert
        assertEquals(1, result.sentencesCount)
        assertEquals(2, result.wordsCount)
        assertEquals(10, result.charactersCount)
        assertEquals(0, result.numbersCount)
        assertEquals(0, result.charactersWithoutSpacesCount)
        assertEquals(0, result.punctuationCount)
    }

    @Test
    fun textMetricsResult_explicitValues_areRetained() {
        // Arrange & Act
        val result = TextMetricsResult(
            sentencesCount = 3,
            wordsCount = 15,
            charactersCount = 120,
            numbersCount = 4,
            charactersWithoutSpacesCount = 105,
            punctuationCount = 7
        )

        // Assert
        assertEquals(3, result.sentencesCount)
        assertEquals(15, result.wordsCount)
        assertEquals(120, result.charactersCount)
        assertEquals(4, result.numbersCount)
        assertEquals(105, result.charactersWithoutSpacesCount)
        assertEquals(7, result.punctuationCount)
    }

    @Test
    fun textMetricsResult_toSummaryString_matchesRequiredFormat() {
        // Arrange
        val result = TextMetricsResult(
            sentencesCount = 2,
            wordsCount = 4,
            charactersCount = 33,
            numbersCount = 1
        )

        // Act
        val summary = result.toSummaryString()

        // Assert
        assertEquals("2, 4, 33, 1", summary)
    }

    @Test
    fun textMetricsResult_dataClassMethods_behaveCorrectly() {
        // Arrange
        val r1 = TextMetricsResult(1, 2, 11, 0, 10, 0)
        val r2 = TextMetricsResult(1, 2, 11, 0, 10, 0)
        val r3 = r1.copy(wordsCount = 5)

        // Act & Assert (equals, hashCode, toString)
        assertEquals(r1, r2)
        assertEquals(r1.hashCode(), r2.hashCode())
        assertFalse(r1 == r3)
        assertTrue(r1.toString().contains("sentencesCount=1"))

        // Destructuring (component1..component6)
        val (s, w, c, n, cws, p) = r1
        assertEquals(1, s)
        assertEquals(2, w)
        assertEquals(11, c)
        assertEquals(0, n)
        assertEquals(10, cws)
        assertEquals(0, p)
    }

    // =========================================================================
    // HistoryRecord testai
    // =========================================================================

    @Test
    fun historyRecord_defaultValues_areGenerated() {
        // Arrange
        val result = TextMetricsResult(1, 2, 11, 0)
        val beforeTimestamp = System.currentTimeMillis()

        // Act
        val record = HistoryRecord(
            text = "Labas rytas",
            result = result
        )
        val afterTimestamp = System.currentTimeMillis()

        // Assert
        assertNotNull(record.id)
        assertTrue(record.id.isNotEmpty())
        assertEquals("Labas rytas", record.text)
        assertEquals(result, record.result)
        assertTrue(record.timestamp in beforeTimestamp..afterTimestamp)
    }

    @Test
    fun historyRecord_explicitValues_areRetained() {
        // Arrange
        val result = TextMetricsResult(2, 4, 33, 1)

        // Act
        val record = HistoryRecord(
            id = "custom-id-123",
            text = "Rugsėjo 1",
            result = result,
            timestamp = 1700000000000L
        )

        // Assert
        assertEquals("custom-id-123", record.id)
        assertEquals("Rugsėjo 1", record.text)
        assertEquals(result, record.result)
        assertEquals(1700000000000L, record.timestamp)
    }

    @Test
    fun historyRecord_dataClassMethods_behaveCorrectly() {
        // Arrange
        val result = TextMetricsResult(1, 2, 11, 0)
        val record1 = HistoryRecord("id-1", "Tekstas", result, 1000L)
        val record2 = HistoryRecord("id-1", "Tekstas", result, 1000L)
        val record3 = record1.copy(text = "Kitas tekstas")

        // Act & Assert
        assertEquals(record1, record2)
        assertEquals(record1.hashCode(), record2.hashCode())
        assertFalse(record1 == record3)
        assertTrue(record1.toString().contains("id-1"))

        // Destructuring (component1..component4)
        val (id, text, res, ts) = record1
        assertEquals("id-1", id)
        assertEquals("Tekstas", text)
        assertEquals(result, res)
        assertEquals(1000L, ts)
    }

    // =========================================================================
    // TextMetricsUiState testai
    // =========================================================================

    @Test
    fun textMetricsUiState_defaultValues_areExpected() {
        // Arrange & Act
        val state = TextMetricsUiState()

        // Assert
        assertEquals("", state.inputText)
        assertNull(state.currentResult)
        assertNull(state.errorMessage)
        assertTrue(state.history.isEmpty())
        assertFalse(state.isSampleDialogVisible)
    }

    @Test
    fun textMetricsUiState_dataClassMethods_behaveCorrectly() {
        // Arrange
        val result = TextMetricsResult(1, 2, 11, 0)
        val history = listOf(HistoryRecord("1", "A", result, 100L))
        val state1 = TextMetricsUiState("Labas", result, "Klaida", history, true)
        val state2 = TextMetricsUiState("Labas", result, "Klaida", history, true)
        val state3 = state1.copy(inputText = "Viso")

        // Act & Assert
        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
        assertFalse(state1 == state3)
        assertTrue(state1.toString().contains("Labas"))

        // Destructuring (component1..component5)
        val (txt, res, err, hist, dialog) = state1
        assertEquals("Labas", txt)
        assertEquals(result, res)
        assertEquals("Klaida", err)
        assertEquals(history, hist)
        assertTrue(dialog)
    }
}
