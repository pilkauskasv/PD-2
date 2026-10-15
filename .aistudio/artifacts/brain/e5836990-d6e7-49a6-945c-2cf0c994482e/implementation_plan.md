# Teksto Metrikų Skaičiuoklė (MIPT Praktikos Darbas #2)

Švari, moderni „Android“ (Jetpack Compose) programėlė, skirta vartotojo įvesto teksto analizei: sakinių, žodžių, rašybos ženklų (simbolių) ir skaičių kiekiui apskaičiuoti su atliktų skaičiavimų istorija bei testinių sakinių generatoriumi.

## Vartotojo patvirtinti pasirinkimai

> [!IMPORTANT]
> Pagal atsakymus į /grill-me patikslinamuosius klausimus patvirtintos šios taisyklės:
> - **Rašybos ženklai (simboliai)**: Skaičiuojamas bendras visų simbolių skaičius su tarpais (kaip nurodyta sąlygos pavyzdyje: „Labas rytas“ => 11 ženklų; „Rugsėjo 1 prasidėjo karas. Liūdna“ => 33 ženklai). Papildomai pateikiama išsami statistika (simboliai be tarpų, skyrybos ženklai).
> - **Skaičių skaičius**: Skaitmenų sekos/grupės traktuojamos kaip atskiri skaičiai (pvz., „1“, „25“, „2024“).
> - **Testinių sakinių generavimas**: Pateikiamas patogus greito užpildymo meniu su paruoštais lietuviškais pavyzdžiais (įskaitant praktikos sąlygos pavyzdžius) bei atsitiktinio parinkimo galimybe.

---

## 1. Apžvalga ir pagrindinė koncepcija

- **Paskirtis**: Teksto metrikų analizė realiame laike su vienu paspaudimu ir išsamia ataskaita.
- **Pagrindiniai rodikliai**:
  1. **Sakinių skaičius**: taisyklingas sakinių skaidymas pagal sakinio pabaigos ženklus (`.`, `!`, `?`, `…`), atsižvelgiant į trumpinius.
  2. **Žodžių skaičius**: tekstinių žodžių atpažinimas (ignoruojant grynuosius skaičius).
  3. **Rašybos ženklų skaičius**: bendras simbolių ilgis (11 ženklų „Labas rytas“ atveju) bei papildoma skyrybos ženklų metrika.
  4. **Skaičių skaičius**: skaičių kiekis tekste (pvz., „Rugsėjo 1 prasidėjo karas“ turi 1 skaičių).
- **Istorija (runtime)**: Atliktų skaičiavimų sąrašas einamosios sesijos metu su formatu: `„Tekstas“ => sakiniai, žodžiai, ženklai, skaičiai`. Galimybė vėl paspausti ant istorijos elemento ir įkelti jį atgal į redaktorių arba išvalyti istoriją.
- **Įvesties tikrinimas**: Jeigu įvesties laukas tuščias ar sudarytas tik iš tarpų – vartotojas informuojamas aiškiu perspėjimu (klaidos pranešimu po laukeliu arba Snackbar).
- **Lokalizacija**: Visi tekstai ir etiketės saugomi `res/values/strings.xml` su paruošta lietuvių (LT) ir anglų (EN) kalbų struktūra.

---

## 2. Vartotojo patirtis ir vizualinis dizainas

### Vizualinė kryptis ir stilius
- **Stilius**: Minimalistinis, švarus, analitinis Material Design 3 stilius su aiškia metrikų kortelių hierarchija.
- **Spalvų paletė**: Gilus indigo/safyro akcentas (`#1E3A8A` / `#3B82F6`), švelnūs neutralūs paviršiai, kontrastingi indikatoriai.
- **Išdėstymas**:
  - Viršutinė antraštė su programėlės pavadinimu ir greito pavyzdžio mygtuku.
  - Didelis patogus teksto įvesties laukas su simbolių skaitikliu, išvalymo ir testinių pavyzdžių įkėlimo mygtukais.
  - 4 išryškintos metrikų kortelės (Sakiniai, Žodžiai, Simboliai, Skaičiai) su vizualiomis piktogramomis ir skaitinėmis reikšmėmis.
  - Skaičiavimų istorijos kortelių sąrašas su laiko žyme, originaliu tekstu ir rodikliais `[S, Ž, R, Sk]`.

### Vartotojo eiga
1. Vartotojas atidaro programėlę – mato tuščią įvesties lauką su patarimu arba gali paspausti „Pavyzdinis sakinys“.
2. Paspaudus „Skaičiuoti“ esant tuščiam laukui – parodomas pranešimas „Įveskite tekstą analizei“.
3. Įvedus tekstą ir paspaudus „Skaičiuoti metrikas“:
   - Apskaičiuojamos 4 metrikos.
   - Rezultatas įrašomas į runtime istorijos sąrašą.
   - Rezultatų kortelės animuotai atsinaujina.
4. Paspaudus ant bet kurio istorijos įrašo – tekstas įkeliamas į įvesties laukelį pakartotinei analizei ar redagavimui.

---

## 3. Techninė architektūra ir duomenų strategija

### Sistemos komponentų diagrama

```
┌────────────────────────────────────────────────────────┐
│                   MainActivity                         │
│             (Jetpack Compose UI & M3)                  │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                  TextMetricsViewModel                  │
│       - uiState: StateFlow<TextMetricsUiState>         │
│       - onTextChanged(), onCalculate(), onClear()       │
│       - onLoadSampleText(), onSelectHistoryItem()      │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│            TextMetricsAnalyzer (Atskiras modulis)      │
│  Perpanaudojama grynoji logika be UI priklausomybių:   │
│  - countSentences(text: String): Int                   │
│  - countWords(text: String): Int                       │
│  - countCharacters(text: String): Int                  │
│  - countNumbers(text: String): Int                     │
│  - analyze(text: String): TextMetricsResult            │
└────────────────────────────────────────────────────────┘
```

### Modulių atsakomybės
1. **`TextMetricsAnalyzer.kt`**:
   - Visiškai atskirta, izoliuota klasė/objektas, neturinti jokių Android UI priklausomybių.
   - Gali būti lengvai testuojama vienetiniais testais (Unit Tests) ir perpanaudojama kituose moduliuose ar projektuose.
   - Tikslus sakinių skaidymas, atpažįstantis daugtaškius, šauktukus, klaustukus.
   - Tikslus žodžių ir skaičių atskyrimas: pvz. „Rugsėjo 1 prasidėjo karas. Liūdna“ => žodžiai: 4, skaičiai: 1, ženklai: 33, sakiniai: 2.
2. **`TextMetricsViewModel.kt`**:
   - Valdo įvestą tekstą, klaidos būseną, aktyvius rezultatus ir istorijos įrašų sąrašą (`List<HistoryRecord>`).
3. **`TextMetricsScreen.kt`**:
   - Compose ekranas su M3 komponentais: `OutlinedTextField`, `ElevatedCard`, `Button`, `LazyColumn`.
   - Visi tekstai paimami per `stringResource(R.string...)`.
4. **`strings.xml`**:
   - Pilnavertė lokalizacija, atitinkanti 6-ąjį reikalavimą („Grafinių elementų tekstas turi būti lengvai lokalizuojamas“).

---

## 4. Reikalavimų atitikties patikra

- [x] **Sakinių skaičius**: realizuojama `TextMetricsAnalyzer.countSentences`.
- [x] **Žodžių skaičius**: realizuojama `TextMetricsAnalyzer.countWords`.
- [x] **Rašybos ženklų skaičius**: bendras simbolių skaičius (patvirtinta Phase 1 metu).
- [x] **Skaičių skaičius**: realizuojama `TextMetricsAnalyzer.countNumbers`.
- [x] **Testavimo sakinių generavimas**: pavyzdžių rinkinys (įskaitant užduoties pavyzdžius) + greitas atsitiktinis pavyzdys.
- [x] **Skaičiavimų istorija (runtime)**: saugoma ViewModel būsenoje su formatu `„Tekstas“ => S, Ž, R, Sk`.
- [x] **Tuščio lauko validacija**: įspėjimas vartotojui.
- [x] **Atskiras modulis/klasė**: `TextMetricsAnalyzer` nepriklauso nuo UI.
- [x] **Tekstų lokalizavimas**: visi elementai `strings.xml`.
