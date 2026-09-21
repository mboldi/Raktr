export function isEan8Compliant(barcode: string): boolean {
  if (!/^\d{8}$/.test(barcode)) {
    return false;
  }

  const digits = barcode.split('').map(Number);
  const sum = digits
    .slice(0, 7)
    .reduce((acc, digit, index) => acc + digit * (index % 2 === 0 ? 3 : 1), 0);
  const checkDigit = (10 - (sum % 10)) % 10;

  return checkDigit === digits[7];
}

// A barcode that happens to look like a valid EAN-8 might actually be an unrelated
// 8-digit code, so the first 7 digits are tried before falling back to the full value.
export function barcodeLookupCandidates(scannedValue: string): string[] {
  if (isEan8Compliant(scannedValue)) {
    return [scannedValue.slice(0, 7), scannedValue];
  }

  return [scannedValue];
}

export function findByBarcode<T>(
  items: T[],
  getBarcode: (item: T) => string,
  scannedValue: string,
): T | undefined {
  for (const candidate of barcodeLookupCandidates(scannedValue)) {
    const match = items.find((item) => getBarcode(item) === candidate);
    if (match) {
      return match;
    }
  }

  return undefined;
}
