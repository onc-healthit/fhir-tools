# ndjson-to-json-text [![Build Status](https://travis-ci.org/azu/ndjson-to-json-text.svg?branch=master)](https://travis-ci.org/azu/ndjson-to-json-text)

Convert ndjson text to JSON text without JSON parsing.

This library convert ndjson text to json text.

```
{"id":1,"name":"Alice"}
{"id":2,"name":"Bob"}
{"id":3,"name":"Carol"}
```

to

```json
[{"id":1,"name":"Alice"},{"id":2,"name":"Bob"},{"id":3,"name":"Carol"}]
```

## Purpose

- Just convert ndjson(text file) to json(text file)
    - You should `JSON.parse` the result json text outside of this library
- No use `JSON.parse` in this library
    - It is a cost

## Install

Install with [npm](https://www.npmjs.com/):

    npm install ndjson-to-json-text

## Usage

```ts
/**
 * Convert ndjson text to JSON text
 * The return value is a string of JSON array text
 * @param ndjsonText
 */
export declare function ndjsonToJsonText(ndjsonText: string): string;
```

## Example

```js
const jsonText = ndjsonToJsonText(
`{"id":1,"name":"Alice"}
{"id":2,"name":"Bob"}
{"id":3,"name":"Carol"}`)
console.log(jsonText);
// [{"id":1,"name":"Alice"},{"id":2,"name":"Bob"},{"id":3,"name":"Carol"}]
const json = JSON.parse(jsonText);
// actual json object!!
```

## Related

- [madnight/ndjson-to-json: Converts NDJSON to JSON](https://github.com/madnight/ndjson-to-json#readme)
    - It only work on Node.js

## Changelog

See [Releases page](https://github.com/azu/ndjson-to-json-text/releases).

## Running tests

Install devDependencies and Run `npm test`:

    npm test
    # Update snapshot
    npm run updateSnapshot

## Contributing

Pull requests and stars are always welcome.

For bugs and feature requests, [please create an issue](https://github.com/azu/ndjson-to-json-text/issues).

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## Author

- [github/azu](https://github.com/azu)
- [twitter/azu_re](https://twitter.com/azu_re)

## License

MIT © azu
