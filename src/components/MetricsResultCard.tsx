import React from 'react';
import { AlignLeft, Type, Hash, Binary } from 'lucide-react';
import { TextMetricsResult, toSummaryString } from '../types';

interface MetricsResultCardProps {
  result: TextMetricsResult;
}

export const MetricsResultCard: React.FC<MetricsResultCardProps> = ({ result }) => {
  return (
    <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm p-4 sm:p-6 space-y-4">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
        <h2 className="text-base font-bold text-blue-900">
          Apskaičiuotos metrikos
        </h2>
        <span className="inline-flex items-center px-3 py-1 text-xs font-bold text-blue-800 bg-blue-100 rounded-lg w-fit">
          Formatas: {toSummaryString(result)}
        </span>
      </div>

      <div className="grid grid-cols-2 gap-3 sm:gap-4">
        <div className="p-3.5 sm:p-4 rounded-xl bg-blue-50/60 border border-blue-100 flex flex-col justify-between space-y-2">
          <div className="flex items-center justify-between text-slate-700">
            <span className="text-xs sm:text-sm font-semibold">Sakiniai</span>
            <AlignLeft className="w-4 h-4 text-blue-600" />
          </div>
          <div className="text-2xl sm:text-3xl font-extrabold text-blue-600">
            {result.sentencesCount}
          </div>
          <div className="text-xs text-slate-500 font-medium truncate">
            Sakinių skaičius
          </div>
        </div>

        <div className="p-3.5 sm:p-4 rounded-xl bg-teal-50/60 border border-teal-100 flex flex-col justify-between space-y-2">
          <div className="flex items-center justify-between text-slate-700">
            <span className="text-xs sm:text-sm font-semibold">Žodžiai</span>
            <Type className="w-4 h-4 text-teal-600" />
          </div>
          <div className="text-2xl sm:text-3xl font-extrabold text-teal-600">
            {result.wordsCount}
          </div>
          <div className="text-xs text-slate-500 font-medium truncate">
            Žodžių skaičius
          </div>
        </div>

        <div className="p-3.5 sm:p-4 rounded-xl bg-purple-50/60 border border-purple-100 flex flex-col justify-between space-y-2">
          <div className="flex items-center justify-between text-slate-700">
            <span className="text-xs sm:text-sm font-semibold">Rašybos ženklai</span>
            <Hash className="w-4 h-4 text-purple-600" />
          </div>
          <div className="text-2xl sm:text-3xl font-extrabold text-purple-600">
            {result.charactersCount}
          </div>
          <div className="text-xs text-slate-500 font-medium truncate">
            Simbolių su tarpais
          </div>
        </div>

        <div className="p-3.5 sm:p-4 rounded-xl bg-amber-50/60 border border-amber-100 flex flex-col justify-between space-y-2">
          <div className="flex items-center justify-between text-slate-700">
            <span className="text-xs sm:text-sm font-semibold">Skaičiai</span>
            <Binary className="w-4 h-4 text-amber-600" />
          </div>
          <div className="text-2xl sm:text-3xl font-extrabold text-amber-600">
            {result.numbersCount}
          </div>
          <div className="text-xs text-slate-500 font-medium truncate">
            Skaičių kiekis
          </div>
        </div>
      </div>

      <div className="flex flex-wrap gap-2 pt-1 border-t border-slate-100">
        <span className="inline-flex items-center px-3 py-1 text-xs font-medium text-slate-700 bg-slate-100 rounded-full">
          Be tarpų: {result.charactersWithoutSpacesCount}
        </span>
        <span className="inline-flex items-center px-3 py-1 text-xs font-medium text-slate-700 bg-slate-100 rounded-full">
          Skyrybos ženklai: {result.punctuationCount}
        </span>
      </div>
    </div>
  );
};
