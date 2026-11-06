package com.example.analyzer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM Unit testai [TextMetricsAnalyzer] moduliui.
 *
 * Tikrinamos visos viešos funkcijos:
 * - [TextMetricsAnalyzer.countSentences]
 * - [TextMetricsAnalyzer.countWords]
 * - [TextMetricsAnalyzer.countCharacters]
 * - [TextMetricsAnalyzer.countNumbers]
 * - [TextMetricsAnalyzer.countCharactersWithoutSpaces]
 * - [TextMetricsAnalyzer.countPunctuation]
 * - [TextMetricsAnalyzer.analyze]
 *
 * Apima praktikos darbo užduoties pavyzdžius, visus kraštutinius atvejus
 * (tuščias tekstas, tarpai, skyryba, lietuviškos raidės, skaičiai su kableliais/taškais,
 * keli skyrikliai iš eilės, eilučių lūžiai, ilgas tekstas)
 * ir atitinka AAA (Arrange / Act / Assert) struktūrą.
 */
class TextMetricsAnalyzerTest {

    // =========================================================================
    // 1. Užduoties pavyzdžiai (MIPT praktikos darbas #2)
    // =========================================================================

    @Test
    fun analyze_assignmentExample1_LabasRytas_returnsExpectedMetrics() {
        // Arrange
        val text = "Labas rytas"

        // Act
        val result = TextMetricsAnalyzer.analyze(text)

        // Assert
        assertEquals("Sakinių skaičius turi būti 1", 1, result.sentencesCount)
        assertEquals("Žodžių skaičius turi būti 2", 2, result.wordsCount)
        assertEquals("Rašybos ženklų (su tarpais) skaičius turi būti 11", 11, result.charactersCount)
        assertEquals("Skaičių skaičius turi būti 0", 0, result.numbersCount)
        assertEquals("Simbolių be tarpų skaičius turi būti 10", 10, result.charactersWithoutSpacesCount)
        assertEquals("Skyrybos ženklų skaičius turi būti 0", 0, result.punctuationCount)
        assertEquals("Santraukos formatas turi atitikti užduoties pavyzdį", "1, 2, 11, 0", result.toSummaryString())
    }

    @Test
    fun analyze_assignmentExample2_Rugsejo1_returnsExpectedMetrics() {
        // Arrange
        val text = "Rugsėjo 1 prasidėjo karas. Liūdna"

        // Act
        val result = TextMetricsAnalyzer.analyze(text)

        // Assert
        assertEquals("Sakinių skaičius turi būti 2", 2, result.sentencesCount)
        assertEquals("Žodžių skaičius turi būti 4", 4, result.wordsCount)
        assertEquals("Rašybos ženklų (su tarpais) skaičius turi būti 33", 33, result.charactersCount)
        assertEquals("Skaičių skaičius turi būti 1", 1, result.numbersCount)
        assertEquals("Simbolių be tarpų skaičius turi būti 29", 29, result.charactersWithoutSpacesCount)
        assertEquals("Skyrybos ženklų skaičius turi būti 1", 1, result.punctuationCount)
        assertEquals("Santraukos formatas turi atitikti užduoties pavyzdį", "2, 4, 33, 1", result.toSummaryString())
    }

    // =========================================================================
    // 2. Sakinių skaičiavimo testai (countSentences)
    // =========================================================================

    @Test
    fun countSentences_whenEmpty_returnsZero() {
        // Arrange
        val text = ""

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        assertEquals(0, count)
    }

    @Test
    fun countSentences_whenWhitespaceOnly_returnsZero() {
        // Arrange
        val text = "   \n\t  \r "

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        assertEquals(0, count)
    }

    @Test
    fun countSentences_singleSentenceWithoutPunctuation_returnsOne() {
        // Arrange
        val text = "Vienas sakinys be taško"

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        assertEquals(1, count)
    }

    @Test
    fun countSentences_singleSentenceWithPeriod_returnsOne() {
        // Arrange
        val text = "Vienas sakinys su tašku."

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        assertEquals(1, count)
    }

    @Test
    fun countSentences_multipleDelimiters_countsEachSentence() {
        // Arrange
        val text = "Pirmas sakinys. Antras sakinys! Trečias sakinys? Ketvirtas sakinys… Penktas"

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        assertEquals(5, count)
    }

    @Test
    fun countSentences_withConsecutivePunctuation_treatedAsSingleDelimiter() {
        // Arrange
        val text = "Ar tikrai??? Taip, žinoma!!!"

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        assertEquals(2, count)
    }

    @Test
    fun countSentences_withNewlines_countsLinesAsSentences() {
        // Arrange
        val text = "Pirma eilutė\nAntra eilutė\r\nTrečia eilutė"

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        assertEquals(3, count)
    }

    @Test
    fun countSentences_onlyPunctuationDelimiters_returnsOne() {
        // Arrange
        val text = "..."

        // Act
        val count = TextMetricsAnalyzer.countSentences(text)

        // Assert
        // Kai tekstą sudaro tik skyrikliai, išfiltruotas sąrašas tuščias, todėl grąžinama 1
        assertEquals(1, count)
    }

    // =========================================================================
    // 3. Žodžių skaičiavimo testai (countWords)
    // =========================================================================

    @Test
    fun countWords_whenEmptyOrBlank_returnsZero() {
        // Arrange
        val emptyText = ""
        val whitespaceText = "    \n\t  "

        // Act & Assert
        assertEquals(0, TextMetricsAnalyzer.countWords(emptyText))
        assertEquals(0, TextMetricsAnalyzer.countWords(whitespaceText))
    }

    @Test
    fun countWords_standardSentence_countsWordsCorrectly() {
        // Arrange
        val text = "Šiandien yra graži ir saulėta diena"

        // Act
        val count = TextMetricsAnalyzer.countWords(text)

        // Assert
        assertEquals(6, count)
    }

    @Test
    fun countWords_ignoresPureNumbers() {
        // Arrange
        val text = "Yra 100 priežasčių ir 25 būdai"

        // Act
        val count = TextMetricsAnalyzer.countWords(text)

        // Assert
        // Žodžiai: „Yra“, „priežasčių“, „ir“, „būdai“ (iš viso 4; 100 ir 25 yra skaičiai)
        assertEquals(4, count)
    }

    @Test
    fun countWords_hyphenatedAndApostropheWords_countsAsSingleWord() {
        // Arrange
        val text = "lopšys-vežimėlis tamsiai-mėlyna O'Connor D’Angelo"

        // Act
        val count = TextMetricsAnalyzer.countWords(text)

        // Assert
        assertEquals(4, count)
    }

    @Test
    fun countWords_onlyNumbers_returnsZero() {
        // Arrange
        val text = "1 23 456 7890 3.14 2,5"

        // Act
        val count = TextMetricsAnalyzer.countWords(text)

        // Assert
        assertEquals(0, count)
    }

    @Test
    fun countWords_withLithuanianLettersInSample_countsAccurately() {
        // Arrange
        val text = "Vilnius įkurtas 1323 metais, o Kaunas minimas nuo 1361 m."

        // Act
        val count = TextMetricsAnalyzer.countWords(text)

        // Assert
        // Žodžiai: Vilnius, įkurtas, metais, o, Kaunas, minimas, nuo, m (8 žodžiai)
        assertEquals(8, count)
    }

    // =========================================================================
    // 4. Rašybos ženklų (simbolių) skaičiavimo testai (countCharacters)
    // =========================================================================

    @Test
    fun countCharacters_whenEmpty_returnsZero() {
        // Arrange & Act & Assert
        assertEquals(0, TextMetricsAnalyzer.countCharacters(""))
    }

    @Test
    fun countCharacters_includesSpacesAndPunctuation() {
        // Arrange
        val text = "Labas rytas!"

        // Act
        val count = TextMetricsAnalyzer.countCharacters(text)

        // Assert
        assertEquals(12, count)
    }

    @Test
    fun countCharacters_whitespaceOnly_countsAllCharacters() {
        // Arrange
        val text = "   \n\t  "

        // Act
        val count = TextMetricsAnalyzer.countCharacters(text)

        // Assert
        assertEquals(7, count)
    }

    // =========================================================================
    // 5. Skaičių skaičiavimo testai (countNumbers)
    // =========================================================================

    @Test
    fun countNumbers_whenEmptyOrBlank_returnsZero() {
        // Arrange & Act & Assert
        assertEquals(0, TextMetricsAnalyzer.countNumbers(""))
        assertEquals(0, TextMetricsAnalyzer.countNumbers("   \n\t "))
    }

    @Test
    fun countNumbers_noNumbers_returnsZero() {
        // Arrange
        val text = "Tekstas kuriame visiškai nėra skaičių."

        // Act
        val count = TextMetricsAnalyzer.countNumbers(text)

        // Assert
        assertEquals(0, count)
    }

    @Test
    fun countNumbers_integersAndDecimalsWithDotAndComma_countedCorrectly() {
        // Arrange
        val text = "Pirkau 2 obuolius už 3.50 EUR ir 15 apelsinų už 12,75 EUR."

        // Act
        val count = TextMetricsAnalyzer.countNumbers(text)

        // Assert
        // Skaičiai: 2, 3.50, 15, 12,75 -> iš viso 4
        assertEquals(4, count)
    }

    @Test
    fun countNumbers_consecutiveNumbersSeparatedBySpaces_countedSeparately() {
        // Arrange
        val text = "1 2 3 4 5 10 20 2026"

        // Act
        val count = TextMetricsAnalyzer.countNumbers(text)

        // Assert
        assertEquals(8, count)
    }

    // =========================================================================
    // 6. Simbolių be tarpų skaičiavimo testai (countCharactersWithoutSpaces)
    // =========================================================================

    @Test
    fun countCharactersWithoutSpaces_excludesAllWhitespaceCharacters() {
        // Arrange
        val text = " a \n b \t c "

        // Act
        val count = TextMetricsAnalyzer.countCharactersWithoutSpaces(text)

        // Assert
        assertEquals(3, count)
    }

    @Test
    fun countCharactersWithoutSpaces_whenWhitespaceOnly_returnsZero() {
        // Arrange & Act & Assert
        assertEquals(0, TextMetricsAnalyzer.countCharactersWithoutSpaces("   \t\n\r "))
    }

    // =========================================================================
    // 7. Skyrybos ženklų skaičiavimo testai (countPunctuation)
    // =========================================================================

    @Test
    fun countPunctuation_whenNoPunctuation_returnsZero() {
        // Arrange
        val text = "Tekstas be skyrybos ženklų 123"

        // Act
        val count = TextMetricsAnalyzer.countPunctuation(text)

        // Assert
        assertEquals(0, count)
    }

    @Test
    fun countPunctuation_variousPunctuationAndSymbols_countedCorrectly() {
        // Arrange
        val text = "„Labas, pasauli!“ – ar girdi? (Taip; žinoma: 100%)."

        // Act
        val count = TextMetricsAnalyzer.countPunctuation(text)

        // Assert
        // Skyryba/simboliai: „ , ! “ – ? ( ; : % ) . -> 12
        assertEquals(12, count)
    }

    // =========================================================================
    // 8. Kraštutiniai atvejai ir bendroji analizė (analyze)
    // =========================================================================

    @Test
    fun analyze_emptyString_returnsAllZeroes() {
        // Arrange
        val text = ""

        // Act
        val result = TextMetricsAnalyzer.analyze(text)

        // Assert
        assertEquals(0, result.sentencesCount)
        assertEquals(0, result.wordsCount)
        assertEquals(0, result.charactersCount)
        assertEquals(0, result.numbersCount)
        assertEquals(0, result.charactersWithoutSpacesCount)
        assertEquals(0, result.punctuationCount)
        assertEquals("0, 0, 0, 0", result.toSummaryString())
    }

    @Test
    fun analyze_veryLongText_calculatesMetricsConsistently() {
        // Arrange - generuojame ilgą tekstą su 100 sakinių
        val sentence = "Vilnius yra Lietuvos sostinė, turinti 500000 gyventojų! "
        val repeatCount = 100
        val longText = sentence.repeat(repeatCount)

        // Act
        val result = TextMetricsAnalyzer.analyze(longText)

        // Assert
        assertEquals(repeatCount, result.sentencesCount)
        assertEquals(6 * repeatCount, result.wordsCount)
        assertEquals(1 * repeatCount, result.numbersCount)
        assertEquals(sentence.length * repeatCount, result.charactersCount)
        assertTrue(result.charactersWithoutSpacesCount > 0)
        assertTrue(result.punctuationCount >= repeatCount)
    }

    @Test
    fun analyze_onlyPunctuation_returnsConsistentMetrics() {
        // Arrange
        val text = "...!?---"

        // Act
        val result = TextMetricsAnalyzer.analyze(text)

        // Assert
        assertEquals(1, result.sentencesCount)
        assertEquals(0, result.wordsCount)
        assertEquals(text.length, result.charactersCount)
        assertEquals(0, result.numbersCount)
        assertEquals(text.length, result.charactersWithoutSpacesCount)
        assertEquals(text.length, result.punctuationCount)
    }

    // =========================================================================
    // 9. Reikalavimas #9: Klaidą atskleidžiantis testas (Unicode \b apribojimas)
    // =========================================================================

    @Test
    fun countWords_pureUnicodeLithuanianWord_isCorrectlyCountedAsOneWord() {
        // Arrange
        // Žodis sudarytas vien tik iš ne-ASCII lietuviškų raidžių (pvz., „šį“).
        // Pagal lietuvių kalbos taisykles tai yra lygiai 1 prasmingas žodis.
        val text = "šį"

        // Act
        val actualWordCount = TextMetricsAnalyzer.countWords(text)

        // Assert
        // Naudojant (?U) vėliavėlę WORD_REGEX reguliariojoje išraiškoje,
        // grynai ne-ASCII žodžiai (kaip „šį“, „ąčę“) teisingai atpažįstami kaip žodžiai.
        assertEquals("Žodis „šį“ turi būti suskaičiuotas kaip 1 žodis", 1, actualWordCount)
    }
}
