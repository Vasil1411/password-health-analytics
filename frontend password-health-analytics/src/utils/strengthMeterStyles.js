export const getMeterStyle = (category) => {
  switch (category) {
    case 'WEAK':
      return { width: '25%', color: 'var(--status-weak)', label: 'Слаба' };
    case 'MODERATE':
      return { width: '50%', color: 'var(--status-moderate)', label: 'Средна' };
    case 'STRONG':
      return { width: '80%', color: 'var(--status-strong)', label: 'Силна' };
    case 'VERY_STRONG':
      return { width: '100%', color: 'var(--status-strong)', label: 'Много силна' };
    case 'CRITICAL_BREACH':
      return { width: '100%', color: 'var(--status-critical)', label: 'Компрометирана' };
    default:
      return { width: '0%', color: 'var(--text-muted)', label: 'Изчакване...' };
  }
};