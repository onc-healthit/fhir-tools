/**
 * Convert ndjson text to JSON text
 * The return value is a string of JSON array text
 * @param ndjsonText
 */
export function ndjsonToJsonText(ndjsonText: string): string {
    // Remove First and Last empty for avoid to add , in invalid position
    // For example, JSON.parse does not parse [1,2,3,]
    const linesWithComma = ndjsonText.trim()
    // NDJSON may skip empty lines -> handle mulitple new lines as a single new line
    // https://github.com/ndjson/ndjson-spec#32-parsing
    .replace(/(!?\r?\n)+/g, ",");
    return `[${linesWithComma}]`;
}