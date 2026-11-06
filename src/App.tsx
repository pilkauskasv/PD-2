import React, { useState } from 'react';
import { TopAppBar } from './components/TopAppBar';
import { InputCard } from './components/InputCard';
import { MetricsResultCard } from './components/MetricsResultCard';
import { HistoryList } from './components/HistoryList';
import { SamplesModal } from './components/SamplesModal';
import { TextMetricsAnalyzer } from './analyzer/TextMetricsAnalyzer';
import { TextMetricsResult, HistoryRecord } from './types';

const SAMPLE_SENTENCES: string[] = [
  'Labas rytas',
  'Rugsėjo 1 prasidėjo karas. Liūdna',
  'Vilnius įkurtas 1323 metais, o Kaunas minimas nuo 1361 m.',
  'Kiek kainuoja 2 obuoliai ir 15 apelsinų? Iš viso 17 eurų!',
  'Ar šiandien lis? Galbūt. Bet pasiimk skėtį!',
  'MIPT praktikos darbas #2: teksto metrikų skaičiavimas veikia puikiai.',
];

export const App: React.FC = () => {
  const [inputText, setInputText] = useState<string>('');
  const [currentResult, setCurrentResult] = useState<TextMetricsResult | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [history, setHistory] = useState<HistoryRecord[]>([]);
  const [isSampleModalOpen, setIsSampleModalOpen] = useState<boolean>(false);

  const handleTextChanged = (text: string) => {
    setInputText(text);
    if (text.trim().length > 0 && errorMessage) {
      setErrorMessage(null);
    }
  };

  const handleCalculate = () => {
    if (!inputText.trim()) {
      setErrorMessage('Įvesties laukas yra tuščias! Įveskite tekstą skaičiavimui.');
      setCurrentResult(null);
      return;
    }

    const result = TextMetricsAnalyzer.analyze(inputText);
    const newRecord: HistoryRecord = {
      id: `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`,
      text: inputText,
      result,
      timestamp: Date.now(),
    };

    setCurrentResult(result);
    setErrorMessage(null);
    setHistory((prev) => [newRecord, ...prev]);
  };

  const handleLoadSample = (sample: string) => {
    const result = TextMetricsAnalyzer.analyze(sample);
    const newRecord: HistoryRecord = {
      id: `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`,
      text: sample,
      result,
      timestamp: Date.now(),
    };

    setInputText(sample);
    setCurrentResult(result);
    setErrorMessage(null);
    setHistory((prev) => [newRecord, ...prev]);
    setIsSampleModalOpen(false);
  };

  const handleRandomSample = () => {
    const randomIndex = Math.floor(Math.random() * SAMPLE_SENTENCES.length);
    handleLoadSample(SAMPLE_SENTENCES[randomIndex]);
  };

  const handleSelectHistoryItem = (record: HistoryRecord) => {
    setInputText(record.text);
    setCurrentResult(record.result);
    setErrorMessage(null);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleClearInput = () => {
    setInputText('');
    setCurrentResult(null);
    setErrorMessage(null);
  };

  const handleClearHistory = () => {
    setHistory([]);
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col">
      <TopAppBar
        onRandomSample={handleRandomSample}
        onOpenSamples={() => setIsSampleModalOpen(true)}
      />

      <main className="flex-1 max-w-4xl w-full mx-auto p-4 sm:p-6 space-y-5">
        <InputCard
          inputText={inputText}
          errorMessage={errorMessage}
          onTextChanged={handleTextChanged}
          onCalculate={handleCalculate}
          onClear={handleClearInput}
          onOpenSamples={() => setIsSampleModalOpen(true)}
        />

        {currentResult && <MetricsResultCard result={currentResult} />}

        <HistoryList
          history={history}
          onSelectRecord={handleSelectHistoryItem}
          onClearHistory={handleClearHistory}
        />
      </main>

      <footer className="py-4 text-center text-xs text-slate-400 border-t border-slate-200/60 mt-auto">
        Teksto metrikų skaičiuoklė &bull; MIPT Praktikos Darbas #2
      </footer>

      <SamplesModal
        isOpen={isSampleModalOpen}
        samples={SAMPLE_SENTENCES}
        onSelectSample={handleLoadSample}
        onClose={() => setIsSampleModalOpen(false)}
      />
    </div>
  );
};

export default App;
