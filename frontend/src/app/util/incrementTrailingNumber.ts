/** Increments the trailing run of digits in a string, keeping its zero-padded width
 * (e.g. "CAM-007" -> "CAM-008", "CAM-099" -> "CAM-100"). Values with no trailing digits
 * are returned unchanged. */
export function incrementTrailingNumber(value: string): string {
  const match = value.match(/^(.*?)(\d+)$/);
  if (!match) {
    return value;
  }

  const [, prefix, digits] = match;
  const incremented = (Number(digits) + 1).toString().padStart(digits.length, '0');

  return prefix + incremented;
}
