package com.example

import com.example.analyzer.TextMetricsAnalyzer
import com.example.ui.TextMetricsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Vienetiniai testai teksto metrikų skaičiavimo algoritmams ir validacijai.
 */
class ExampleUnitTest {

    @Test
    fun testAssignmentExample1_LabasRytas() {
        val text = "Labas rytas"
        val result = TextMetricsAnalyzer.analyze(text)

        assertEquals("Sakinių skaičius turi būti 1", 1, result.sentencesCount)
        assertEquals("Žodžių skaičius turi būti 2", 2, result.wordsCount)
        assertEquals("Rašybos ženklų (simbolių su tarpais) skaičius turi būti 11", 11, result.charactersCount)
        assertEquals("Skaičių skaičius turi būti 0", 0, result.numbersCount)
        assertEquals("Santrauka turi atitikti formatą", "1, 2, 11, 0", result.toSummaryString())
    }

    @Test
    fun testAssignmentExample2_Rugsejo1() {
        val text = "Rugsėjo 1 prasidėjo karas. Liūdna"
        val result = TextMetricsAnalyzer.analyze(text)

        assertEquals("Sakinių skaičius turi būti 2", 2, result.sentencesCount)
        assertEquals("Žodžių skaičius turi būti 4", 4, result.wordsCount)
        assertEquals("Rašybos ženklų (simbolių su tarpais) skaičius turi būti 33", 33, result.charactersCount)
        assertEquals("Skaičių skaičius turi būti 1", 1, result.numbersCount)
        assertEquals("Santrauka turi atitikti formatą", "2, 4, 33, 1", result.toSummaryString())
    }

    @Test
    fun testMultipleSentencesAndNumbers() {
        val text = "Vilnius įkurtas 1323 metais, o Kaunas minimas nuo 1361 m. Tai du didieji Lietuvos miestai!"
        val result = TextMetricsAnalyzer.analyze(text)

        assertEquals(2, result.sentencesCount)
        assertEquals(2, result.numbersCount)
    }

    @Test
    fun testEmptyAndWhitespaceInput() {
        val emptyResult = TextMetricsAnalyzer.analyze("")
        assertEquals(0, emptyResult.sentencesCount)
        assertEquals(0, emptyResult.wordsCount)
        assertEquals(0, emptyResult.charactersCount)
        assertEquals(0, emptyResult.numbersCount)

        val whitespaceResult = TextMetricsAnalyzer.analyze("   \n\t  ")
        assertEquals(0, whitespaceResult.sentencesCount)
        assertEquals(0, whitespaceResult.wordsCount)
        assertEquals(7, whitespaceResult.charactersCount)
        assertEquals(0, whitespaceResult.numbersCount)
    }

    @Test
    fun testViewModelValidationAndHistory() {
        val viewModel = TextMetricsViewModel()

        // 1. Tuščia įvestis turi sukelti klaidą
        viewModel.onTextChanged("   ")
        viewModel.calculateMetrics()
        assertNotNull("Turi būti nustatytas klaidos pranešimas esant tuščiam tekstui", viewModel.uiState.value.errorMessage)
        assertNull("Rezultatas neturi būti apskaičiuotas", viewModel.uiState.value.currentResult)
        assertEquals(0, viewModel.uiState.value.history.size)

        // 2. Teisingas tekstas turi apskaičiuoti metrikas ir įrašyti į istoriją
        viewModel.onTextChanged("Labas rytas")
        viewModel.calculateMetrics()
        assertNull("Klaidos pranešimas turi būti išvalytas", viewModel.uiState.value.errorMessage)
        assertNotNull("Rezultatas turi būti apskaičiuotas", viewModel.uiState.value.currentResult)
        assertEquals(1, viewModel.uiState.value.history.size)
        assertEquals("Labas rytas", viewModel.uiState.value.history[0].text)
        assertEquals("1, 2, 11, 0", viewModel.uiState.value.history[0].result.toSummaryString())
    }
}
