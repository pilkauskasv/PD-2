import { TextMetricsResult } from '../types';

const WORD_REGEX = /(?:(?<=^|[^\p{L}\p{M}]))[\p{L}\p{M}]+(?:[-'’][\p{L}\p{M}]+)*(?=[^\p{L}\p{M}]|$)/gu;
const NUMBER_REGEX = /(?:(?<=^|[^\p{L}\p{M}\d]))\d+(?:[.,]\d+)*(?=[^\p{L}\p{M}\d]|$)/gu;
const SENTENCE_DELIMITER_REGEX = /[.!?…]+|[\r\n]+/g;
const PUNCTUATION_REGEX = /[\p{P}\p{S}]/gu;

export const TextMetricsAnalyzer = {
  countSentences(text: string): number {
    const trimmed = text.trim();
    if (!trimmed) return 0;
    const sentences = trimmed
      .split(SENTENCE_DELIMITER_REGEX)
      .map((s) => s.trim())
      .filter((s) => s.length > 0);
    return sentences.length === 0 ? 1 : sentences.length;
  },

  countWords(text: string): number {
    if (!text || !text.trim()) return 0;
    const matches = text.match(WORD_REGEX);
    return matches ? matches.length : 0;
  },

  countCharacters(text: string): number {
    return text.length;
  },

  countNumbers(text: string): number {
    if (!text || !text.trim()) return 0;
    const matches = text.match(NUMBER_REGEX);
    return matches ? matches.length : 0;
  },

  countCharactersWithoutSpaces(text: string): number {
    return text.replace(/\s/g, '').length;
  },

  countPunctuation(text: string): number {
    const matches = text.match(PUNCTUATION_REGEX);
    return matches ? matches.length : 0;
  },

  analyze(text: string): TextMetricsResult {
    return {
      sentencesCount: this.countSentences(text),
      wordsCount: this.countWords(text),
      charactersCount: this.countCharacters(text),
      numbersCount: this.countNumbers(text),
      charactersWithoutSpacesCount: this.countCharactersWithoutSpaces(text),
      punctuationCount: this.countPunctuation(text),
    };
  },
};
