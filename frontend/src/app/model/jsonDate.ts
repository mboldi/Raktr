/**
 * The backend models acquisition and warranty dates as nullable LocalDate, so
 * both ends of the wire have to keep null as null. Going through `new Date()`
 * unguarded turns a missing date into 1970-01-01, which then gets written back
 * to the database as if the user had picked it.
 */

/** Parses a LocalDate off a JSON payload, leaving a missing or unparseable one as null. */
export function dateFromJson(value: unknown): Date | null {
  if (value === null || value === undefined || value === '') {
    return null;
  }

  const parsed = new Date(value as string);
  return isNaN(parsed.getTime()) ? null : parsed;
}

/** Serializes a Date as a LocalDate, leaving a missing one as null. */
export function dateToJson(value: Date | null): string | null {
  return value ? value.toISOString().split('T')[0] : null;
}
