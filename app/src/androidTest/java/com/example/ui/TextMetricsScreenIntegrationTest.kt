package com.example.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.MainActivity
import com.example.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI ir Integraciniai (instrumented) testai [TextMetricsScreen] vartotojo sąsajai.
 *
 * Testuojama realiame Android kontekste / emuliatoriuje naudojant [createAndroidComposeRule].
 * Apima 8 privalomus scenarijus:
 * 1) Tuščio lauko validacija (klaidos pranešimas, metrikos nerodomos, istorija nepildoma)
 * 2) Užduoties 1 pavyzdys: „Labas rytas“ => rodomi 1, 2, 11, 0 ir istorijos įrašas
 * 3) Užduoties 2 pavyzdys: „Rugsėjo 1 prasidėjo karas. Liūdna“ => 2, 4, 33, 1
 * 4) Testinių sakinių pavyzdžių dialogas: atidarymas, sakinio parinkimas, lauko užpildymas ir automatinis skaičiavimas
 * 5) Atsitiktinio sakinio mygtukas (TopAppBar) prideda naują įrašą į istoriją
 * 6) Paspaudus istorijos įrašą, tekstas grįžta atgal į įvesties lauką
 * 7) „Valyti istoriją“ mygtukas išvalo visus istorijos įrašus
 * 8) Ekrano pasukimas / aktyvumo perkūrimas (recreate): įvestas tekstas ir istorija išlieka
 */
@RunWith(AndroidJUnit4::class)
class TextMetricsScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val targetContext
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    // =========================================================================
    // 1 scenarijus: Tuščio lauko validacija
    // =========================================================================
    @Test
    fun test01_emptyInputValidation_showsErrorAndDoesNotAddToHistory() {
        val emptyErrorText = targetContext.getString(R.string.error_empty_input)

        // Užtikriname, kad teksto laukas tuščias
        composeTestRule.onNodeWithTag("text_input_field").performTextClearance()

        // Spaudžiame „Skaičiuoti metrikas“
        composeTestRule.onNodeWithTag("calculate_button").performClick()
        composeTestRule.waitForIdle()

        // Patikriname, kad rodomas klaidos pranešimas
        composeTestRule.onNodeWithTag("input_error_message").assertIsDisplayed()
        composeTestRule.onNodeWithText(emptyErrorText).assertIsDisplayed()

        // Patikriname, kad metrikų rezultatų kortelė nerodoma
        composeTestRule.onNodeWithTag("metrics_result_card").assertDoesNotExist()

        // Patikriname, kad istorija nepildoma ir rodoma tuščios istorijos kortelė
        composeTestRule.onNodeWithTag("history_item_0").assertDoesNotExist()
        composeTestRule.onNodeWithTag("empty_history_card").assertIsDisplayed()
    }

    // =========================================================================
    // 2 scenarijus: „Labas rytas“ => 1, 2, 11, 0 ir istorijos įrašas
    // =========================================================================
    @Test
    fun test02_calculate_labasRytas_showsExpectedMetricsAndHistoryRecord() {
        val sampleText = "Labas rytas"

        // Įvedame tekstą
        composeTestRule.onNodeWithTag("text_input_field").performTextClearance()
        composeTestRule.onNodeWithTag("text_input_field").performTextInput(sampleText)

        // Spaudžiame skaičiuoti
        composeTestRule.onNodeWithTag("calculate_button").performClick()
        composeTestRule.waitForIdle()

        // Tikriname, kad atsirado rezultatų kortelė su teisingu formatu
        composeTestRule.onNodeWithTag("metrics_result_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Formatas: 1, 2, 11, 0").assertIsDisplayed()

        // Tikriname 4 atskirų rodiklių reikšmes
        composeTestRule.onNodeWithTag("metric_card_sentences").assertIsDisplayed()
        composeTestRule.onNodeWithTag("metric_card_words").assertIsDisplayed()
        composeTestRule.onNodeWithTag("metric_card_characters").assertIsDisplayed()
        composeTestRule.onNodeWithTag("metric_card_numbers").assertIsDisplayed()

        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_sentences)}")
            .assertTextEquals("1")
        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_words)}")
            .assertTextEquals("2")
        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_characters)}")
            .assertTextEquals("11")
        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_numbers)}")
            .assertTextEquals("0")

        // Tikriname, kad atsirado istorijos įrašas su tekstu ir santrauka
        composeTestRule.onNodeWithTag("history_item_0").assertIsDisplayed()
        composeTestRule.onNodeWithText("„$sampleText“").assertIsDisplayed()
        composeTestRule.onNodeWithText("=> 1, 2, 11, 0").assertIsDisplayed()
    }

    // =========================================================================
    // 3 scenarijus: „Rugsėjo 1 prasidėjo karas. Liūdna“ => 2, 4, 33, 1
    // =========================================================================
    @Test
    fun test03_calculate_rugsejo1_showsExpectedMetricsAndHistoryRecord() {
        val sampleText = "Rugsėjo 1 prasidėjo karas. Liūdna"

        composeTestRule.onNodeWithTag("text_input_field").performTextClearance()
        composeTestRule.onNodeWithTag("text_input_field").performTextInput(sampleText)
        composeTestRule.onNodeWithTag("calculate_button").performClick()
        composeTestRule.waitForIdle()

        // Formatas: 2, 4, 33, 1
        composeTestRule.onNodeWithTag("metrics_result_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Formatas: 2, 4, 33, 1").assertIsDisplayed()

        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_sentences)}")
            .assertTextEquals("2")
        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_words)}")
            .assertTextEquals("4")
        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_characters)}")
            .assertTextEquals("33")
        composeTestRule.onNodeWithTag("metric_value_${targetContext.getString(R.string.metric_numbers)}")
            .assertTextEquals("1")

        // Istorijos įrašas
        composeTestRule.onNodeWithTag("history_item_0").assertIsDisplayed()
        composeTestRule.onNodeWithText("„$sampleText“").assertIsDisplayed()
        composeTestRule.onNodeWithText("=> 2, 4, 33, 1").assertIsDisplayed()
    }

    // =========================================================================
    // 4 scenarijus: Testinių sakinių dialogas
    // =========================================================================
    @Test
    fun test04_samplesDialog_openSelectSample_populatesFieldAndCalculates() {
        val dialogTitle = targetContext.getString(R.string.dialog_sample_title)

        // Atidarome testinių sakinių dialogą mygtuku
        composeTestRule.onNodeWithTag("samples_button").performClick()
        composeTestRule.waitForIdle()

        // Patikriname, kad dialogas atidarytas
        composeTestRule.onNodeWithTag("samples_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText(dialogTitle).assertIsDisplayed()

        // Pasirenkame pirmąjį pavyzdį (index 0: „Labas rytas“)
        composeTestRule.onNodeWithTag("sample_item_0").performClick()
        composeTestRule.waitForIdle()

        // Užtikriname, kad dialogas uždarytas
        composeTestRule.onNodeWithTag("samples_dialog").assertDoesNotExist()

        // Patikriname, kad laukas užpildytas pasirinktu tekstu
        composeTestRule.onNodeWithTag("text_input_field").assertTextContains("Labas rytas")

        // Patikriname, kad skaičiavimas atliktas automatiškai
        composeTestRule.onNodeWithTag("metrics_result_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Formatas: 1, 2, 11, 0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_item_0").assertIsDisplayed()
    }

    // =========================================================================
    // 5 scenarijus: Atsitiktinio sakinio mygtukas prideda istorijos įrašą
    // =========================================================================
    @Test
    fun test05_randomSampleButton_addsHistoryRecord() {
        // Paspaudžiame viršutinės juostos atsitiktinio sakinio piktogramą
        composeTestRule.onNodeWithTag("random_sample_top_button").performClick()
        composeTestRule.waitForIdle()

        // Patikriname, kad skaičiavimo rezultatas rodomas
        composeTestRule.onNodeWithTag("metrics_result_card").assertIsDisplayed()

        // Patikriname, kad istorijoje yra įrašas
        composeTestRule.onNodeWithTag("history_item_0").assertIsDisplayed()
    }

    // =========================================================================
    // 6 scenarijus: Paspaudus istorijos įrašą, tekstas grįžta į įvesties lauką
    // =========================================================================
    @Test
    fun test06_clickHistoryItem_restoresTextToInputField() {
        val sampleText = "Vilnius įkurtas 1323 metais, o Kaunas minimas nuo 1361 m."

        // Įvedame ir apskaičiuojame metrikas
        composeTestRule.onNodeWithTag("text_input_field").performTextClearance()
        composeTestRule.onNodeWithTag("text_input_field").performTextInput(sampleText)
        composeTestRule.onNodeWithTag("calculate_button").performClick()
        composeTestRule.waitForIdle()

        // Išvalome įvesties lauką paspausdami lauko išvalymo mygtuką
        composeTestRule.onNodeWithTag("clear_input_icon").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("text_input_field").assertTextEquals("")

        // Paspaudžiame istorijos įrašą sąraše
        composeTestRule.onNodeWithTag("history_item_0").performClick()
        composeTestRule.waitForIdle()

        // Patikriname, kad tekstas sugrįžo į įvesties lauką
        composeTestRule.onNodeWithTag("text_input_field").assertTextContains(sampleText)
        composeTestRule.onNodeWithTag("metrics_result_card").assertIsDisplayed()
    }

    // =========================================================================
    // 7 scenarijus: „Valyti istoriją“ išvalo istoriją
    // =========================================================================
    @Test
    fun test07_clearHistory_clearsAllHistoryItems() {
        // Užtikriname, kad istorijoje yra bent vienas įrašas
        composeTestRule.onNodeWithTag("text_input_field").performTextClearance()
        composeTestRule.onNodeWithTag("text_input_field").performTextInput("Testinis sakinys istorijai")
        composeTestRule.onNodeWithTag("calculate_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("history_item_0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("clear_history_button").assertIsDisplayed()

        // Spaudžiame „Valyti istoriją“
        composeTestRule.onNodeWithTag("clear_history_button").performClick()
        composeTestRule.waitForIdle()

        // Patikriname, kad istorija tuščia ir valymo mygtukas pasislėpė
        composeTestRule.onNodeWithTag("history_item_0").assertDoesNotExist()
        composeTestRule.onNodeWithTag("clear_history_button").assertDoesNotExist()
        composeTestRule.onNodeWithTag("empty_history_card").assertIsDisplayed()
    }

    // =========================================================================
    // 8 scenarijus: Ekrano pasukimas / aktyvumo perkūrimas išlaiko būseną
    // =========================================================================
    @Test
    fun test08_screenRotationOrRecreation_retainsInputTextAndHistory() {
        val testText = "Sakinys prieš ekrano perkūrimą 2026"

        // Įvedame tekstą ir atliekame skaičiavimą
        composeTestRule.onNodeWithTag("text_input_field").performTextClearance()
        composeTestRule.onNodeWithTag("text_input_field").performTextInput(testText)
        composeTestRule.onNodeWithTag("calculate_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("metrics_result_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_item_0").assertIsDisplayed()

        // Perkuriame aktyvumą (simuliuoja ekrano orientacijos pasikeitimą / configuration change)
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        // Patikriname, kad po perkūrimo įvesties tekstas, rezultatas ir istorija išliko
        composeTestRule.onNodeWithTag("text_input_field").assertTextContains(testText)
        composeTestRule.onNodeWithTag("metrics_result_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_item_0").assertIsDisplayed()
        composeTestRule.onNodeWithText("„$testText“").assertIsDisplayed()
    }
}
