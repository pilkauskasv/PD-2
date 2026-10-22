# Teksto metrikų skaičiuoklė

MIPT praktikos darbas #2. „Android“ programėlė (Kotlin, Jetpack Compose), kuri iš vartotojo įvesto teksto apskaičiuoja **sakinių, žodžių, rašybos ženklų ir skaičių** kiekį, saugo skaičiavimų istoriją ir leidžia greitai išbandyti paruoštus testinius sakinius.

## Turinys

- [Galimybės](#galimybės)
- [Metrikų apibrėžimai](#metrikų-apibrėžimai)
- [Reikalavimai](#reikalavimai)
- [Paleidimas](#paleidimas)
- [Naudojimas](#naudojimas)
- [Projekto struktūra ir architektūra](#projekto-struktūra-ir-architektūra)
- [Metrikų klasės perpanaudojimas](#metrikų-klasės-perpanaudojimas)
- [Testai](#testai)
- [Lokalizacija](#lokalizacija)
- [Žinomi apribojimai](#žinomi-apribojimai)
- [Kūrimo eiga](#kūrimo-eiga)

## Galimybės

- Keturių pagrindinių metrikų skaičiavimas vienu paspaudimu: sakiniai, žodžiai, rašybos ženklai, skaičiai.
- Papildoma statistika: simboliai be tarpų ir skyrybos ženklų skaičius.
- **Skaičiavimų istorija** (tik einamosios sesijos metu, saugoma atmintyje) formatu `„Tekstas“ => sakiniai, žodžiai, ženklai, skaičiai`, naujausi įrašai viršuje. Palietus įrašą, tekstas įkeliamas atgal į įvesties lauką.
- **Testiniai sakiniai:** dialogas su paruoštais pavyzdžiais ir mygtukas atsitiktiniam sakiniui parinkti.
- **Įvesties validacija:** tuščias arba tik iš tarpų sudarytas laukas rodo klaidos pranešimą ir nepildo istorijos.
- Gyvas simbolių skaitiklis prie įvesties lauko.
- Skaičiavimo logika atskirta nuo vartotojo sąsajos, todėl ją lengva testuoti ir perpanaudoti.

## Metrikų apibrėžimai

| Metrika | Kaip skaičiuojama |
|---|---|
| **Sakiniai** | Tekstas skaidomas pagal `.`, `!`, `?`, `…` ir eilutės lūžius; skaičiuojami netušti fragmentai. Paskutinis sakinys be pabaigos ženklo taip pat skaičiuojamas. |
| **Žodžiai** | Raidžių sekos (įskaitant lietuviškas raides); žodžiai su brūkšneliu ar apostrofu (pvz., „pilka-balta“) laikomi vienu žodžiu. Grynieji skaičiai **nėra** žodžiai. |
| **Rašybos ženklai** | Visų simbolių skaičius, **įskaitant tarpus ir skyrybą**. |
| **Skaičiai** | Skaitmenų sekų kiekis (pvz., „2026“ yra vienas skaičius; „2,5“ ir „1.000“ taip pat po vieną). |

### Pavyzdžiai

| Įvestis | Sakiniai | Žodžiai | Rašybos ženklai | Skaičiai |
|---|---|---|---|---|
| `Labas rytas` | 1 | 2 | 11 | 0 |
| `Rugsėjo 1 prasidėjo karas. Liūdna` | 2 | 4 | 33 | 1 |

Istorijoje šie pavyzdžiai atrodo taip:

```
„Labas rytas“ => 1, 2, 11, 0
„Rugsėjo 1 prasidėjo karas. Liūdna“ => 2, 4, 33, 1
```

## Reikalavimai

- **Android Studio** (naujausia stabili versija).
- **JDK 11 ar naujesnis** (naudojamas kartu su Android Studio).
- Android įrenginys arba emuliatorius su **Android 7.0 (API 24) ar naujesne** versija.
- Projektas kompiliuojamas su `compileSdk 36`; Gradle versija nurodyta `gradle/wrapper/gradle-wrapper.properties` (9.3.1).

## Paleidimas

1. Parsisiųskite repozitoriją:
   ```bash
   git clone https://github.com/pilkauskasv/PD-2.git
   ```
2. Android Studio pasirinkite **File → Open** ir atidarykite `PD-2` aplanką. Palaukite, kol baigsis Gradle sinchronizacija.
3. Pasirinkite emuliatorių arba prijungtą telefoną (su įjungtu USB debugging).
4. Paspauskite **Run ▶** (`app` konfigūracija).

## Naudojimas

1. Įveskite arba įklijuokite tekstą į lauką „Teksto įvestis“.
2. Paspauskite **Skaičiuoti metrikas**. Pasirodys keturios metrikų kortelės, o įrašas bus pridėtas prie istorijos.
3. Jei laukas tuščias, bus parodytas klaidos pranešimas ir skaičiavimas neįvyks.
4. Norėdami išbandyti paruoštus pavyzdžius, paspauskite **Testiniai pavyzdžiai** (sąrašas) arba viršutinės juostos kibirkšties piktogramą (atsitiktinis sakinys). Pasirinktas sakinys iškart išanalizuojamas.
5. Istorijoje palietę įrašą, įkelsite jo tekstą ir rezultatą atgal; **Valyti istoriją** išvalo visą sąrašą.

## Projekto struktūra ir architektūra

Naudojama MVVM architektūra su vienakrypčiu duomenų srautu (`StateFlow`).

```
app/src/main/java/com/example/
├── MainActivity.kt                 # Įėjimo taškas, Compose turinys
├── analyzer/
│   └── TextMetricsAnalyzer.kt      # Grynoji skaičiavimo logika (be Android priklausomybių)
├── ui/
│   ├── TextMetricsViewModel.kt     # Būsena, validacija, istorija, testiniai sakiniai
│   ├── TextMetricsScreen.kt        # Compose ekranas ir komponentai
│   └── theme/                      # Spalvos, tipografija, tema
app/src/main/res/values/strings.xml # Vartotojo sąsajos tekstai
app/src/test/                       # Vienetiniai testai
```

| Komponentas | Atsakomybė |
|---|---|
| `TextMetricsAnalyzer` | Skaičiuoja metrikas ir grąžina `TextMetricsResult`. Nepriklauso nuo UI. |
| `TextMetricsViewModel` | Laiko `TextMetricsUiState` (įvestis, rezultatas, klaida, istorija), vykdo validaciją. Būsena išlieka pasukus ekraną. |
| `TextMetricsScreen` | Atvaizduoja būseną ir perduoda vartotojo veiksmus ViewModel. |

## Metrikų klasės perpanaudojimas

`TextMetricsAnalyzer` yra Kotlin `object` be jokių Android klasių, todėl jį galima nukopijuoti į bet kurį Kotlin / JVM projektą:

```kotlin
import com.example.analyzer.TextMetricsAnalyzer

val result = TextMetricsAnalyzer.analyze("Rugsėjo 1 prasidėjo karas. Liūdna")

println(result.sentencesCount)   // 2
println(result.wordsCount)       // 4
println(result.charactersCount)  // 33
println(result.numbersCount)     // 1
println(result.toSummaryString()) // "2, 4, 33, 1"
```

Atskiros funkcijos taip pat prieinamos: `countSentences`, `countWords`, `countCharacters`, `countNumbers`, `countCharactersWithoutSpaces`, `countPunctuation`.

## Testai

Vienetiniai testai (`app/src/test/java/com/example/ExampleUnitTest.kt`) tikrina:

- abu užduoties pavyzdžius (`1, 2, 11, 0` ir `2, 4, 33, 1`);
- kelių sakinių ir skaičių atvejį;
- tuščią ir tik tarpų įvestį;
- ViewModel validaciją ir istorijos pildymą.

Paleidimas: Android Studio lange atidarykite `ExampleUnitTest`, spustelėkite dešinį pelės klavišą ir pasirinkite **Run**, arba Gradle skydelyje paleiskite `app → Tasks → verification → testDebugUnitTest`.

## Lokalizacija

Vartotojo sąsajos tekstai laikomi `app/src/main/res/values/strings.xml` (numatytoji kalba – lietuvių). Naujai kalbai pridėti:

1. Sukurkite aplanką `app/src/main/res/values-<kalbos kodas>/` (pvz., `values-en/`).
2. Į jį įdėkite `strings.xml` su išverstomis tomis pačiomis eilutėmis.

## Žinomi apribojimai

- **Sakinių skaičiavimas** remiasi skyrikliais `.`, `!`, `?`, `…`, todėl dešimtainis taškas (pvz., „3.5“), inicialai (pvz., „J. Jonaitis“) ir trumpiniai (pvz., „Dr.“) gali būti klaidingai laikomi sakinio pabaiga. Eilutės lūžis taip pat skaidomas kaip sakinio riba.
- **Žodžių skaičiavimas** naudoja regex su `\b`; JVM aplinkoje (naujesnėse Java versijose) žodžiai, sudaryti vien iš ne-ASCII raidžių (pvz., „šį“), gali būti neatpažinti.
- **Datos ir telefono numeriai** (pvz., `1990-05-12`) skaičiuojami kaip keli skaičiai.
- **Istorija nesaugoma** po programos uždarymo (tik runtime).

## Kūrimo eiga

Projektas sukurtas naudojant dirbtinio intelekto įrankius pagal praktinio darbo reikalavimus:

1. **Generavimas.** Reikalavimai patikslinti `/grill-me` etapu, tada sugeneruotas projektas (planas: `.aistudio/artifacts/brain/.../implementation_plan.md`).
2. **Code review.** Atskira AI sesija peržiūrėjo kodą; įgyvendinti du aukščiausio prioriteto pakeitimai.
3. **Dokumentacija.** Šis README sugeneruotas atskiroje AI sesijoje.
4. **GitHub.** Repozitorija paskelbta per atskirą sesiją.
