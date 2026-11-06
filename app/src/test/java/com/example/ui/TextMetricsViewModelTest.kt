package com.example.ui

import com.example.analyzer.TextMetricsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * JVM Unit testai [TextMetricsViewModel] klasei:
 * - Pradinė būsena
 * - Įvesties keitimas ([TextMetricsViewModel.onTextChanged])
 * - Tuščios ir tik tarpų įvesties validacija (klaidos pranešimas, istorija nepildoma)
 * - Sėkmingas skaičiavimas ([TextMetricsViewModel.calculateMetrics])
 * - Istorijos eiliškumas (naujausias įrašas viršuje)
 * - Testinių sakinių įkėlimas ([TextMetricsViewModel.loadSample], [TextMetricsViewModel.loadRandomSample])
 * - Istorijos įrašo pasirinkimas ([TextMetricsViewModel.selectHistoryItem])
 * - Įvesties ir rezultatų valymas ([TextMetricsViewModel.clearInput])
 * - Istorijos valymas ([TextMetricsViewModel.clearHistory])
 * - Klaidos pranešimo atmetimas ([TextMetricsViewModel.dismissError])
 * - Pavyzdžių dialogo matomumo valdymas ([TextMetricsViewModel.setSampleDialogVisible])
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TextMetricsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TextMetricsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TextMetricsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // =========================================================================
    // Pradinės būsenos testai
    // =========================================================================

    @Test
    fun initialState_hasDefaultValues() {
        // Arrange & Act
        val state = viewModel.uiState.value

        // Assert
        assertEquals("", state.inputText)
        assertNull(state.currentResult)
        assertNull(state.errorMessage)
        assertTrue(state.history.isEmpty())
        assertFalse(state.isSampleDialogVisible)
    }

    @Test
    fun sampleSentences_containsPredefinedSentences() {
        // Arrange & Act
        val samples = viewModel.sampleSentences

        // Assert
        assertEquals(6, samples.size)
        assertTrue(samples.contains("Labas rytas"))
        assertTrue(samples.contains("Rugsėjo 1 prasidėjo karas. Liūdna"))
    }

    // =========================================================================
    // Įvesties keitimo testai (onTextChanged)
    // =========================================================================

    @Test
    fun onTextChanged_updatesInputText() {
        // Arrange
        val newText = "Sveikas, pasauli"

        // Act
        viewModel.onTextChanged(newText)

        // Assert
        assertEquals("Sveikas, pasauli", viewModel.uiState.value.inputText)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onTextChanged_whenTextIsNotBlank_clearsPreviousErrorMessage() {
        // Arrange - sukeliame klaidą
        viewModel.onTextChanged("")
        viewModel.calculateMetrics()
        assertNotNull("Klaida turi būti nustatyta", viewModel.uiState.value.errorMessage)

        // Act - įvedame ne tuščią tekstą
        viewModel.onTextChanged("Pradėtas vesti tekstas")

        // Assert
        assertEquals("Pradėtas vesti tekstas", viewModel.uiState.value.inputText)
        assertNull("Klaidos pranešimas turi būti išvalytas", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onTextChanged_whenTextIsBlank_retainsExistingErrorMessage() {
        // Arrange - sukeliame klaidą
        viewModel.onTextChanged("")
        viewModel.calculateMetrics()
        val errorBefore = viewModel.uiState.value.errorMessage
        assertNotNull(errorBefore)

        // Act - įvedame tik tarpus
        viewModel.onTextChanged("   ")

        // Assert
        assertEquals("   ", viewModel.uiState.value.inputText)
        assertEquals("Klaidos pranešimas turi išlikti", errorBefore, viewModel.uiState.value.errorMessage)
    }

    // =========================================================================
    // Validacijos testai esant tuščiai ar tarpų įvesčiai
    // =========================================================================

    @Test
    fun calculateMetrics_whenInputIsEmpty_setsErrorMessageAndDoesNotAddToHistory() {
        // Arrange
        viewModel.onTextChanged("")

        // Act
        viewModel.calculateMetrics()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Įvesties laukas yra tuščias! Įveskite tekstą skaičiavimui.", state.errorMessage)
        assertNull(state.currentResult)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun calculateMetrics_whenInputIsOnlyWhitespace_setsErrorMessageAndDoesNotAddToHistory() {
        // Arrange
        viewModel.onTextChanged("   \n\t  ")

        // Act
        viewModel.calculateMetrics()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Įvesties laukas yra tuščias! Įveskite tekstą skaičiavimui.", state.errorMessage)
        assertNull(state.currentResult)
        assertTrue(state.history.isEmpty())
    }

    // =========================================================================
    // Sėkmingo skaičiavimo ir istorijos eiliškumo testai
    // =========================================================================

    @Test
    fun calculateMetrics_whenInputIsValid_computesMetricsAndAddsToHistory() {
        // Arrange
        val text = "Labas rytas"
        viewModel.onTextChanged(text)

        // Act
        viewModel.calculateMetrics()

        // Assert
        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertNotNull(state.currentResult)

        val result = state.currentResult!!
        assertEquals(1, result.sentencesCount)
        assertEquals(2, result.wordsCount)
        assertEquals(11, result.charactersCount)
        assertEquals(0, result.numbersCount)

        // Istorija turi turėti 1 įrašą su šiuo tekstu ir rezultatu
        assertEquals(1, state.history.size)
        val record = state.history[0]
        assertEquals(text, record.text)
        assertEquals(result, record.result)
    }

    @Test
    fun calculateMetrics_multipleCalculations_prependsNewestRecordAtTop() {
        // Arrange & Act 1
        viewModel.onTextChanged("Pirmas sakinys")
        viewModel.calculateMetrics()

        // Arrange & Act 2
        viewModel.onTextChanged("Antras sakinys")
        viewModel.calculateMetrics()

        // Arrange & Act 3
        viewModel.onTextChanged("Trečias sakinys")
        viewModel.calculateMetrics()

        // Assert
        val history = viewModel.uiState.value.history
        assertEquals(3, history.size)
        assertEquals("Naujausias įrašas turi būti 0 pozicijoje", "Trečias sakinys", history[0].text)
        assertEquals("Vidurinis įrašas turi būti 1 pozicijoje", "Antras sakinys", history[1].text)
        assertEquals("Seniausias įrašas turi būti 2 pozicijoje", "Pirmas sakinys", history[2].text)
    }

    // =========================================================================
    // Testinių sakinių įkėlimo testai (loadSample, loadRandomSample)
    // =========================================================================

    @Test
    fun loadSample_setsInputTextAnalyzesAndClosesDialog() {
        // Arrange
        val sample = "Rugsėjo 1 prasidėjo karas. Liūdna"
        viewModel.setSampleDialogVisible(true)
        assertTrue(viewModel.uiState.value.isSampleDialogVisible)

        // Act
        viewModel.loadSample(sample)

        // Assert
        val state = viewModel.uiState.value
        assertEquals(sample, state.inputText)
        assertNotNull(state.currentResult)
        assertEquals("2, 4, 33, 1", state.currentResult!!.toSummaryString())
        assertNull(state.errorMessage)
        assertFalse("Dialogas turi būti uždarytas", state.isSampleDialogVisible)
        assertEquals(1, state.history.size)
        assertEquals(sample, state.history[0].text)
    }

    @Test
    fun loadRandomSample_selectsFromPredefinedListAndCalculates() {
        // Arrange & Act
        viewModel.loadRandomSample()

        // Assert
        val state = viewModel.uiState.value
        assertTrue("Įvestas tekstas turi būti vienas iš pavyzdžių", viewModel.sampleSentences.contains(state.inputText))
        assertNotNull(state.currentResult)
        assertNull(state.errorMessage)
        assertEquals(1, state.history.size)
        assertEquals(state.inputText, state.history[0].text)
    }

    // =========================================================================
    // Istorijos įrašo pasirinkimo testai (selectHistoryItem)
    // =========================================================================

    @Test
    fun selectHistoryItem_loadsRecordTextAndResultBackToEditor() {
        // Arrange
        val customResult = TextMetricsResult(1, 2, 11, 0, 10, 0)
        val historyRecord = HistoryRecord(
            id = "test-id-1",
            text = "Senas tekstas iš istorijos",
            result = customResult
        )
        // Užpildome redaktorių kita reikšme ir klaida
        viewModel.onTextChanged("")
        viewModel.calculateMetrics()
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Act
        viewModel.selectHistoryItem(historyRecord)

        // Assert
        val state = viewModel.uiState.value
        assertEquals("Senas tekstas iš istorijos", state.inputText)
        assertEquals(customResult, state.currentResult)
        assertNull("Klaidos pranešimas turi būti išvalytas", state.errorMessage)
    }

    // =========================================================================
    // Išvalymo ir pagalbiniai testai (clearInput, clearHistory, dismissError, setSampleDialogVisible)
    // =========================================================================

    @Test
    fun clearInput_resetsInputResultAndErrorMessage() {
        // Arrange
        viewModel.onTextChanged("Koks nors tekstas")
        viewModel.calculateMetrics()
        assertNotNull(viewModel.uiState.value.currentResult)

        // Act
        viewModel.clearInput()

        // Assert
        val state = viewModel.uiState.value
        assertEquals("", state.inputText)
        assertNull(state.currentResult)
        assertNull(state.errorMessage)
    }

    @Test
    fun clearHistory_emptiesHistoryList() {
        // Arrange
        viewModel.onTextChanged("Tekstas 1")
        viewModel.calculateMetrics()
        viewModel.onTextChanged("Tekstas 2")
        viewModel.calculateMetrics()
        assertEquals(2, viewModel.uiState.value.history.size)

        // Act
        viewModel.clearHistory()

        // Assert
        assertTrue("Istorija turi būti tuščia", viewModel.uiState.value.history.isEmpty())
    }

    @Test
    fun dismissError_clearsErrorMessage() {
        // Arrange
        viewModel.onTextChanged("")
        viewModel.calculateMetrics()
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Act
        viewModel.dismissError()

        // Assert
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun setSampleDialogVisible_togglesDialogVisibilityState() {
        // Act 1
        viewModel.setSampleDialogVisible(true)

        // Assert 1
        assertTrue(viewModel.uiState.value.isSampleDialogVisible)

        // Act 2
        viewModel.setSampleDialogVisible(false)

        // Assert 2
        assertFalse(viewModel.uiState.value.isSampleDialogVisible)
    }
}
