import React from 'react';
import { Calculator, ListFilter, X, AlertTriangle } from 'lucide-react';

interface InputCardProps {
  inputText: string;
  errorMessage: string | null;
  onTextChanged: (text: string) => void;
  onCalculate: () => void;
  onClear: () => void;
  onOpenSamples: () => void;
}

export const InputCard: React.FC<InputCardProps> = ({
  inputText,
  errorMessage,
  onTextChanged,
  onCalculate,
  onClear,
  onOpenSamples,
}) => {
  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
      e.preventDefault();
      onCalculate();
    }
  };

  return (
    <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm p-4 sm:p-6 space-y-4">
      <div className="flex items-center justify-between">
        <label
          htmlFor="text_input_field"
          className="text-sm font-bold text-blue-900 tracking-wide uppercase flex items-center gap-2"
        >
          Teksto įvestis
        </label>
        <span className="px-2.5 py-1 text-xs font-semibold bg-slate-100 text-slate-700 rounded-lg border border-slate-200">
          {inputText.length} Simbolių su tarpais
        </span>
      </div>

      <div className="relative">
        <textarea
          id="text_input_field"
          value={inputText}
          onChange={(e) => onTextChanged(e.target.value)}
          onKeyDown={handleKeyDown}
          rows={4}
          placeholder="Įveskite arba įklijuokite tekstą analizei..."
          className={`w-full p-3.5 pr-10 text-slate-900 placeholder-slate-400 bg-white border rounded-xl text-base resize-y transition-all focus:outline-none focus:ring-2 ${
            errorMessage
              ? 'border-red-500 focus:ring-red-500/20 focus:border-red-500'
              : 'border-slate-300 focus:ring-blue-600/20 focus:border-blue-600'
          }`}
        />

        {inputText.length > 0 && (
          <button
            type="button"
            onClick={onClear}
            title="Išvalyti tekstą"
            className="absolute top-3 right-3 p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        )}
      </div>

      {errorMessage && (
        <div className="flex items-center gap-2 text-sm text-red-600 font-medium bg-red-50 px-3.5 py-2.5 rounded-xl border border-red-200/80">
          <AlertTriangle className="w-4 h-4 shrink-0 text-red-600" />
          <span>{errorMessage}</span>
        </div>
      )}

      <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 pt-1">
        <button
          type="button"
          onClick={onCalculate}
          className="flex-1 inline-flex items-center justify-center gap-2 px-5 py-3 rounded-xl bg-blue-700 hover:bg-blue-800 active:bg-blue-900 text-white font-semibold text-sm shadow-xs transition-all cursor-pointer"
        >
          <Calculator className="w-4 h-4" />
          <span>Skaičiuoti metrikas</span>
        </button>

        <button
          type="button"
          onClick={onOpenSamples}
          className="inline-flex items-center justify-center gap-2 px-4 py-3 rounded-xl border border-slate-300 hover:bg-slate-50 text-slate-700 font-semibold text-sm transition-all cursor-pointer"
        >
          <ListFilter className="w-4 h-4 text-slate-600" />
          <span>Testiniai pavyzdžiai</span>
        </button>
      </div>
    </div>
  );
};
