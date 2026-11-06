import React from 'react';
import { History, Trash2 } from 'lucide-react';
import { HistoryRecord, toSummaryString } from '../types';

interface HistoryListProps {
  history: HistoryRecord[];
  onSelectRecord: (record: HistoryRecord) => void;
  onClearHistory: () => void;
}

export const HistoryList: React.FC<HistoryListProps> = ({
  history,
  onSelectRecord,
  onClearHistory,
}) => {
  const formatTime = (timestamp: number) => {
    const d = new Date(timestamp);
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
  };

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <History className="w-5 h-5 text-blue-700" />
          <h2 className="text-base font-bold text-slate-800">
            Skaičiavimų istorija (runtime)
          </h2>
        </div>

        {history.length > 0 && (
          <button
            type="button"
            onClick={onClearHistory}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-rose-600 hover:text-rose-700 hover:bg-rose-50 rounded-lg transition-colors cursor-pointer"
          >
            <Trash2 className="w-3.5 h-3.5" />
            <span>Valyti istoriją</span>
          </button>
        )}
      </div>

      {history.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200/70 p-8 text-center space-y-2 shadow-2xs">
          <History className="w-9 h-9 text-slate-400 mx-auto" />
          <p className="text-sm text-slate-500">
            Istorija tuščia. Atlikite skaičiavimą arba pasirinkite testinį sakinį.
          </p>
        </div>
      ) : (
        <div className="space-y-2.5">
          {history.map((record, index) => (
            <div
              key={record.id || index}
              onClick={() => onSelectRecord(record)}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  onSelectRecord(record);
                }
              }}
              className="bg-white rounded-xl border border-slate-200/90 hover:border-blue-400 hover:shadow-sm p-3.5 transition-all cursor-pointer text-left"
            >
              <div className="flex items-start justify-between gap-2">
                <span className="text-sm font-semibold text-slate-900 line-clamp-2">
                  „{record.text}“
                </span>
                <span className="text-xs font-medium text-slate-400 shrink-0">
                  {formatTime(record.timestamp)}
                </span>
              </div>

              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mt-2 pt-2 border-t border-slate-100">
                <div className="text-xs font-bold text-blue-700">
                  =&gt; {toSummaryString(record.result)}
                </div>

                <div className="flex items-center gap-1.5 flex-wrap">
                  <span className="px-2 py-0.5 text-xs font-medium bg-slate-100 text-slate-700 rounded-md">
                    S: {record.result.sentencesCount}
                  </span>
                  <span className="px-2 py-0.5 text-xs font-medium bg-slate-100 text-slate-700 rounded-md">
                    Ž: {record.result.wordsCount}
                  </span>
                  <span className="px-2 py-0.5 text-xs font-medium bg-slate-100 text-slate-700 rounded-md">
                    R: {record.result.charactersCount}
                  </span>
                  <span className="px-2 py-0.5 text-xs font-medium bg-slate-100 text-slate-700 rounded-md">
                    Sk: {record.result.numbersCount}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
