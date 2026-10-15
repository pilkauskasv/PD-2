package com.example.analyzer

/**
 * Rezultatų duomenų klasė, talpinanti visas apskaičiuotas teksto metrikas.
 */
data class TextMetricsResult(
    val sentencesCount: Int,
    val wordsCount: Int,
    val charactersCount: Int,
    val numbersCount: Int,
    val charactersWithoutSpacesCount: Int = 0,
    val punctuationCount: Int = 0
) {
    /**
     * Grąžina trumpą reprezentacijos formatą pagal praktikos darbo pavyzdį:
     * pvz. "1, 2, 11, 0" (Sakiniai, Žodžiai, Ženklai, Skaičiai)
     */
    fun toSummaryString(): String {
        return "$sentencesCount, $wordsCount, $charactersCount, $numbersCount"
    }
}

/**
 * Atskiras, nepriklausomas teksto metrikų analizatorius (perpanaudojamas modulis).
 * Realizuoja sakinių, žodžių, rašybos ženklų (simbolių) ir skaičių skaičiavimo logiką.
 */
object TextMetricsAnalyzer {

    // Regex atitinkantis žodžius (tik raidžių sekos su galima brūkšnine jungtimi)
    private val WORD_REGEX = Regex("""\b[\p{L}\p{M}]+(?:[-'’][\p{L}\p{M}]+)*\b""")

    // Regex atitinkantis skaičius (sveikieji ir dešimtainiai skaičiai)
    private val NUMBER_REGEX = Regex("""\b\d+(?:[.,]\d+)*\b""")

    // Regex sakinių pabaigos skyrikliams
    private val SENTENCE_DELIMITER_REGEX = Regex("""[.!?…]+|[\r\n]+""")

    // Regex skyrybos ženklams
    private val PUNCTUATION_REGEX = Regex("""[\p{P}\p{S}]""")

    /**
     * Apskaičiuoja sakinių skaičių tekste.
     * Sakinys baigiasi tašku, klaustuku, šauktuku, daugtaškiu arba teksto pabaiga / eilutės lūžiu.
     */
    fun countSentences(text: String): Int {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return 0

        val sentences = trimmed.split(SENTENCE_DELIMITER_REGEX)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return if (sentences.isEmpty()) 1 else sentences.size
    }

    /**
     * Apskaičiuoja žodžių skaičių tekste.
     * Skaičiuojami tik tekstiniai žodžiai (sudaryti iš raidžių), grynus skaičius atskiriant.
     */
    fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return WORD_REGEX.findAll(text).count()
    }

    /**
     * Apskaičiuoja bendrą rašybos ženklų (simbolių) skaičių su tarpais.
     * Kaip patvirtinta specifikacijoje („Labas rytas“ => 11; „Rugsėjo 1 prasidėjo karas. Liūdna“ => 33).
     */
    fun countCharacters(text: String): Int {
        return text.length
    }

    /**
     * Apskaičiuoja skaičių skaičių tekste (skaitmenų sekos/grupės kaip atskiri skaičiai).
     */
    fun countNumbers(text: String): Int {
        if (text.isBlank()) return 0
        return NUMBER_REGEX.findAll(text).count()
    }

    /**
     * Apskaičiuoja simbolių skaičių be tarpų.
     */
    fun countCharactersWithoutSpaces(text: String): Int {
        return text.count { !it.isWhitespace() }
    }

    /**
     * Apskaičiuoja skyrybos ženklų skaičių.
     */
    fun countPunctuation(text: String): Int {
        return PUNCTUATION_REGEX.findAll(text).count()
    }

    /**
     * Atlieka pilną teksto analizę ir grąžina visų metrikų rezultatą.
     */
    fun analyze(text: String): TextMetricsResult {
        return TextMetricsResult(
            sentencesCount = countSentences(text),
            wordsCount = countWords(text),
            charactersCount = countCharacters(text),
            numbersCount = countNumbers(text),
            charactersWithoutSpacesCount = countCharactersWithoutSpaces(text),
            punctuationCount = countPunctuation(text)
        )
    }
}
