package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.analyzer.TextMetricsAnalyzer
import com.example.analyzer.TextMetricsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Vienas atlikto skaičiavimo įrašas istorijoje (runtime).
 */
data class HistoryRecord(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val result: TextMetricsResult,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * UI būsenos modelis.
 */
data class TextMetricsUiState(
    val inputText: String = "",
    val currentResult: TextMetricsResult? = null,
    val errorMessage: String? = null,
    val history: List<HistoryRecord> = emptyList(),
    val isSampleDialogVisible: Boolean = false
)

/**
 * ViewModel valdanti teksto metrikų skaičiavimą, validaciją ir runtime istoriją.
 */
class TextMetricsViewModel : ViewModel() {

    // Paruošti pavyzdiniai sakiniai testavimui
    val sampleSentences = listOf(
        "Labas rytas",
        "Rugsėjo 1 prasidėjo karas. Liūdna",
        "Vilnius įkurtas 1323 metais, o Kaunas minimas nuo 1361 m.",
        "Kiek kainuoja 2 obuoliai ir 15 apelsinų? Iš viso 17 eurų!",
        "Ar šiandien lis? Galbūt. Bet pasiimk skėtį!",
        "MIPT praktikos darbas #2: teksto metrikų skaičiavimas veikia puikiai."
    )

    private val _uiState = MutableStateFlow(TextMetricsUiState())
    val uiState: StateFlow<TextMetricsUiState> = _uiState.asStateFlow()

    fun onTextChanged(newText: String) {
        _uiState.update { current ->
            current.copy(
                inputText = newText,
                errorMessage = if (newText.isNotBlank()) null else current.errorMessage
            )
        }
    }

    /**
     * Vykdo metrikų skaičiavimą su tuščio lauko patikra (4 reikalavimas).
     */
    fun calculateMetrics() {
        val text = _uiState.value.inputText
        if (text.trim().isEmpty()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Įvesties laukas yra tuščias! Įveskite tekstą skaičiavimui.",
                    currentResult = null
                )
            }
            return
        }

        val result = TextMetricsAnalyzer.analyze(text)
        val historyRecord = HistoryRecord(
            text = text,
            result = result
        )

        _uiState.update { current ->
            current.copy(
                currentResult = result,
                errorMessage = null,
                history = listOf(historyRecord) + current.history
            )
        }
    }

    /**
     * Įkelia pasirinktą testinį sakinį ir automatiškai jį išanalizuoja.
     */
    fun loadSample(sample: String) {
        val result = TextMetricsAnalyzer.analyze(sample)
        val record = HistoryRecord(text = sample, result = result)
        _uiState.update { current ->
            current.copy(
                inputText = sample,
                currentResult = result,
                errorMessage = null,
                history = listOf(record) + current.history,
                isSampleDialogVisible = false
            )
        }
    }

    /**
     * Parinkti atsitiktinį testinį sakinį.
     */
    fun loadRandomSample() {
        val randomSample = sampleSentences.random()
        loadSample(randomSample)
    }

    /**
     * Įkelia tekstą iš istorijos atgal į redaktorių.
     */
    fun selectHistoryItem(record: HistoryRecord) {
        _uiState.update {
            it.copy(
                inputText = record.text,
                currentResult = record.result,
                errorMessage = null
            )
        }
    }

    /**
     * Išvalo teksto įvestį ir esamą rezultatą.
     */
    fun clearInput() {
        _uiState.update {
            it.copy(
                inputText = "",
                currentResult = null,
                errorMessage = null
            )
        }
    }

    /**
     * Išvalo skaičiavimų istoriją.
     */
    fun clearHistory() {
        _uiState.update {
            it.copy(history = emptyList())
        }
    }

    fun dismissError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun setSampleDialogVisible(visible: Boolean) {
        _uiState.update {
            it.copy(isSampleDialogVisible = visible)
        }
    }
}
