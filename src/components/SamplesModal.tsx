import React, { useEffect } from 'react';
import { ListFilter, X } from 'lucide-react';

interface SamplesModalProps {
  isOpen: boolean;
  samples: string[];
  onSelectSample: (sample: string) => void;
  onClose: () => void;
}

export const SamplesModal: React.FC<SamplesModalProps> = ({
  isOpen,
  samples,
  onSelectSample,
  onClose,
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };
    if (isOpen) {
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div
      role="dialog"
      aria-modal="true"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs"
    >
      <div
        className="bg-white rounded-2xl max-w-lg w-full border border-slate-200 shadow-xl overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100">
          <div className="flex items-center gap-2 text-blue-900 font-bold text-base">
            <ListFilter className="w-5 h-5 text-blue-600" />
            <span>Testinių sakinių pavyzdžiai</span>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-4 space-y-2 max-h-[60vh] overflow-y-auto">
          {samples.map((sample, index) => (
            <button
              key={index}
              type="button"
              onClick={() => onSelectSample(sample)}
              className="w-full text-left p-3.5 rounded-xl border border-slate-200/90 hover:border-blue-500 hover:bg-blue-50/40 transition-all cursor-pointer font-medium text-slate-800 text-sm"
            >
              {sample}
            </button>
          ))}
        </div>

        <div className="px-5 py-3.5 bg-slate-50 border-t border-slate-100 flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-200/70 rounded-xl transition-colors cursor-pointer"
          >
            Uždaryti
          </button>
        </div>
      </div>
    </div>
  );
};
