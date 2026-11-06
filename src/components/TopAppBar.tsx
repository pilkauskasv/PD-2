import React from 'react';
import { Sparkles, ListFilter } from 'lucide-react';

interface TopAppBarProps {
  onRandomSample: () => void;
  onOpenSamples: () => void;
}

export const TopAppBar: React.FC<TopAppBarProps> = ({ onRandomSample, onOpenSamples }) => {
  return (
    <header className="bg-white border-b border-slate-200/80 sticky top-0 z-30 shadow-xs">
      <div className="max-w-4xl mx-auto px-4 py-3 sm:px-6 flex items-center justify-between">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 flex items-center gap-2">
            <span>Teksto metrikų skaičiuoklė</span>
          </h1>
          <p className="text-xs sm:text-sm font-medium text-slate-500">
            MIPT praktikos darbas #2
          </p>
        </div>

        <div className="flex items-center gap-1.5 sm:gap-2">
          <button
            type="button"
            onClick={onRandomSample}
            title="Atsitiktinis sakinys"
            className="p-2 text-blue-700 hover:bg-blue-50 rounded-xl transition-colors cursor-pointer flex items-center gap-1.5 text-xs font-semibold sm:px-3"
          >
            <Sparkles className="w-5 h-5 text-blue-600" />
            <span className="hidden sm:inline">Atsitiktinis sakinys</span>
          </button>

          <button
            type="button"
            onClick={onOpenSamples}
            title="Testiniai pavyzdžiai"
            className="p-2 text-blue-700 hover:bg-blue-50 rounded-xl transition-colors cursor-pointer flex items-center gap-1.5 text-xs font-semibold sm:px-3"
          >
            <ListFilter className="w-5 h-5 text-blue-600" />
            <span className="hidden sm:inline">Pavyzdžiai</span>
          </button>
        </div>
      </div>
    </header>
  );
};
