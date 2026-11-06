export interface TextMetricsResult {
  sentencesCount: number;
  wordsCount: number;
  charactersCount: number;
  numbersCount: number;
  charactersWithoutSpacesCount: number;
  punctuationCount: number;
}

export function toSummaryString(result: TextMetricsResult): string {
  return `${result.sentencesCount}, ${result.wordsCount}, ${result.charactersCount}, ${result.numbersCount}`;
}

export interface HistoryRecord {
  id: string;
  text: string;
  result: TextMetricsResult;
  timestamp: number;
}
